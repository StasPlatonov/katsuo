package bmg.katsuo.gameplay.objects;

import bmg.katsuo.Globals;
import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.gameplay.weapons.Bullet;
import bmg.katsuo.objects.BaseGameObject;
import bmg.katsuo.objects.GameObject;
import bmg.katsuo.physics.Collider;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import bmg.katsuo.ui.AtlasSpriteEx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import static bmg.katsuo.objects.Types.*;

public class BreakableObject extends BaseGameObject
{
    protected Box2dController PhysController;

    private boolean NeedBreak = false;
    private float Health, InitialHealth;
    private Vector2 DamageImpulse = new Vector2();

    private List<Vector2> ForcesBegs = new ArrayList<Vector2>();
    private List<Vector2> ForcesEnds = new ArrayList<Vector2>();
    private boolean DebugForces = false;
    private boolean PlayerInteraction = true;
    private boolean WeaponInteraction = true;
    private float PlayerBreakTimeout = 0f;
    private float BreakingTime = 0f;
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        properties.put("phys_category", (int)GROUND_BIT | BREAKABLE_BIT | SHADOW_CASTER_BIT);
        properties.put("phys_mask", (int)(GROUND_BIT | PLAYER_BIT | WEAPON_BIT | ITEM_BIT | TRIGGER_BIT | ENEMY_WEAKNESS_BIT));

        boolean isDynamic = properties.get("is_dynamic", false, Boolean.class);
        properties.put("phys_object_type", isDynamic ? "dynamic" : "static");

        super.Init(properties);

        InitialHealth = Health = properties.get("health", 1f, Float.class);
        PlayerInteraction = properties.get("player_interaction", true, Boolean.class);
        WeaponInteraction = properties.get("weapon_interaction", true, Boolean.class);
        PlayerBreakTimeout = properties.get("player_break_timeout", 0f, Float.class);

        if (DebugForces)
        {
            setDebug(true, true);
        }

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

