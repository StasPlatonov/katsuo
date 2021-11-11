package bmg.katsuo.gameplay.weapons;

import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.controllers.LightController;
import bmg.katsuo.gameplay.objects.LightObject;
import bmg.katsuo.objects.DynamicObject;
import bmg.katsuo.objects.GameObject;
import bmg.katsuo.objects.Types;
import bmg.katsuo.physics.Collider;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.UUID;

import static bmg.katsuo.objects.Types.*;

public class Bullet extends DynamicObject
{
    private boolean NeedDestroy = false;
    private float Damage;

    private ExplosionEffect Explosion = null;
    private boolean NeedExplode = false;

    private float Life = 0f;
    private float LifeTime = 4f;

    private LightObject TheLight;

    //-------------------------------------------------------------------------------------------------------------------------

    public Bullet(GameObject owner, Box2dPhysicsSystem physics, float x, float y, float vx, float vy, float damage)
    {
        this(owner, physics, x, y, vx, vy, damage, false);
    }

    public Bullet(GameObject owner, Box2dPhysicsSystem physics, float x, float y, float vx, float vy, float damage, boolean enemyBullet)
    {
        super(owner.GetLayer(), physics, x - (enemyBullet ? 4 : 2), y - (enemyBullet ? 4 : 2), enemyBullet ? 8 : 4, enemyBullet ? 8 : 4, enemyBullet ? "redtexture" : "greentexture");

        Damage = damage;

        setName("bullet_" + UUID.randomUUID().toString());

        Filter filter = new Filter();
        if (enemyBullet)
        {
            filter.categoryBits = WEAPON_BIT;
            filter.maskBits = GROUND_BIT | PLAYER_BIT | BREAKABLE_BIT | ITEM_BIT;
        }
        else
        {
            filter.categoryBits = WEAPON_BIT;
            filter.maskBits = GROUND_BIT | ENEMY_WEAKNESS_BIT | BREAKABLE_BIT | ITEM_BIT;
        }
        TheFixture.setFilterData(filter);

        // Bullets can push the things it collides with, but we need to increase its mass
        //MassData md =new MassData();
        //md.mass = 10f;
        //TheBody.setMassData(md);

        TheBody.setBullet(true);

        TheBody.setLinearVelocity(vx, vy);
        TheBody.setGravityScale(0f);

        Layer.addActorAfter(owner, this);

        //Explosion = new ExplosionEffect(Layer.GetApp(), 0.1f, 1f, "BulletSparksClass", "bullet");

        Sounds.Add(getClass().getSimpleName(), GROUND_HIT_SOUND, "ground_hit");

        CreateLight();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void CreateLight()
    {
        MapProperties lightProps = new MapProperties();
        lightProps.put("type", "LightObject");
        lightProps.put("light_type", "point");
        lightProps.put("strength", 50);
        lightProps.put("color", Color.valueOf("#999966BB"));
        TheLight = (LightObject)Layer.GetState().CreateObject(Layer, getName() + "_light", lightProps);
        LightController lightContr = TheLight.GetController(LightController.class);
        lightContr.AttachTo(TheBody);
        lightContr.SetSoftnessLengthK(0.3f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void RemoveLight()
    {
        if (TheLight == null)
        {
            return;
        }

        Layer.RemoveGameObject(TheLight);

        TheLight = null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove()
    {
        RemoveLight();

        //@TODO: DynamicObject is not GameObject, so copy logic from GameObject::remove()
        Layer.removeActor(this);

        if (Explosion != null)
            Explosion.dispose();

        return super.remove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        if (Explosion != null)
        {
            if (Explosion.IsExploding())
            {
                Explosion.draw(batch, parentAlpha);
                return;
            }
        }
        super.draw(batch, parentAlpha);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Interpolation ExplosionAlphaInterpolation = new Interpolation.ExpIn(8, 6);
    private Interpolation ExplosionScaleInterpolation = new Interpolation.ExpOut(8, 6);

    @Override
    public void act(float delta)
    {
        Life += delta;
        if (Life >= LifeTime)
        {
            remove();
            return;
        }

        if (NeedDestroy)
        {
            remove();
            return;
        }

        if (NeedExplode)
        {
            Explode();

            NeedExplode = false;
        }

        if (Explosion != null)
        {
            if (Explosion.IsExploding())
            {
                NeedDestroy = Explosion.Update(delta);
            }
        }

        super.act(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Explode()
    {
        RemoveLight();

        if (Explosion != null)
        {
            Explosion.Explode(getX(), getY(), getWidth(), getHeight());
        }
        else
        {
            //float size = 32f;
            //new AnimationEffect(Layer, getX() - size * .5f, getY() - size * .5f, size, size, "light_glow", 0.012f);

            NeedDestroy = true;
        }

        Box2dController.SetFreezeBits(TheBody, NOTHING_BIT, NOTHING_BIT);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
        if (NeedExplode)
        {
            return;
        }

        Fixture fixture = collider.GetFixture();

        //Explode();
        NeedExplode = true;

        // All objects have their own sounds, except for ground
        if ((fixture.getFilterData().categoryBits & GROUND_BIT) == GROUND_BIT)
        {
            long soundId = Layer.GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), GROUND_HIT_SOUND), getX(), getY(),false);
        }

        //Body body = collider.GetFixture().getBody();
        //body.applyLinearImpulse(TheBody.getLinearVelocity().x, TheBody.ge);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetDamage()
    {
        return Damage;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Types.DamageType GetDamageType()
    {
        return DamageType.DAMAGE_BULLET;
    }
    //-------------------------------------------------------------------------------------------------------------------------

}
