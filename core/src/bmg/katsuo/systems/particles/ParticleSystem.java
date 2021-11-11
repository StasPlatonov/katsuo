package bmg.katsuo.systems.particles;

import java.util.Comparator;

import bmg.katsuo.objects.GameObject;
import bmg.katsuo.systems.LayerSystem;
import bmg.katsuo.systems.particles.ParticleEmitterEx;
import bmg.katsuo.systems.particles.ParticleEmitterSettings;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import bmg.katsuo.controllers.ParticleController;
import bmg.katsuo.objects.GameObject.GameObjectChangeType;

public class ParticleSystem extends LayerSystem
{
    private ArrayMap<GameObject, ParticleController> Effects;
    private Array<PooledEffect> PreDraws;
    private Array<PooledEffect> PostDraws;
    private Array<ParticleEmitterEx> PostDrawsEx;

    private Array<ParticleEmitterEx> Emitters = new Array<ParticleEmitterEx>();

    private ParticleComparator Comparator;
    private Color TheColor;
    private boolean Enabled;
    //-------------------------------------------------------------------------------------------------------------------------

    public ParticleSystem()
    {
        Enabled = true;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        Effects = new ArrayMap<GameObject, ParticleController>();
        PreDraws = new Array<ParticleEffectPool.PooledEffect>();
        PostDraws = new Array<ParticleEffectPool.PooledEffect>();
        PostDrawsEx = new Array<ParticleEmitterEx>();
        Comparator = new ParticleComparator();
        TheColor = new Color(1f, 1f, 1f, 1f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectAdded(GameObject object, GameObject parent)
    {
        ParticleController pc = object.GetController(ParticleController.class);
        if (pc != null && !Effects.containsKey(object))
        {
            WhenObjectAdded(object, pc);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectChanged(GameObject object, int type, float value)
    {
        if (type != GameObjectChangeType.CONTROLLER)
        {
            return;
        }

        ParticleController pc = object.GetController(ParticleController.class);
        if (pc != null && !Effects.containsKey(object))
        {
            WhenObjectAdded(object, pc);
        }
        else if (pc == null && Effects.containsKey(object))
        {
            WhenObjectRemoved(object);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectRemoved(GameObject object, GameObject parent)
    {
        WhenObjectRemoved(object);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void WhenObjectAdded(GameObject object, ParticleController pc)
    {
        Effects.put(object, pc);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void WhenObjectRemoved(GameObject object)
    {
        if (Effects.containsKey(object))
        {
            Effects.removeKey(object);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public ParticleEmitterEx CreateEmitter(ParticleEmitterSettings settings, float x, float y)
    {
        ParticleEmitterEx emitter = new ParticleEmitterEx(GetLayer(), x, y, settings);

        Emitters.add(emitter);

        return emitter;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void RemoveEffect(PooledEffect pe)
    {
        while (PreDraws.removeValue(pe, true))
        {
            ;
        }
        while (PostDraws.removeValue(pe, true))
        {
            ;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RemoveEmitter(ParticleEmitterEx emitter)
    {
        PostDrawsEx.removeValue(emitter, true);

        Emitters.removeValue(emitter, true);

        emitter.dispose();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Render(Batch batch, float parentAlpha, Array<PooledEffect> effects)
    {
        if (!Enabled || (effects.size == 0))
        {
            return;
        }

        Color oldColor = batch.getColor();
        batch.setColor(TheColor.r, TheColor.g, TheColor.b, TheColor.a * parentAlpha);
        for (int i = 0; i < effects.size; i++)
        {
            PooledEffect pe = effects.get(i);

            pe.draw(batch);
        }

        batch.setColor(oldColor);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void RenderEx(Batch batch, float parentAlpha, Array<ParticleEmitterEx> effects)
    {
        if (!Enabled || (effects.size == 0))
        {
            return;
        }

        Color oldColor = batch.getColor();
        batch.setColor(TheColor.r, TheColor.g, TheColor.b, TheColor.a * parentAlpha);
        for (ParticleEmitterEx emitter: effects)
        {
            emitter.Render(batch);
        }

        batch.setColor(oldColor);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void PreDraw(Batch batch, float parentAlpha)
    {
        Render(batch, parentAlpha, PreDraws);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetEnabled(boolean enabled)
    {
        this.Enabled = enabled;
    }

    public boolean IsEnabled()
    {
        return Enabled;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void PostDraw(Batch batch, float parentAlpha)
    {
        Render(batch, parentAlpha, PostDraws);
        RenderEx(batch, parentAlpha, PostDrawsEx);

        RenderEx(batch, parentAlpha, Emitters);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta, float timeScale)
    {
        if (!Enabled)
        {
            return;
        }
        PreDraws.clear();
        PostDraws.clear();
        PostDrawsEx.clear();

        for (int i = 0; i < Effects.size; i++)
        {
            Array<PooledEffect> posta = Effects.getValueAt(i).GetPostDraw();
            Array<PooledEffect> prea = Effects.getValueAt(i).GetPreDraw();
            Array<ParticleEmitterEx> postaex = Effects.getValueAt(i).GetPostDrawEx();

            PostDraws.addAll(posta);
            PreDraws.addAll(prea);
            PostDrawsEx.addAll(postaex);
        }

        PreDraws.sort(Comparator);
        PostDraws.sort(Comparator);
        //PostDrawsEx.sort(Comparator);

        for (ParticleEmitterEx emitter : Emitters)
        {
            emitter.Update(delta * timeScale);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnRemove()
    {
        while (Emitters.size > 0)
        {
            RemoveEmitter(Emitters.get(0));
        }
        Effects.clear();
        PostDraws.clear();
        PreDraws.clear();
        PostDrawsEx.clear();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private static boolean ContainsAdditive(PooledEffect effect)
    {
        boolean additive = false;
        Array<ParticleEmitter> emmiters = effect.getEmitters();
        for (int i = 0; i < emmiters.size; i++)
        {
            if (emmiters.get(i).isAdditive())
            {
                additive = true;
                break;
            }
        }
        return additive;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void DrawDebug(ShapeRenderer shapes)
    {
        if (!Enabled)
        {
            return;
        }
        for (int i = 0; i < PreDraws.size; i++)
        {
            BoundingBox bb = PreDraws.get(i).getBoundingBox();

            shapes.rect(bb.getCenterX() - bb.getWidth() / 2, bb.getCenterY() - bb.getHeight() / 2, bb.getWidth(), bb.getHeight());
        }

        for (int i = 0; i < PostDraws.size; i++)
        {
            BoundingBox bb = PostDraws.get(i).getBoundingBox();

            shapes.rect(bb.getCenterX() - bb.getWidth() / 2, bb.getCenterY() - bb.getHeight() / 2, bb.getWidth(), bb.getHeight());
        }

        for (int i = 0; i < PostDrawsEx.size; i++)
        {
            PostDrawsEx.get(i).RenderDebug(shapes);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static class ParticleComparator implements Comparator<PooledEffect>
    {
        @Override
        public int compare(PooledEffect o1, PooledEffect o2)
        {
            boolean o1_add = false;
            boolean o2_add = false;
            o1_add = ContainsAdditive(o1);
            o2_add = ContainsAdditive(o2);
            if (o1_add && !o2_add)
            {
                return 11;
            }
            else if (o2_add && !o1_add)
            {
                return -1;
            }
            return 0;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
