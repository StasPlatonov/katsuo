package bmg.katsuo.gameplay.weapons;

import bmg.katsuo.IApplication;
import bmg.katsuo.systems.particles.ParticleEmitterEx;
import bmg.katsuo.systems.particles.ParticleEmitterSettings;
import bmg.katsuo.systems.particles.ParticleSystem;
import bmg.katsuo.ui.AtlasSpriteEx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;

import java.util.UUID;

public class ExplosionEffect
{
    private String Id;
    private boolean Exploding = false;
    private AtlasSpriteEx ExplosionSprite;
    private float ExplosionLife;
    private final static float ExplosionLifeMax = 0.1f;
    private float ExplosionMaxScale;

    private boolean ExplosionComplete = false;

    private IApplication App;
    private float ScaleMin;
    private float ScaleMax;
    private Sound ExplodeSound;
    private long ExplodeSoundId;

    private float Scale;
    private Color Col = new Color(Color.RED);

    private ParticleSystem PartSystem;
    private ParticleEmitterSettings EmitSettings = null;
    private ParticleEmitterEx Emit = null;
    //-------------------------------------------------------------------------------------------------------------------------

    public ExplosionEffect(IApplication app, float scaleMin, float scaleMax, String particleClass, String sound)
    {
        Id = UUID.randomUUID().toString();

        App = app;
        ScaleMin = scaleMin;
        ScaleMax = scaleMax;

        AtlasSpriteEx sprite = (AtlasSpriteEx)App.GetState().GetSprite("whitetexture");
        ExplosionSprite = new AtlasSpriteEx(sprite.getAtlasRegion());

        PartSystem = App.GetState().GetPlaygroundLayer().GetSystem(ParticleSystem.class);
        if (PartSystem != null)
        {
            final MapProperties particlesProps = App.GetGamePlay().GetClassProperties(particleClass);
            EmitSettings = new ParticleEmitterSettings(particlesProps);
        }

        App.Log("ExplosionEffect", "Create explosion effect " + Id);

        ExplodeSound = app.GetSound(sound);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        App.Log("ExplosionEffect", "Dispose explosion effect " + Id);
        if (Emit != null)
        {
            PartSystem.RemoveEmitter(Emit);
            Emit = null;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean IsExploding()
    {
        return Exploding;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void draw(Batch batch, float parentAlpha)
    {
        if (Exploding && !ExplosionComplete)
        {
            ExplosionSprite.setColor(Col);
            ExplosionSprite.setScale(Scale, Scale);
            ExplosionSprite.setAlpha(Col.a * parentAlpha);

            ExplosionSprite.draw(batch, parentAlpha);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Explode(float x, float y, float wid, float hei)
    {
        Exploding = true;
        ExplosionLife = 0f;

        ExplosionSprite.setPosition(x, y);
        ExplosionSprite.setSize(wid, hei);

        ExplosionSprite.setRotation(MathUtils.random(0, 90));

        ExplosionSprite.setOriginCenter();
        ExplosionSprite.setFlip(false, false);

        ExplosionMaxScale = MathUtils.random(ScaleMin, ScaleMax);

        if (ExplodeSound != null)
        {
            ExplodeSoundId = ExplodeSound.play();
            App.AdjustSoundVolume(ExplodeSound, ExplodeSoundId, x, y);
        }

        if (EmitSettings != null)
        {
            Emit = PartSystem.CreateEmitter(EmitSettings, x, y);
        }

        float expSize = ExplosionMaxScale * ExplosionSprite.getWidth(); // for a while

        float expStrength = ExplosionMaxScale *.5f;

        App.ShakeEffect(x, y, expStrength, expSize, ExplosionLifeMax * 5f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean IsComplete()
    {
        if (!ExplosionComplete)
        {
            return false;
        }

        return (Emit != null) ? Emit.IsComplete() : true;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean Update(float delta)
    {
        if (Exploding)
        {
            if (ExplosionLife >= ExplosionLifeMax)
            {
                ExplosionComplete = true;
                ExplosionLife = ExplosionLifeMax;
            }
            else
            {
                float k = ExplosionLife / ExplosionLifeMax;

                Col.a = 1f - k;

                Scale = ExplosionMaxScale * k;

                ExplosionLife += delta;
            }
        }

        if (IsComplete())
        {
            if (Emit != null)
            {
                PartSystem.RemoveEmitter(Emit);
                Emit = null;
            }
        }
        return IsComplete();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
