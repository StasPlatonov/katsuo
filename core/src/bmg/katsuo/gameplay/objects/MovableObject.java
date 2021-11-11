package bmg.katsuo.gameplay.objects;

import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.gameplay.weapons.Bullet;
import bmg.katsuo.objects.BaseGameObject;
import bmg.katsuo.physics.Collider;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import static bmg.katsuo.objects.Types.*;

public class MovableObject extends BaseGameObject
{
    protected Box2dController PhysController;

    @Override
    public void Init(MapProperties properties)
    {
        properties.put("phys_category", (int)ITEM_BIT | SHADOW_CASTER_BIT | GROUND_BIT);
        properties.put("phys_object_type", "dynamic");
        properties.put("phys_mask", PLAYER_BIT | GROUND_BIT | TRIGGER_BIT | ITEM_BIT | LIGHTS_BIT | WEAPON_BIT | ENEMY_WEAKNESS_BIT);
        float mass = properties.get("mass", 1f, Float.class);
        float area = Math.max(0.0001f, getWidth() * getHeight() * 0.001f);
        properties.put("phys_density", mass / area);

        super.Init(properties);

        Sounds.Add(getClass().getSimpleName(), HIT_SOUND, "ground_hit");
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void InitPhysics(Box2dController controller)
    {
        super.InitPhysics(controller);

        PhysController = controller;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        super.Update(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
        if (type == Collider.CollisionType.COLLISION_BEGIN)
        {
            int category = collider.GetFilter().categoryBits;
            if ((category & PLAYER_BIT) == PLAYER_BIT)
            {
            }

            if ((category & WEAPON_BIT) == WEAPON_BIT)
            {
                if (collider.GetFixture().getUserData() instanceof Bullet)
                {
                    final Bullet bullet = (Bullet) collider.GetFixture().getUserData();

                    final Vector2 bulletSpeed = bullet.GetSpeed();
                    float impX = bullet.GetMass() * bulletSpeed.x;
                    float impY = bullet.GetMass() * bulletSpeed.y;

                    long soundId = GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), HIT_SOUND), getX(), getY(),false);

                    PhysController.Impulse(-impX, -impY, 0f, 0f);
                }
            }
        }
        else if (type == Collider.CollisionType.COLLISION_END)
        {
        }
        super.OnCollide(collider, type);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String GetDebugString()
    {
        return super.GetDebugString();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
