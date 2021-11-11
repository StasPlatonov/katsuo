package bmg.katsuo.physics;

import bmg.katsuo.Globals;
import bmg.katsuo.objects.DynamicObject;
import bmg.katsuo.objects.GameLayer;
import bmg.katsuo.objects.GameObject;
import bmg.katsuo.objects.GameState;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import bmg.katsuo.ui.AtlasSpriteEx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PhysicalParticle extends DynamicObject
{
    private float LifeTime = 0f;
    private float MaxLifeTime = 1f;
    private float InitialOpacity = 1f;

    public PhysicalParticle(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, String spriteId)
    {
        this(layer, physics, x, y, width, height, spriteId, MathUtils.random(1.0f, 2.0f));
    }

    public PhysicalParticle(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, String spriteId, float maxLife)
    {
        super(layer, physics, x, y, width, height, spriteId);
        MaxLifeTime = maxLife;
    }

    public PhysicalParticle(GameLayer layer, Box2dPhysicsSystem physics, float x, float y, float width, float height, AtlasSpriteEx sprite, float maxLife)
    {
        super(layer, physics, x, y, width, height, sprite);
        MaxLifeTime = maxLife;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove()
    {
        return super.remove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void SetMaxLifeTime(float maxLifeTime)
    {
        MaxLifeTime = maxLifeTime;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetInitialOpacity(float opacity)
    {
        InitialOpacity = opacity;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void act(float delta)
    {
        LifeTime += delta;

        if (LifeTime > MaxLifeTime)
        {
            remove();
        }

        getColor().a = InitialOpacity * (1.0f - LifeTime / MaxLifeTime);

        super.act(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
