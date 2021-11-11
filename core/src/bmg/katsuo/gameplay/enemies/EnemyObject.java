package bmg.katsuo.gameplay.enemies;

import bmg.katsuo.Globals;
import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.gameplay.GameEvents;
import bmg.katsuo.gameplay.events.DamageEnemyEventArgs;
import bmg.katsuo.gameplay.objects.PlatformerObject;
import bmg.katsuo.gameplay.weapons.Bullet;
import bmg.katsuo.physics.Collider;
import bmg.katsuo.systems.particles.ParticleEmitterEx;
import bmg.katsuo.systems.particles.ParticleEmitterSettings;
import bmg.katsuo.systems.particles.ParticleSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import static bmg.katsuo.objects.Types.*;
import static bmg.katsuo.objects.Types.TRIGGER_BIT;

public class EnemyObject extends PlatformerObject
{
    private ParticleSystem PartSystem;
    private ParticleEmitterSettings EmitSettings = null;
    private ParticleEmitterEx Emit = null;
    private float ShotSpeed;
    private final Vector2 BulletVelocity = new Vector2();
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        properties.put("phys_fix_rotation", 1);
        properties.put("phys_category", (int)ENEMY_WEAKNESS_BIT);
        properties.put("phys_mask", (int)(GROUND_BIT | WEAPON_BIT | PLAYER_BIT | ITEM_BIT | TRIGGER_BIT | BREAKABLE_BIT));

        super.Init(properties);

        PartSystem = GetState().GetPlaygroundLayer().GetSystem(ParticleSystem.class);
        if (PartSystem != null)
        {
            final MapProperties particlesProps = GetApp().GetGamePlay().GetClassProperties("BloodParticlesClass");
            EmitSettings = new ParticleEmitterSettings(particlesProps);
            Emit = PartSystem.CreateEmitter(EmitSettings, 0f, 0f);
        }

        Sounds.Add(getClass().getSimpleName(), JUMP_SOUND, properties.get("jump_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), SHOOT_SOUND, properties.get("shoot_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), HIT_SOUND, properties.get("hit_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), DIE_SOUND, properties.get("die_sound", "", String.class));

        ShotSpeed = properties.get("shoot_speed", 100f, Float.class);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove()
    {
        if ((PartSystem != null) && (Emit != null))
        {
            //@TODO: check all bodies are removedS
            PartSystem.RemoveEmitter(Emit);
            Emit.dispose();
        }
        return super.remove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void InitPhysics(Box2dController controller)
    {
        super.InitPhysics(controller);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void UpdateStates(float delta)
    {
        super.UpdateStates(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        super.Update(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    long LastDamageSoundTime = 0;

    @Override
    public void Damage(float damage, DamageType type)
    {
        int oldHealth = State.GetHealth();

        super.Damage(damage, type);

        int delta = oldHealth - State.GetHealth();
        if (delta != 0)
        {
            long curr = TimeUtils.millis();
            if (curr - LastDamageSoundTime > 1000)
            {
                long soundId = GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), HIT_SOUND), getX(), getY(), false);
                LastDamageSoundTime = curr;
            }

            GetApp().GetEvents().Event(GameEvents.EventId.EVENT_DAMAGE_ENEMY, new DamageEnemyEventArgs(this, oldHealth, State.GetHealth()));

            /*long curr = TimeUtils.millis();
            if (curr - LastDamageEffectTime > 300)
            {
                float size = 48f;
                new AnimationEffect(GetLayer(), GetTrueX() + getWidth() * .5f - size * .5f, GetTrueY() + getHeight() * .5f - size * .5f - 2f, size, size, "light_glow", 0.02f);

                LastDamageEffectTime = curr;
            }*/
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void BulletDamage(float damage, Vector2 contactPoint, Vector2 direction)
    {
        Damage(damage, DamageType.DAMAGE_BULLET);

        if (Emit != null)
        {
            Emit.SetPosition(contactPoint.x * Globals.METERS_TO_PIXELS, contactPoint.y * Globals.METERS_TO_PIXELS);
            Emit.SetInitialImpulse(direction.x * 5f, direction.y * 5f);
            Emit.NeedEmit(20);

            direction.x = 0;
            direction.y = 1f;
            CrateGhost(direction, 50, 0.5f);
        }

        // Turn to player
        if (GetDirection() == MoveDirection.MD_RIGHT)
        {
            if (contactPoint.x < PhysController.GetPosition().x)
            {
                SetDirection(MoveDirection.MD_LEFT);
            }
        }
        else if (contactPoint.x > PhysController.GetPosition().x)
        {
            SetDirection(MoveDirection.MD_RIGHT);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void InitKill()
    {
        if (IsDying())
        {
            return;
        }
        long soundId = GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), DIE_SOUND), getX(), getY(), false);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
        if (type != Collider.CollisionType.COLLISION_BEGIN)
        {
            return;
        }

        // we can use collider.GetObject().GetGroupId() or compare by name (collObj.getName().equals(Globals.PLAYER_ID))
        final int category = collider.GetFixture().getFilterData().categoryBits;

        if ((category & GROUND_BIT) == GROUND_BIT)
        {
            SetState(ES_IDLE);
        }

        if ((category & WEAPON_BIT) == WEAPON_BIT)
        {
            if (collider.GetFixture().getUserData() instanceof Bullet)
            {
                Bullet bullet = (Bullet)collider.GetFixture().getUserData();

                final Vector2 contactPoint = collider.GetManifold().getPoints()[0];

                // Use bullet speed as direction
                Vector2 dir = new Vector2(bullet.GetSpeed()).nor();

                BulletDamage(bullet.GetDamage(), contactPoint, dir);
            }
        }

        super.OnCollide(collider, type);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        super.drawDebug(shapes);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    //private long JumpTime;

    public void Jump(float jumpSpeed)
    {
        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), JUMP_SOUND), getX(), getY(), false);

        SetState(PlatformerObject.ES_JUMP);
        //PhysController.SetSpeed(kx, ky);
        float jumpX = PhysController.GetMass() * (PhysController.GetSpeed().x > 0 ? jumpSpeed : -jumpSpeed);
        float jumpY = PhysController.GetMass() * (float)Math.sqrt(2 * 9.8 * (32 * 2f * Globals.PIXELS_TO_METERS));
        //float jumpY = jumpSpeed;
        PhysController.Impulse(jumpX, jumpY, 0f, 0f);

        //JumpTime = TimeUtils.millis();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void FireBullet(float startX, float startY, float speedX, float speedY)
    {
        Bullet bullet = new Bullet(this, ThePhysics, startX, startY, speedX, speedY, 1f, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Shoot(float sourceX, float sourceY, float targetX, float targetY)
    {
        BulletVelocity.set(targetX, targetY).sub(sourceX, sourceY).nor();
        BulletVelocity.scl(ShotSpeed * Globals.PIXELS_TO_METERS);

        FireBullet(sourceX, sourceY, BulletVelocity.x, BulletVelocity.y);

        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), SHOOT_SOUND), getX(), getY(), false);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
