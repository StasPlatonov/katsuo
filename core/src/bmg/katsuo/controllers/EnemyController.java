package bmg.katsuo.controllers;

import bmg.katsuo.IApplication;
import bmg.katsuo.gameplay.enemies.*;
import bmg.katsuo.gameplay.objects.PlatformerObject;
import bmg.katsuo.objects.GameObject;
import bmg.katsuo.objects.GameObjectController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class EnemyController extends GameObjectController
{
    protected EnemyObject Enemy;
    private IApplication App;
    protected Box2dController PhysController;

    private Vector2 DesiredSpeed = new Vector2();

    private List<EnemySkill> Skills = new ArrayList<EnemySkill>();
    private FollowPlayerSkill FollowSkill;

    private long LastChangeDirectionTime;
    private final static long ChangeDirectionInterval = 2000;

    protected Vector2 PlayerPos = new Vector2();
    protected Vector2 EnemyPos = new Vector2();

    protected float AttackDistance;
    protected boolean Attacking = false;
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(GameObject object)
    {
        assert(object instanceof EnemyObject);
        Enemy = (EnemyObject)object;
        App = Enemy.GetApp();

        PhysController = Enemy.GetController(Box2dController.class);

        float followPlayerDistance = Enemy.GetProperties().get("follow_player_distance", 0f, Float.class);
        if (followPlayerDistance != 0f)
        {
            FollowSkill = new FollowPlayerSkill(Enemy, followPlayerDistance);
            AddSkill(FollowSkill);
        }

        AddSkill(new CheckWallSkill(Enemy, 1.2f, new CheckWallSkill.CheckWallCallback()
        {
            @Override
            public void OnWall()
            {
                ReverseDirection();
            }

            @Override
            public void OnUpdate(boolean feelWall)
            {
            }
        }));

        final boolean canShoot = Enemy.GetProperties().get("can_shoot", false, Boolean.class);
        if (canShoot)
        {
            AddSkill(new ShootSkill(Enemy, Enemy.GetProperties()));
        }

        AttackDistance = Enemy.GetProperties().get("attack_distance", 0f, Float.class);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected boolean CheckNearbyAttack()
    {
        if (AttackDistance == 0f)
        {
            return false;
        }
        float distSq = PlayerPos.dst2(EnemyPos);
        return (distSq < AttackDistance * AttackDistance);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    void ReverseDirection()
    {
        final long currentTime = TimeUtils.millis();

        if (currentTime - LastChangeDirectionTime > ChangeDirectionInterval)
        {
            Enemy.SetDirection(Enemy.GetDirection() == PlatformerObject.MoveDirection.MD_RIGHT ? PlatformerObject.MoveDirection.MD_LEFT : PlatformerObject.MoveDirection.MD_RIGHT);
            LastChangeDirectionTime = currentTime;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    void AddSkill(EnemySkill skill)
    {
        Skills.add(skill);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    void RemoveSkill(Class<? extends EnemySkill> skillClass)
    {
        Iterator<EnemySkill> it = Skills.iterator();
        while (it.hasNext())
        {
            EnemySkill skill = it.next();
            if (skill.getClass().getName().equals(skillClass.getName()))
            {
                it.remove();
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected abstract void AdjustSpeed(float delta, float maxSpeed);
    //-------------------------------------------------------------------------------------------------------------------------

    boolean IsFollowingPlayer()
    {
        return (FollowSkill != null) && FollowSkill.IsFollowing();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    void TurnToPlayer()
    {
        if (Enemy.GetDirection() == PlatformerObject.MoveDirection.MD_LEFT)
        {
            if (PlayerPos.x > EnemyPos.x)
            {
                Enemy.SetDirection(PlatformerObject.MoveDirection.MD_RIGHT);
            }
        }
        else if (PlayerPos.x < EnemyPos.x)
        {
            Enemy.SetDirection(PlatformerObject.MoveDirection.MD_LEFT);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(GameObject object, float delta)
    {
        if (!Enemy.IsUpdatable() || Enemy.IsDying())
        {
            return;
        }

        // Update enemy and player positions
        EnemyPos.set(Enemy.GetTrueX() + Enemy.getWidth() * .5f, Enemy.GetTrueY() + Enemy.getHeight() * .5f);

        if (Enemy.GetState().GetPlayerPos(PlayerPos))
        {
            if (IsFollowingPlayer())
            {
                // Keep look at player
                TurnToPlayer();

                if (CheckNearbyAttack())
                {
                    if (!Attacking)
                    {
                        Enemy.SetState(PlatformerObject.ES_ATTACK);
                        Attacking = true;
                    }
                }
                else
                {
                    if (Attacking)
                    {
                        Attacking = false;
                    }
                }
            }
        }

        AdjustSpeed(delta, Enemy.GetWalkSpeed());

        for (EnemySkill skill : Skills)
        {
            skill.Update(delta);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Remove(GameObject object)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RenderDebug(ShapeRenderer shapes)
    {
        for (EnemySkill skill : Skills)
        {
            skill.RenderDebug(shapes);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    void SetDesiredSpeed(float vx, float vy)
    {
        DesiredSpeed.set(vx, vy);

        // Method 1: Simple set desired speed
        PhysController.SetSpeed(DesiredSpeed.x, DesiredSpeed.y);

        //final Vector2 currentSpd = PhysController.GetSpeed();
        //final float speedDiff = DesiredSpeed.x - currentSpd.x;

        // Method 2: Use force
        //float force = PhysController.GetMass() * speedDiff / (1f / 60f);
        //PhysController.SetForce(force, 0f);

        // Method 3: Use impulse
        //final float impulse = PhysController.GetMass() * speedDiff;
        //final Vector2 impulsePos = PhysController.GetBody().getWorldCenter();
        //PhysController.Impulse(impulse, 0, impulsePos.x, impulsePos.y);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
