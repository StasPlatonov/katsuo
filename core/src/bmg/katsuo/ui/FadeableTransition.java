package bmg.katsuo.ui;

import bmg.katsuo.IApplication;
import bmg.katsuo.render.Renderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;

public class FadeableTransition implements Screen
{
    protected IApplication App;
    static Renderer Ren;

    public static class TransitionEffect
    {
        protected float Time;
        protected float Duration;

        protected float getAlpha() {
            return Time / Duration;
        }

        public TransitionEffect(float duration)
        {
            Duration = duration;
            Time = 0f;
        }

        public void update(float delta)
        {
            Time += delta;
            if (Time > Duration)
            {
                Time = Duration;
            }
        }

        public void render(Screen current, Screen next, float delta) {

        }

        public boolean isFinished() {
            return Time >= Duration;
        }
    }

    public static class FadeOutTransitionEffect extends TransitionEffect
    {
        Color color = new Color();

        public FadeOutTransitionEffect(float duration) {
            super(duration);
        }

        Matrix4 proj = new Matrix4();

        @Override
        public void render(Screen current, Screen next, float delta)
        {
            current.render(delta); // keep updating current screen
            color.set(0f, 0f, 0f, getAlpha()); // from 0 to 1
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            proj.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Ren.BeginShapes(proj, ShapeRenderer.ShapeType.Filled, color);
            Ren.DrawRect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Ren.EndShapes();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }

    public static class FadeInTransitionEffect extends TransitionEffect
    {
        Color color = new Color();

        public FadeInTransitionEffect(float duration)
        {
            super(duration);
        }

        Matrix4 proj = new Matrix4();

        @Override
        public void render(Screen current, Screen next, float delta)
        {
            next.render(0f * delta); // do not updating next screen (avoid visual reset when next screen will be shown)
            color.set(0f, 0f, 0f, 1f - getAlpha()); // from 1 to 0

            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            proj.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Ren.BeginShapes(proj, ShapeRenderer.ShapeType.Filled, color);
            Ren.DrawRect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Ren.EndShapes();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }

    protected Screen current;
    protected Screen next;

    int currentTransitionEffect;
    ArrayList<TransitionEffect> transitionEffects;
    int lastTransitionEffect;

    public FadeableTransition(IApplication app, Screen current, Screen next, ArrayList<TransitionEffect> transitionEffects)
    {
        App = app;
        Ren = App.GetRenderer();

        this.current = current;
        this.next = next;
        this.transitionEffects = transitionEffects;
        this.currentTransitionEffect = 0;
    }

    private void update(float delta)
    {
        if (currentTransitionEffect >= transitionEffects.size())
        {
            current.hide();
            next.show();

            onTransitionFinished();

            // After complete keep update last effect
            TransitionEffect lastEffect = transitionEffects.get(lastTransitionEffect);
            lastEffect.update(delta);
            return;
        }

        TransitionEffect currentEffect = transitionEffects.get(currentTransitionEffect);

        currentEffect.update(delta);

        if (currentEffect.isFinished())
        {
            lastTransitionEffect = currentTransitionEffect;
            ++currentTransitionEffect;
        }
    }

    protected void onTransitionFinished()
    {
    }

    @Override
    public final void render(float delta)
    {
        update(delta);

        if (currentTransitionEffect >= transitionEffects.size())
        {
            // After complete keep render last effect to avoid showing empty screen for one frame (while next effect is in queue)
            transitionEffects.get(lastTransitionEffect).render(current, next, delta);
            return;
        }
        transitionEffects.get(currentTransitionEffect).render(current, next, delta);
    }

    @Override
    public void show()
    {
        /*if (current != null)
        {
            current.show();
        }
        next.show();*/
    }

    @Override
    public void hide()
    {
        /*if (current != null)
        {
            current.hide();
        }
        next.hide();*/
    }

    @Override
    public void resize(int w, int h)
    {
        if (current != null)
        {
            current.resize(w, h);
        }
        next.resize(w, h);
    }

    @Override
    public void pause()
    {
        if (current != null)
        {
            current.pause();
        }
        next.pause();
    }

    @Override
    public void resume()
    {
        if (current != null)
        {
            current.resume();
        }
        next.resume();
    }

    @Override
    public void dispose()
    {

    }
}
