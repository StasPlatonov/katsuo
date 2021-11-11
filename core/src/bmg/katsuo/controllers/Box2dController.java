package bmg.katsuo.controllers;

import bmg.katsuo.Globals;
import bmg.katsuo.managers.Material;
import bmg.katsuo.managers.MaterialManager;
import bmg.katsuo.objects.GameObjectController;
import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import static bmg.katsuo.objects.Types.*;

public class Box2dController extends GameObjectController
{
    private Box2dPhysicsSystem ThePhysics;
    private Body TheBody;
    private Rectangle TheBodyAABB  = new Rectangle();
    private boolean IsDynamic;
    private boolean IsKinematic;
    private boolean IsSensor;

    private boolean PhysicalMovement;
    private float OffsetX, OffsetY;

    private float PrevDamping;
    private float LadderDamping = 1f;
    private boolean UseCachedBody = false;
    //-------------------------------------------------------------------------------------------------------------------------

    public Box2dController(Box2dPhysicsSystem physics)
    {
        ThePhysics = physics;
        IsSensor = false;
        PhysicalMovement = false;
        OffsetX = OffsetY = 0f;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(GameObject object)
    {
        //Gdx.app.log("Box2dController", "Init for object " + object.getName());
        assert(TheBody == null);

        object.InitPhysics(this);

        LadderDamping = object.GetProperties().get("ladder_damping", 1f, Float.class);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Box2dPhysicsSystem GetPhysics()
    {
        return ThePhysics;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void dispose()
    {
        DisposeWorld();
    }

    public void DisposeWorld()
    {
        if (ThePhysics != null)
        {
            Gdx.app.log("Box2dController", "Dispose");
            SetBody(null);
            ThePhysics = null;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetBody(Body body)
    {
        if (this.TheBody != null)
        {
            ThePhysics.DestroyBody(this.TheBody);
        }
        this.TheBody = body;
    }

    public Body GetBody()
    {
        return TheBody;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean IsMoving()
    {
        if (TheBody == null)
        {
            return false;
        }
        return (TheBody.getLinearVelocity().len2() != 0);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetMass()
    {
        return TheBody.getMass();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Vector2 GetPosition()
    {
        return TheBody.getPosition();
    }

    public void SetPosition(float px, float py)
    {
        TheBody.setTransform(px, py, TheBody.getAngle());
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Vector2 GetSpeed()
    {
        return TheBody.getLinearVelocity();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Remove(GameObject object)
    {
        DisposeWorld();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static float[] ProcessVertices(float [] vertices, float x, float y, float width, float height, boolean flipX, boolean flipY)
    {
        float[] transformedVertices = new float[vertices.length];
        for (int i = 0; i < vertices.length; i+=2)
        {
            transformedVertices[i] = x + (flipX ? (width - vertices[i]) : vertices[i]) ;
            transformedVertices[i + 1] = y + (flipY ? (height - vertices[i + 1]) : vertices[i + 1]) ;
        }
        return transformedVertices;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private static Shape GetShapeFromMapObject(MapObject mapObject, float width, float height, boolean flipX, boolean flipY, float sx, float sy, float rotation)
    {
        if (mapObject instanceof PolygonMapObject)
        {
            PolygonMapObject tilePolygon = (PolygonMapObject) mapObject;
            Polygon polygon = tilePolygon.getPolygon();
            final float [] vertices = polygon.getTransformedVertices();
            final float [] transformedVertices = ProcessVertices(vertices, 0f, 0f, width, height, flipX, flipY);

            // Transform to physics space
            Vector2[] worldVerticies = new Vector2[transformedVertices.length / 2];
            for (int i = 0; i < worldVerticies.length; ++i)
            {
                worldVerticies[i] = new Vector2((transformedVertices[i * 2] - width * .5f) * Globals.PIXELS_TO_METERS, (transformedVertices[i * 2 + 1] - height * .5f) * sy * Globals.PIXELS_TO_METERS);
            }
            //@TODO: vertexes limit!
            if (worldVerticies.length <= 4)
            {
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.set(worldVerticies);

                return polygonShape;
            }
            else
            {
                ChainShape chainShape = new ChainShape();
                chainShape.createChain(worldVerticies);
                return chainShape;
            }
        }
        else if (mapObject instanceof EllipseMapObject)
        {
            EllipseMapObject ellObj = (EllipseMapObject) mapObject;
            Ellipse ellipse = ellObj.getEllipse();

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(Math.max(ellipse.width, ellipse.height) * .5f * Globals.PIXELS_TO_METERS);
            final Vector2 pos = new Vector2(
                    (flipX ? (width - ellipse.x) : ellipse.x) - width * .5f + ellipse.width * .5f,
                    (flipY ? (height - ellipse.y) : ellipse.y) - height * .5f + ellipse.height * .5f).scl(Globals.PIXELS_TO_METERS);
            circleShape.setPosition(pos);

            return circleShape;
        }
        else if (mapObject instanceof RectangleMapObject)
        {
            RectangleMapObject rectObj = (RectangleMapObject) mapObject;
            Rectangle rect = rectObj.getRectangle();

            // Points have zero width and height
            if ((rect.width == 0f) && (rect.height == 0f))
            {
                return null;
            }

            float [] vertices = new float[4 * 2];
            vertices[0] = rect.x * sx; vertices[1] = rect.y * sy;
            vertices[2] = rect.x * sx; vertices[3] = rect.y * sy + rect.height * sy;
            vertices[4] = rect.x * sx + rect.width * sx; vertices[5] = rect.y * sy+ rect.height * sy;
            vertices[6] = rect.x * sx + rect.width * sx; vertices[7] = rect.y * sy;

            final float [] transformedVertices = ProcessVertices(vertices, 0f, 0f, width, height, flipX, flipY);

            Vector2[] worldVerticies = new Vector2[vertices.length / 2];
            for (int i = 0; i < worldVerticies.length; ++i)
            {
                worldVerticies[i] = new Vector2(transformedVertices[i * 2], transformedVertices[i * 2 + 1]);

                worldVerticies[i].rotate(-rotation);
                worldVerticies[i].add(-width * .5f, -height * .5f);
                worldVerticies[i].rotate(rotation);

                worldVerticies[i].scl(Globals.PIXELS_TO_METERS);
            }

            PolygonShape rectShape = new PolygonShape();
            rectShape.set(worldVerticies);

            return rectShape;
        }

        return null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private static List<Shape> GetShapesFromTiledMapObject(GameObject object, MapObject mapObject)
    {
        List<Shape> result = new ArrayList<Shape>();

        if (mapObject instanceof TiledMapTileMapObject)
        {
            TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) mapObject;
            TiledMapTile tile = tiledObject.getTile();

            MapObjects tileObjects = tile.getObjects();
            boolean flipX = tiledObject.isFlipHorizontally();
            boolean flipY = tiledObject.isFlipVertically();

            float sx = tiledObject.getScaleX();
            float sy = tiledObject.getScaleY();
            float width = mapObject.getProperties().get("width", Float.class);
            float height = mapObject.getProperties().get("height", Float.class);

            float rotation = tiledObject.getRotation();

            if (tileObjects.getCount() > 0)
            {
                for (MapObject tileObj : tileObjects)
                {
                    Shape shape = GetShapeFromMapObject(tileObj, width, height, flipX, flipY, sx, sy, rotation);
                    if (shape != null)
                    {
                        result.add(shape);
                    }
                    else
                    {
                        Gdx.app.error("Box2DController", "ERROR: Failed to create shape from map object " + tileObj.getName());
                    }
                }
            }
        }
        return result;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static boolean CreateFixturesFromTileMapObjects(GameObject object, Body body, TiledMapTileMapObject tiledMapObject, boolean objIsSensor)
    {
        final MapProperties props = object.GetProperties();

        final int categoryBits = props.get("phys_category", (int)CATEGORY_DEFAULT, Integer.class);
        final int maskBits = props.get("phys_mask", (int)MASK_DEFAULT, Integer.class);
        final int groupIndex = props.get("phys_group_index", 0, Integer.class);
        float density = props.get("phys_density", 1.0f, Float.class);

        float friction = props.get("phys_friction", 1f, Float.class);
        float restitution = props.get("phys_restitution", 0.0f, Float.class);
        final String materialName = object.GetProperties().get("material", "", String.class);
        if (!materialName.isEmpty())
        {
            Material material = MaterialManager.GetMaterial(materialName);
            friction = material.GetFriction();
            restitution = material.GetRestitution();
        }

        boolean isSensor = tiledMapObject.getProperties().get("is_sensor", objIsSensor, Boolean.class);

        final List<Shape> shapes = GetShapesFromTiledMapObject(object, tiledMapObject);
        if (shapes.isEmpty())
        {
            return false;
        }

        for (Shape shape : shapes)
        {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            fixtureDef.filter.categoryBits = (short) categoryBits;
            fixtureDef.filter.maskBits = (short) maskBits;
            fixtureDef.filter.groupIndex = (short)groupIndex;
            fixtureDef.isSensor = isSensor;

            fixtureDef.shape = shape;

            body.createFixture(fixtureDef).setUserData(object);

            shape.dispose();
        }

        return true;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void CreateFixturesFromProperties(GameObject object, Body body, boolean objIsSensor)
    {
        MapProperties props = object.GetProperties();

        final int categoryBits = props.get("phys_category", (int)CATEGORY_DEFAULT, Integer.class);
        final int maskBits = props.get("phys_mask", (int)MASK_DEFAULT, Integer.class);
        final int groupIndex = props.get("phys_group_index", 0, Integer.class);

        IsSensor = props.get("phys_object_type", "", String.class).equals("trigger");

        float objPhysWidth = object.getWidth() * Globals.PIXELS_TO_METERS;
        float objPhysHalfWidth = objPhysWidth * .5f;
        float objPhysHeight = object.getHeight() * Globals.PIXELS_TO_METERS;
        float objPhysHalfHeight = objPhysHeight * .5f;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = props.get("phys_density", 1.0f, Float.class);
        fixtureDef.friction = props.get("phys_friction", 1f, Float.class);
        fixtureDef.restitution = props.get("phys_restitution", 0.0f, Float.class);
        final String materialName = object.GetProperties().get("material", "", String.class);
        if (!materialName.isEmpty())
        {
            Material material = MaterialManager.GetMaterial(materialName);
            fixtureDef.friction = material.GetFriction();
            fixtureDef.restitution = material.GetRestitution();
        }

        fixtureDef.filter.categoryBits = (short) categoryBits;
        fixtureDef.filter.maskBits = (short) maskBits;
        fixtureDef.filter.groupIndex = (short)groupIndex;
        fixtureDef.isSensor = objIsSensor;//IsSensor;

        final String shapeType = props.get("phys_shape", "", String.class);

        if (shapeType.equals("capsule"))
        {
            /*CircleShape circleShape = new CircleShape();
            circleShape.setRadius(objPhysHalfWidth);
            circleShape.setPosition(new Vector2(0, objPhysHalfHeight * .5f));
            fixtureDef.shape = circleShape;

            body.createFixture(fixtureDef).setUserData(object);
            circleShape.dispose();

            PolygonShape polyShape = new PolygonShape();
            polyShape.setAsBox(objPhysHalfWidth, objPhysHalfHeight);
            fixtureDef.shape = polyShape;
            body.createFixture(fixtureDef).setUserData(object);
            polyShape.dispose();

            circleShape = new CircleShape();
            circleShape.setRadius(objPhysHalfWidth * ThePhysics.PixelsToMeters());
            circleShape.setPosition(new Vector2(0, -objPhysHalfHeight * .5f));
            fixtureDef.shape = circleShape;
            body.createFixture(fixtureDef).setUserData(object);
            circleShape.dispose();*/
        }
        else // one shape bodies
        {
            Shape shape;
            if (shapeType.equals("circle"))
            {
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(Math.max(objPhysHalfWidth, objPhysHalfHeight));
                shape = circleShape;
                body.setAngularDamping(1.5f);
            }
            else //"box" or other
            {
                PolygonShape polyShape = new PolygonShape();
                polyShape.setAsBox(objPhysHalfWidth, objPhysHalfHeight);
                shape = polyShape;
            }

            fixtureDef.shape = shape;

            body.createFixture(fixtureDef).setUserData(object);

            shape.dispose();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void InitBaseObjectPhysics(GameObject object)
    {
        assert(ThePhysics != null);
        assert(TheBody == null);

        final MapProperties props = object.GetProperties();

        final int categoryBits = props.get("phys_category", (int)CATEGORY_DEFAULT, Integer.class);
        final int maskBits = props.get("phys_mask", (int)MASK_DEFAULT, Integer.class);
        final int groupIndex = props.get("phys_group_index", 0, Integer.class);
        //object.SetGroupId(categoryBits);
        //object.SetFilter(maskBits);

        final String link = props.get("phys_link", "", String.class);

        IsDynamic = props.containsKey("phys_object_type") && props.get("phys_object_type", String.class).equals("dynamic");
        IsKinematic = props.containsKey("phys_object_type") && props.get("phys_object_type", String.class).equals("kinematic");
        IsSensor = props.get("phys_object_type", "", String.class).equals("trigger");

        boolean neetTransform = true;

        if ((link != null) && !link.isEmpty()) // Create physics from external file (Physics Editor)
        {
            // scale physics according to object size
            float sX = object.getWidth() / Globals.PHYSICS_CACHE_IMAGE_SIZE;
            float sY = object.getHeight() / Globals.PHYSICS_CACHE_IMAGE_SIZE;

            TheBody = ThePhysics.CreateBodyFromCache(link, sX, sY);

            TheBodyAABB.set(Box2dPhysicsSystem.FitBodyToRect(TheBody));
            Box2dPhysicsSystem.SetBodyParams(TheBody, categoryBits, maskBits, groupIndex, object);

            UseCachedBody = true;
        }
        else
        {
            BodyDef bodyDef = new BodyDef();
            TheBody = ThePhysics.CreateBody(bodyDef);

            boolean tiledFixtures = false;
            // Create physics based on tile physics objects
            final MapObject mapObject = object.GetMapObject();
            if (mapObject != null)
            {
                // Some objects (e.g. turrets) has physics made in Tiled
                if (mapObject instanceof TiledMapTileMapObject)
                {
                    TiledMapTileMapObject tilemapObject = (TiledMapTileMapObject) mapObject;

                    IsSensor = tilemapObject.getProperties().get("is_sensor", IsSensor, Boolean.class);
                    IsDynamic = tilemapObject.getProperties().get("is_dynamic", IsDynamic, Boolean.class);

                    if (CreateFixturesFromTileMapObjects(object, TheBody, tilemapObject, IsSensor))
                    {
                        tiledFixtures = true;
                    }
                }
                else if (mapObject instanceof PolygonMapObject)
                {
                    float density = props.get("phys_density", 1.0f, Float.class);

                    float friction = props.get("phys_friction", 1f, Float.class);
                    float restitution = props.get("phys_restitution", 0.0f, Float.class);
                    final String materialName = object.GetProperties().get("material", "", String.class);
                    if (!materialName.isEmpty())
                    {
                        Material material = MaterialManager.GetMaterial(materialName);
                        friction = material.GetFriction();
                        restitution = material.GetRestitution();
                    }

                    IsSensor = mapObject.getProperties().get("is_sensor", false, Boolean.class);
                    IsDynamic = mapObject.getProperties().get("is_dynamic", false, Boolean.class);
                    IsKinematic = false;

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.density = density;
                    fixtureDef.friction = friction;
                    fixtureDef.restitution = restitution;
                    fixtureDef.filter.categoryBits = (short) categoryBits;
                    fixtureDef.filter.groupIndex = (short)groupIndex;
                    fixtureDef.filter.maskBits = (short) maskBits;
                    fixtureDef.isSensor = IsSensor;

                    Shape shape = GetShapeFromMapObject(mapObject, 0f, 0f, false, false, 1f, 1f, 0f);
                    fixtureDef.shape = shape;

                    TheBody.createFixture(fixtureDef).setUserData(object);

                    shape.dispose();

                    tiledFixtures = true;
                    neetTransform = false;
                }
            }

            if (!tiledFixtures)
            {
                CreateFixturesFromProperties(object, TheBody, IsSensor);
            }
        }

        TheBody.setUserData(object);

        TheBody.setFixedRotation(props.get("phys_fix_rotation", 0, Integer.class) == 1);
        TheBody.setGravityScale(props.get("phys_gravity_scale", 1.0f, Float.class));

        if (neetTransform)
        {
            // Zero coordinates of a body are in its center, so move to object's center
            TheBody.setTransform(object.GetTruePosition().cpy().add(object.getWidth() * .5f, object.getHeight() * .5f).scl(ThePhysics.PixelsToMeters()), object.GetTrueRotation() * MathUtils.degreesToRadians);
        }

        if (IsSensor)
        {
            TheBody.setType(BodyType.StaticBody);
        }
        else
        {
            TheBody.setType(IsDynamic ? BodyDef.BodyType.DynamicBody : (IsKinematic ? BodyType.KinematicBody : BodyDef.BodyType.StaticBody));
        }

        PhysicalMovement = !props.containsKey("phys_manual") || !props.get("phys_manual", Boolean.class);

        //object.SetCollisionBounds();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetOnLadder(boolean onLadder)
    {
        if (onLadder)
        {
            PrevDamping = TheBody.getLinearDamping();
            TheBody.setLinearDamping(LadderDamping);
            TheBody.setGravityScale(0f); // Hack =)
            TheBody.setLinearVelocity(0f, 0f);
        }
        else
        {
            TheBody.setLinearDamping(PrevDamping);
            TheBody.setGravityScale(1f);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetSliding(boolean sliding)
    {
        if (sliding)
        {
            TheBody.setGravityScale(0.2f); // Hack =)
            TheBody.setLinearVelocity(0f, 0f);
        }
        else
        {
            TheBody.setGravityScale(1f);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(GameObject object, float delta)
    {
        if (!object.IsUpdatable() || IsSensor || (TheBody == null))
        {
            return;
        }

        if (PhysicalMovement)
        {
            if (UseCachedBody)
            {
                object.setPosition(TheBody.getPosition().x * Globals.METERS_TO_PIXELS - object.getWidth() * .5f,
                        TheBody.getPosition().y * Globals.METERS_TO_PIXELS + (object.getHeight() * .5f - TheBodyAABB.height * Globals.METERS_TO_PIXELS * .5f) - object.getHeight() * .5f
                        );
            }
            else
            {
                object.setPosition(object.getWidth() * OffsetX + TheBody.getPosition().x * Globals.METERS_TO_PIXELS - object.getWidth() / 2,
                        object.getHeight() * OffsetY + TheBody.getPosition().y * Globals.METERS_TO_PIXELS - object.getHeight() / 2);
            }

            object.setRotation(TheBody.getAngle() * MathUtils.radiansToDegrees);
        }
        else
        {
            TheBody.setTransform(object.GetTruePosition().add(object.getWidth() * .5f, object.getHeight() * .5f).scl(ThePhysics.PixelsToMeters()), object.GetTrueRotation() * MathUtils.degreesToRadians);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean IsDynamic()
    {
        return (TheBody.getType() == BodyType.DynamicBody);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Impulse(float ix, float iy, float px, float py)
    {
        TheBody.applyLinearImpulse(ix, iy, px, py, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetSpeed(float speedX, float speedY)
    {
        TheBody.setLinearVelocity(speedX, speedY);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetForce(float forceX, float forceY)
    {
        TheBody.applyForce(forceX, forceY, TheBody.getWorldCenter().x, TheBody.getWorldCenter().y, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetAngle()
    {
        return TheBody.getAngle() * MathUtils.radiansToDegrees;
    }

    public void SetAngle(float degrees)
    {
        TheBody.setTransform(TheBody.getPosition(), degrees * MathUtils.degreesToRadians);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void JumpToObject(GameObject object)
    {
        if (TheBody != null)
        {
            TheBody.setTransform(object.GetTruePosition().add(object.getWidth() / 2, object.getHeight() / 2).scl(ThePhysics.PixelsToMeters()), object.GetTrueRotation() * MathUtils.degreesToRadians);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean TestPoint(float x, float y)
    {
        Array<Fixture> fixtures= TheBody.getFixtureList();
        for (Fixture fixture : fixtures)
        {
            if (fixture.testPoint(x, y))
            {
                return true;
            }
        }
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static void SetFreezeBits(Body body, short category, short mask)
    {
        if (body != null)
        {
            //body.setAwake(false);
            //body.setActive(false);
            body.setType(BodyType.KinematicBody);
            //body.setAngularVelocity(0f);
            //body.setLinearVelocity(0f, 0f);
            body.setGravityScale(0f);

            body.setLinearVelocity(0f, 0f);

            for (int i = 0; i < body.getFixtureList().size; ++i)
            {
                Fixture fixture = body.getFixtureList().get(i);

                Filter filter = fixture.getFilterData();
                filter.categoryBits = category;
                filter.maskBits = mask;

                fixture.setFilterData(filter);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetFreezeBits(short category, short mask)
    {
        SetFreezeBits(TheBody, category, mask);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Raycast(Vector2 v1, Vector2 v2, RayCastCallback callback)
    {
        ThePhysics.Raycast(v1, v2, callback);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
