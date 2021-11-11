package bmg.katsuo.gameplay.enemies;

import bmg.katsuo.IApplication;
import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.objects.GameLayer;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class EnemySkill
{
    protected EnemyObject Enemy;
    protected IApplication App;
    protected Box2dPhysicsSystem ThePhysics;
    protected GameLayer Playground;
    //-------------------------------------------------------------------------------------------------------------------------

    public EnemySkill(EnemyObject enemy)
    {
        Enemy = enemy;
        App = enemy.GetApp();
        Playground = enemy.GetLayer();

        //Layer does not exists here
        //ThePhysics = Playground.GetSystem(Box2dPhysicsSystem.class);
        Box2dController enemyPhys = enemy.GetController(Box2dController.class);
        ThePhysics = enemyPhys.GetPhysics();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public abstract void Update(float delta);
    //-------------------------------------------------------------------------------------------------------------------------

    public abstract void RenderDebug(ShapeRenderer shapes);
}
