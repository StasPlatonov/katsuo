package bmg.katsuo.systems;

import bmg.katsuo.objects.GameLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import bmg.katsuo.utils.Priority;

public abstract class AbstractSystem implements Priority
{
    private int ThePriority;
    private boolean Active;
    private GameLayer Layer;
    //-------------------------------------------------------------------------------------------------------------------------

    public AbstractSystem()
    {
        this(0);
    }

    public AbstractSystem(int priority)
    {
        this.ThePriority = priority;
        this.Active = true;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public int GetPriority()
    {
        return ThePriority;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetActive(boolean active)
    {
        this.Active = active;
    }

    public boolean IsActive()
    {
        return Active;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void InternalAddToLayer(GameLayer layer)
    {
        this.Layer = layer;
        AddedToLayer(layer);
    }

    public void InternalRemoveFromLayer(GameLayer layer)
    {
        this.Layer = null;
        RemovedFromLayer(layer);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public GameLayer GetLayer()
    {
        return Layer;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void DrawDebug(ShapeRenderer shapes)
    {
        /*TODO: override*/
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void PreDraw(Batch batch, float parentAlpha)
    {
        /* TODO: override */
    }

    public void PostDraw(Batch batch, float parentAlpha)
    {
        /* TODO:override */
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public abstract void Update(float delta, float timeScale);
    //-------------------------------------------------------------------------------------------------------------------------

    public abstract void AddedToLayer(GameLayer layer);

    public abstract void RemovedFromLayer(GameLayer layer);
    //-------------------------------------------------------------------------------------------------------------------------
}
