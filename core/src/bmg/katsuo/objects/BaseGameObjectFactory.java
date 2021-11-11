package bmg.katsuo.objects;

import bmg.katsuo.gameplay.enemies.*;
import bmg.katsuo.gameplay.objects.PlatformerObject;
import bmg.katsuo.gameplay.objects.*;
import bmg.katsuo.gameplay.objects.WeaponItemObject;
import com.badlogic.gdx.utils.ObjectMap;

public class BaseGameObjectFactory implements IGameObjectFactory
{
    private ObjectMap<String, IGameObjectGetter> ObjectGetters;
    //-------------------------------------------------------------------------------------------------------------------------

    public BaseGameObjectFactory()
    {
        ObjectGetters = new ObjectMap<String, IGameObjectGetter>();
        RegisterObject("BaseGameObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new BaseGameObject();
            }
        });

        RegisterObject("ScrollingBackground", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new ScrollingBackground();
            }
        });

        RegisterObject("TilemapLayerObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new TilemapLayerObject();
            }
        });

        RegisterObject("GroundLayerObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new GroundLayerObject();
            }
        });

        RegisterObject("PlatformerObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new PlatformerObject();
            }
        });

        RegisterObject("PlayerObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new PlayerObject();
            }
        });

        RegisterObject("SpringObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new SpringObject();
            }
        });

        RegisterObject("TriggerObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new TriggerObject();
            }
        });

        RegisterObject("SawObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new SawObject();
            }
        });

        RegisterObject("MovingPlatformObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new MovingPlatformObject();
            }
        });

        RegisterObject("PathObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new PathObject();
            }
        });

        RegisterObject("SpawnPointObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new SpawnPointObject();
            }
        });

        RegisterObject("DropsSourceObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new DropsSourceObject();
            }
        });

        RegisterObject("ParticlesEmitterObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new ParticlesEmitterObject();
            }
        });

        RegisterObject("LightObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new LightObject();
            }
        });

        RegisterObject("ItemObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new ItemObject();
            }
        });

        RegisterObject("SlidingDoorObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new SlidingDoorObject();
            }
        });

        RegisterObject("ActionButtonObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new ActionButtonObject();
            }
        });

        RegisterObject("RopeObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new RopeObject();
            }
        });

        RegisterObject("WalkingEnemyObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new WalkingEnemyObject();
            }
        });

        RegisterObject("FlyingEnemyObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new FlyingEnemyObject();
            }
        });

        RegisterObject("BreakableObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new BreakableObject();
            }
        });

        RegisterObject("LadderObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new LadderObject();
            }
        });

        RegisterObject("MovableObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new MovableObject();
            }
        });

        RegisterObject("WeaponItemObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new WeaponItemObject();
            }
        });

        RegisterObject("BoxItemObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new BoxItemObject();
            }
        });

        RegisterObject("TurretObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new TurretObject();
            }
        });

        RegisterObject("DeathZoneObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new DeathZoneObject();
            }
        });

        RegisterObject("AdGameObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new AdGameObject();
            }
        });

        RegisterObject("FanObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new FanObject();
            }
        });

        RegisterObject("FallingPlatform", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new FallingPlatform();
            }
        });

        RegisterObject("InteractiveObject", new IGameObjectGetter()
        {
            @Override
            public GameObject GetObject()
            {
                return new InteractiveObject();
            }
        });
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public GameObject GetObject(String name)
    {
        if (name != null && ObjectGetters.containsKey(name))
        {
            return ObjectGetters.get(name).GetObject();
        }
        return null;
    }
    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public void RegisterObject(String objectname, IGameObjectGetter getter)
    {
        ObjectGetters.put(objectname, getter);
    }
    //-------------------------------------------------------------------------------------------------------------------------

}
