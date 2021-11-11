package bmg.katsuo.physics;

import bmg.katsuo.systems.Box2dPhysicsSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

public class RopePhysObject
{
    Box2dPhysicsSystem ThePhysics;

    private Body[] Bodies;
    private Joint[] Joints;
    private float[] SegmentsSizes;
    private float Thickness;
    private int SegmentsCount;
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        for (Joint joint : Joints)
        {
            if (joint != null)
            {
                ThePhysics.GetWorld().destroyJoint(joint);
            }
        }
        Joints = null;

        for (Body body : Bodies)
        {
            ThePhysics.DestroyBody(body);
        }

        Bodies = null;
    }
    //-------------------------------------------------------------------------------------------------------------------------


    private Body CreateAnchorBody(Box2dPhysicsSystem physics, Vector2 pos, float damping, Object userObject)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.angularDamping = damping;
        bodyDef.linearDamping = damping;
        Body anchor = physics.CreateBody(bodyDef);
        anchor.setUserData(userObject);

        PolygonShape anchorShape = new PolygonShape();
        anchor.setType(BodyDef.BodyType.StaticBody);
        anchorShape.setAsBox(0.02f, 0.02f);
        anchor.setTransform(pos, 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 1.0f;
        fixtureDef.isSensor = true;
        fixtureDef.shape = anchorShape;
        anchor.createFixture(fixtureDef);
        anchorShape.dispose();

        anchor.setTransform(pos, 0f);
        return anchor;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Body CreateSegmentBody(Box2dPhysicsSystem physics, Vector2 v1, Vector2 v2, float width, Object userObject, int categoryBits, int maskBits, float damping)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.angularDamping = damping;
        bodyDef.linearDamping = damping;

        Body body = physics.CreateBody(bodyDef);
        body.setUserData(userObject);
        body.setType(BodyDef.BodyType.DynamicBody);
        //body.setType(BodyDef.BodyType.StaticBody);

        Vector2 diff = new Vector2(v2);
        diff.sub(v1);
        float segmentLength = diff.len();

        // Create shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(segmentLength * 0.5f, width * 0.5f);

        // Create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 2.0f;
        //fixtureDef.friction = 1.0f;
        fixtureDef.filter.categoryBits = (short)categoryBits;
        fixtureDef.filter.maskBits = (short)maskBits;
        fixtureDef.filter.groupIndex = 0;
        fixtureDef.shape = shape;
        Fixture fix = body.createFixture(fixtureDef);
        fix.setUserData(userObject);

        shape.dispose();

        float alpha = (float)Math.atan((v2.y - v1.y) / (v2.x - v1.x));
        body.setTransform(v1.x + segmentLength * 0.5f, v1.y + width * 0.5f, alpha);

        return body;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Body[] GetBodies()
    {
        return Bodies;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public RopePhysObject(Box2dPhysicsSystem physics, float[] vertices, int categoryBits, int maskBits, Object userObject, int[] anchorsIndices, float thickness, float damping)
    {
        ThePhysics = physics;
        Thickness = thickness;

        int numVerts = vertices.length / 2;
        Vector2[] worldVerticies = new Vector2[numVerts];

        for (int i = 0; i < numVerts; ++i)
        {
            worldVerticies[i] = new Vector2(vertices[i * 2], vertices[i * 2 + 1]).scl(physics.PixelsToMeters());
        }

        int numBodies = numVerts - 1;
        SegmentsCount = numBodies;

        Body[] bodies = new Body[numBodies + anchorsIndices.length];
        Joint[] joints = new Joint[numVerts + anchorsIndices.length];

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.collideConnected = false; // do not collide between segments
        ropeJointDef.maxLength = 0.1f;
        ropeJointDef.localAnchorA.set(0,0);
        ropeJointDef.localAnchorB.set(0.0f,0);

        RevoluteJointDef revJointDef = new RevoluteJointDef();
        revJointDef.enableLimit = true;
        revJointDef.collideConnected = false;
        revJointDef.lowerAngle = -15.0f * MathUtils.degreesToRadians;
        revJointDef.upperAngle = 15.0f * MathUtils.degreesToRadians;
        //revJointDef.referenceAngle = 15f * MathUtils.degreesToRadians;

        /*
        DistanceJointDef distJointDef = new DistanceJointDef();
        distJointDef.collideConnected = false;
        distJointDef.dampingRatio = 0f;
        distJointDef.length = 2.0f;
        distJointDef.frequencyHz = 0;
        */

        float ropeWidth = Thickness * physics.PixelsToMeters();

        Body prevBody = null;

        // Create all segments with revolute joints
        float[] segmentsSizes = new float[numBodies];
        for (int b = 0; b < numBodies; ++b)
        {
            final Vector2 v1 = worldVerticies[b];
            final Vector2 v2 = worldVerticies[b + 1];

            final Vector2 diff = new Vector2(v2).sub(v1);
            float segmentLength = diff.len();

            Body body = CreateSegmentBody(physics, v1, v2, ropeWidth, userObject, categoryBits, maskBits, damping);

            if (b > 0)
            {
                ropeJointDef.localAnchorA.set(segmentLength * 0.5f * 1.0f, 0f);
                revJointDef.localAnchorA.set(segmentLength * 0.5f * 1.0f, 0f);

                ropeJointDef.localAnchorB.set(-segmentLength * 0.5f * 1.0f, 0f);
                revJointDef.localAnchorB.set(-segmentLength * 0.5f * 1.0f, 0f);

                //ropeJointDef.bodyA = prevBody;
                //ropeJointDef.bodyB = body;
                //joints[b] = ThePhysics.GetWorld().createJoint(ropeJointDef);

                // revolute
                revJointDef.bodyA = prevBody;
                revJointDef.bodyB = body;
                joints[b] = ThePhysics.GetWorld().createJoint(revJointDef);

                //distance
                //distJointDef.initialize(prevBody, body, prevBody.getWorldCenter(), body.getWorldCenter());
                //joints[i - 1] = ThePhysics.GetWorld().createJoint(distJointDef);
            }

            bodies[b] = body;
            segmentsSizes[b] = segmentLength;

            prevBody = body;
        }

        // add static anchors with rope joints
        for (int a = 0; a < anchorsIndices.length; ++a)
        {
            int idx = anchorsIndices[a];
            Body anch = CreateAnchorBody(ThePhysics, worldVerticies[idx], damping, userObject);

            ropeJointDef.bodyA = anch;
            ropeJointDef.localAnchorA.set(0f, 0f);

            if (idx < numVerts - 1)
            {
                ropeJointDef.bodyB = bodies[idx];
                ropeJointDef.localAnchorB.set(-segmentsSizes[idx] * 0.5f * 1.0f, 0f);
            }
            else
            {
                ropeJointDef.bodyB = bodies[numBodies - 1];
                ropeJointDef.localAnchorB.set(segmentsSizes[numBodies - 1] * 0.5f * 1.0f, 0f);
            }

            bodies[numBodies + a] = anch;
            joints[numVerts + a] = ThePhysics.GetWorld().createJoint(ropeJointDef);
        }

        Bodies = bodies;
        Joints = joints;
        SegmentsSizes = segmentsSizes;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float[] GetSegmentsSizes()
    {
        return SegmentsSizes;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public int GetSegmentsCount()
    {
        return SegmentsCount;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetThickness()
    {
        return Thickness;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
