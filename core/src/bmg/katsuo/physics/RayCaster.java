package bmg.katsuo.physics;

import bmg.katsuo.systems.Box2dPhysicsSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class RayCaster implements RayCastCallback
{
    private Box2dPhysicsSystem ThePhysics;
    private boolean Nearest;
    private boolean HitSomething;
    private int Collidables;
    private int OtherCollidables;
    private Vector2 HitPoint = new Vector2();
    private Fixture ResultFixture;

    public RayCaster()
    {
    }

    public RayCaster(Box2dPhysicsSystem physics)
    {
        ThePhysics = physics;
    }

    public boolean Check(Vector2 begin, Vector2 end, int collidables)
    {
        return (ThePhysics == null) ? false : Check(ThePhysics, begin, end, collidables);
    }

    public boolean Check(Box2dPhysicsSystem physics, Vector2 begin, Vector2 end, int collidables)
    {
        Nearest = false;
        HitSomething = false;
        Collidables = collidables;
        ResultFixture = null;
        physics.Raycast(begin, end, this);
        return HitSomething;
    }

    public Fixture GetResultFixture()
    {
        return ResultFixture;
    }

    public boolean CheckNearest(Vector2 begin, Vector2 end, int targetCollidables, int otherCollidables)
    {
        return (ThePhysics == null) ? false : CheckNearest(ThePhysics, begin, end, targetCollidables, otherCollidables);
    }

    public boolean CheckNearest(Vector2 begin, Vector2 end, int targetCollidables, int otherCollidables, Vector2 hitPoint)
    {
        return (ThePhysics == null) ? false : CheckNearest(ThePhysics, begin, end, targetCollidables, otherCollidables, hitPoint);
    }

    public boolean CheckNearest(Box2dPhysicsSystem physics, Vector2 begin, Vector2 end, int targetCollidables, int otherCollidables)
    {
        Nearest = true;
        HitSomething = false;
        Collidables = targetCollidables;
        OtherCollidables = otherCollidables;
        HitPoint.set(begin);
        physics.Raycast(begin, end, this);
        return HitSomething;
    }

    public boolean CheckNearest(Box2dPhysicsSystem physics, Vector2 begin, Vector2 end, int targetCollidables, int otherCollidables, Vector2 hitPoint)
    {
        boolean result = CheckNearest(physics, begin, end, targetCollidables, otherCollidables);
        hitPoint.set(HitPoint);
        return result;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
    {
        final int category = fixture.getFilterData().categoryBits;

        if (fixture.isSensor())
        {
            return -1; // ignore collision and continue raycast
        }

        if (Nearest)
        {
            HitPoint.set(point);
            if ((OtherCollidables & category) != 0)
            {
                HitSomething = false;
            }
            else if ((Collidables & category) != 0)
            {
                HitSomething = true;
            }
            else
            {
                // We intersect something that we are not interested in.. ignore collision
                return -1;
            }

            ResultFixture = fixture;
            return fraction;
        }
        else
        {
            if ((Collidables & category) == 0)
            {
                return -1; // ignore collision and continue raycast
            }

            if (fraction < 1.0f)
            {
                HitSomething = true;
            }

            ResultFixture = fixture;
            return 0;
        }
    }
}
