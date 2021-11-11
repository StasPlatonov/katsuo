package bmg.katsuo.objects;

import bmg.katsuo.Globals;
import bmg.katsuo.managers.SoundsCollection;
import bmg.katsuo.physics.Collider;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import bmg.katsuo.ui.AtlasSpriteEx;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class DynamicObject extends Actor
{
    private boolean Removed = false;
    protected GameLayer Layer;
    protected GameObject TheOwner;
    protected Box2dPhysicsSystem ThePhysics;

    protected Body TheBody;
    protected Fixture TheFixture = null;

    protected AtlasSpriteEx Render;

    protected SoundsCollection Sounds;
    //-------------------------------------------------------------------------------------------------------------------------

    public DynamicObject(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, String spriteId)
    {
        this(layer, physics, x, y, width, height, (AtlasSpriteEx)layer.GetSprite(spriteId));
    }

    public DynamicObject(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, String spriteId, String physicsName)
    {
        this(layer, physics, x, y, width, height, (AtlasSpriteEx)layer.GetSprite(spriteId), physicsName);
    }

    public DynamicObject(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, AtlasSpriteEx sprite)
    {
        Layer = layer;
        ThePhysics = physics;
        Sounds = Layer.GetApp().GetSoundsCollection();

        TheBody = ThePhysics.CreateBox(x, y, width, height, (short)0, (short)0, BodyDef.BodyType.DynamicBody, 0.2f, 0f, this);

        TheFixture = TheBody.getFixtureList().get(0);

        setWidth(width);
        setHeight(height);
        setOriginX(width / 2);
        setOriginY(height / 2);

        Render = sprite;

        setDebug(layer.GetApp().GetRenderOptions().Debug); //@TODO: move to correct place
    }

    public DynamicObject(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, AtlasSpriteEx sprite, String physicsName/*, boolean scale*/)
    {
        Layer = layer;
        ThePhysics = physics;

        Sounds = Layer.GetApp().GetSoundsCollection();

        // Scale physics according to object size
        float spW = sprite.getAtlasRegion().originalWidth;
        float spH = sprite.getAtlasRegion().originalHeight;

        float scX = width / spW;
        float scY = height / spH;

        TheBody = ThePhysics.CreateBodyFromCache(physicsName, scX, scY);
        Box2dPhysicsSystem.FitBodyToRect(TheBody);//, width, height);

        TheBody.setTransform(new Vector2(x + width * .5f, y + height * .5f).scl(Globals.PIXELS_TO_METERS), 0f * MathUtils.degreesToRadians);

        TheFixture = TheBody.getFixtureList().get(0);

        setWidth(width);
        setHeight(height);
        setOriginX(width / 2);
        setOriginY(height / 2);

        Render = sprite;

        setDebug(layer.GetApp().GetRenderOptions().Debug); //@TODO: move to correct place
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetMass()
    {
        return TheBody.getMass();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Body GetBody()
    {
        return TheBody;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove()
    {
        if (!Removed)
        {
            Gdx.app.log("DO", "Remove " + this.getClass().getSimpleName() + ": " + getName());
            Removed = true;

            dispose();
        }

        return super.remove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        if (TheBody != null)
        {
            Gdx.app.log("DO","Destroy dynamic object's (" + getName() + ") body");
            ThePhysics.DestroyBody(TheBody);
            TheFixture = null;
            TheBody = null;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        super.drawDebug(shapes);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        if (Render == null)
        {
            return;
        }

        Render.setPosition(getX(), getY());
        Render.setRotation(getRotation());

        Render.setAlpha(getColor().a * parentAlpha);
        Render.setOrigin(getOriginX(), getOriginY());
        Render.setColor(getColor());
        Render.setSize(getWidth(), getHeight());
        Render.setScale(getScaleX(), getScaleY());
        Render.setFlip(false, false);

        Render.draw(batch, parentAlpha);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void act(float delta)
    {
        if (TheBody != null)
        {
            setPosition(TheBody.getPosition().x * ThePhysics.MetersToPixel() - getWidth() / 2,
                        TheBody.getPosition().y * ThePhysics.MetersToPixel() - getHeight() / 2);

            setRotation(TheBody.getAngle() * MathUtils.radiansToDegrees);
        }

        super.act(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetSpeed(float speedX, float speedY)
    {
        TheBody.setLinearVelocity(speedX, speedY);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Vector2 Speed = new Vector2();

    public Vector2 GetSpeed()
    {
        Speed.set(TheBody.getLinearVelocity());
        return Speed;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetSprite(AtlasSpriteEx sprite)
    {
        Render = sprite;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