    private void Break(Vector2 epicenter, float maxPower, float radius)
    {
        GetApp().PlaySound("break", false);

        // Disable collisions for broken object
        Box2dController controller = GetController(Box2dController.class);
        if (controller != null)
        {
            controller.SetFreezeBits(NOTHING_BIT, NOTHING_BIT);
        }

        Box2dPhysicsSystem phys = GetLayer().GetSystem(Box2dPhysicsSystem.class);

        float area = getWidth() * getHeight();

        assert(Render != null);
        float deltaU = Render.getU2() - Render.getU();
        float deltaV = Render.getV2() - Render.getV();

        while (area > 0f)
        {
            // Generate part randomly
            float partX = MathUtils.random(0, getWidth());
            float partY = MathUtils.random(0, getHeight());

            //float partW = MathUtils.random(5, 20);
            //float partH = MathUtils.random(5, 20);
            float partW = MathUtils.random(Math.min(20, getWidth() / 3), Math.min(32, getWidth() / 3));
            float partH = MathUtils.random(Math.min(20, getHeight() / 3), Math.min(32, getHeight() / 3));

            // Check we do not exceed total area
            float partArea = partW * partH;
            if (partArea > area)
            {
                partW = partH = (float)Math.sqrt(area);
                area = 0;
            }
            else
            {
                area -= partArea;
                if (area < 4f) // Not allow too small parts (Box2d assertion on too small areas)
                {
                    area = 0;
                }
            }

            float lf = DebugForces ? 1000f : MathUtils.random(0.5f, 2f);

            AtlasSpriteEx sprite = new AtlasSpriteEx(Render.getAtlasRegion());

            float partDeltaU = partW * deltaU / getWidth();
            float partDeltaV = partH * deltaV / getHeight();

            float u = MathUtils.random(Render.getU(), Render.getU2() - partDeltaU);
            float u2 = u + partDeltaU;
            float v = MathUtils.random(Render.getV(), Render.getV2() - partDeltaV);
            float v2 = v + partDeltaV;

            sprite.setU(u);
            sprite.setU2(u2);
            sprite.setV(v);
            sprite.setV2(v2);

            // @TODO: use texture coords correctly or use different debris sprites

            BrokenPart part = new BrokenPart(GetLayer(), phys, GetTrueX() + partX, GetTrueY() + partY, partW, partH, lf, sprite);

            if (epicenter == null)
            {
                // Simple break
                float maxForce = MathUtils.random(0.01f, 0.05f);
                float fx = MathUtils.random(-maxForce * 0.1f, maxForce * 0.1f);
                float fy = MathUtils.random(-maxForce, maxForce * 0.1f);

                part.Push(fx, fy);
            }
            else
            {
                DamageImpulse.set(GetTrueX() + partX + partW / 2, GetTrueY() + partY + partH / 2);

                if (DebugForces)
                {
                    ForcesBegs.add(new Vector2(DamageImpulse));
                }
                // Simple push. All parts with same forces
                //dir.scl(Globals.PIXELS_TO_METERS).sub(epicenter).nor();
                //part.Push(dir.x * 0.01f, dir.y * 0.01f);

                // Calculate force in dependendence of the distance of part from epicenter
                DamageImpulse.scl(Globals.PIXELS_TO_METERS).sub(epicenter);
                float distanceFromEpicenter = DamageImpulse.len();
                DamageImpulse.nor();

                final float impulsePower = maxPower * 0.04f * MathUtils.clamp(MathUtils.lerp(1f, 0f, distanceFromEpicenter / (radius * Globals.PIXELS_TO_METERS)), 0f, 1f);

                DamageImpulse.scl(impulsePower);

                if (DebugForces)
                {
                    DamageImpulse.scl(Globals.METERS_TO_PIXELS).scl(10);

                    ForcesEnds.add(new Vector2(ForcesBegs.get(ForcesBegs.size() - 1)).add(DamageImpulse));

                    part.DebugFreeze();
                }
                else
                {
                    part.Push(DamageImpulse.x, DamageImpulse.y);
                }
            }

            GetLayer().addActor(part);
        }

        if (!DebugForces)
        {
            //remove();
            GetLayer().RemoveGameObject(this);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (DebugForces && !ForcesBegs.isEmpty())
        {
            for (int i = 0; i < ForcesBegs.size(); ++i)
            {
                shapes.line(ForcesBegs.get(i), ForcesEnds.get(i));
            }
        }

        super.drawDebug(shapes);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Destruct(Vector2 epicenter, float maxPower, float radius)
     {
        if (!WeaponInteraction)
        {
            return;
        }
        Break(epicenter, maxPower, radius);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Damage(float damage)
    {
        if (!WeaponInteraction)
        {
            return;
        }

        Health -= damage;
        if (Health <= 0)
        {
            NeedBreak = true;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        if (!IsUpdatable() || !isVisible())
        {
            return;
        }

        super.Update(delta);

        if (NeedBreak)
        {
            if (PlayerBreakTimeout > 0f)
            {
                BreakingTime += delta;

                Health = InitialHealth * (1f - BreakingTime / PlayerBreakTimeout);

                if (Health > 0)
                {
                    return;
                }
            }
            Break(null, 0f, 0f);
            SetUpdatable(false);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private int PlayerCollisions = 0;

    @Override
    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
        final int group = collider.GetFixture().getFilterData().categoryBits;

        if (type == Collider.CollisionType.COLLISION_BEGIN)
        {
            if (PlayerInteraction && ((group & PLAYER_BIT) == PLAYER_BIT))
            {
                ++PlayerCollisions;
                NeedBreak = true;
            }
            else if (WeaponInteraction && ((group & WEAPON_BIT) == WEAPON_BIT))
            {
                if (collider.GetFixture().getUserData() instanceof Bullet)
                {
                    final Bullet bullet = (Bullet) collider.GetFixture().getUserData();
                    Damage(bullet.GetDamage());

                    long soundId = GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), HIT_SOUND), getX(), getY(),false);

                    final Vector2 bulletSpeed = bullet.GetSpeed();
                    float impX = bullet.GetMass() * bulletSpeed.x;
                    float impY = bullet.GetMass() * bulletSpeed.y;

                    PhysController.Impulse(-impX, -impY, 0f, 0f);
                }
            }
        }

        if (type == Collider.CollisionType.COLLISION_END)
        {
            if (PlayerInteraction && ((group & PLAYER_BIT) == PLAYER_BIT))
            {
                --PlayerCollisions;
                if (PlayerCollisions == 0)
                {
                    NeedBreak = false;
                }
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String GetDebugString()
    {
        return "H=" + Health + (PlayerBreakTimeout > 0f ? String.format(" B=%.2f", PlayerBreakTimeout - BreakingTime) : "");
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnAction(GameObject sender, String action)
    {
        Health = 0;
        NeedBreak = true;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
