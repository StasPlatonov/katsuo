package bmg.katsuo.physics;

import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.physics.box2d.*;

public class Collider
{
    public enum CollisionType
    {
        COLLISION_BEGIN,
        COLLISION_END
    };

    Fixture selfFixure;
    Fixture fixture;

    WorldManifold manifold;

    public Collider(Fixture selfFixture, Fixture fixture, WorldManifold manifold)
    {
        this.selfFixure = selfFixture;
        this.fixture = fixture;
        this.manifold = manifold;
    }

    public GameObject GetObject()
    {
        return (fixture.getUserData() instanceof GameObject) ? (GameObject) fixture.getUserData() : null;
    }

    public Body GetBody()
    {
        return fixture.getBody();
    }

    public Filter GetFilter()
    {
        return fixture.getFilterData();
    }

    public Fixture GetSelfFixture()
    {
        return selfFixure;
    }

    public Fixture GetFixture()
    {
        return fixture;
    }

    public WorldManifold GetManifold()
    {
        return manifold;
    }
}
