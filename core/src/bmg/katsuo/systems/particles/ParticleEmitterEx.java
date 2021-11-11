package bmg.katsuo.systems.particles;

import bmg.katsuo.Globals;
import bmg.katsuo.objects.GameLayer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParticleEmitterEx
{
    private Pool<ParticleEx> ParticlesPool;
    private Array<ParticleEx> Particles = new Array<ParticleEx>();

    private GameLayer TheLayer;
    private Sprite TheSprite;
    private int SimultaneousParticlesCount;
    private long MaxParticles;
    private long TotalParticlesCounter = 0;
    private float X;
    private float Y;

    private ParticleAffectorEx Affector;

    private float Accumulator;

    private ParticleEmitterSettings EmSettings;

    private Vector2 CamPos = new Vector2();

    private float GenAccumulator;

    private long SoundId = 0;

    private boolean Running = true;
    //-------------------------------------------------------------------------------------------------------------------------

    public ParticleEmitterEx(GameLayer layer, float x, float y, ParticleEmitterSettings settings)
    {
        TheLayer = layer;
        TheSprite = TheLayer.GetState().GetSprite(settings.GetSprite());

        X = x;
        Y = y;

        EmSettings = settings;
        SimultaneousParticlesCount = EmSettings.GetMaxCount();
        MaxParticles = SimultaneousParticlesCount * EmSettings.GetIterations();

        final ParticleEmitterEx ps = this;

        ParticlesPool = new Pool<ParticleEx>((int)MaxParticles)//SimultaneousParticlesCount)
        {
            @Override
            protected ParticleEx newObject()
            {
                ParticleEx particle = new ParticleEx(ps, TheSprite, X, Y, EmSettings);

                ResetParticle(particle);

                return particle;
            }
        };

        Affector = new ParticleAffectorEx(EmSettings, ParticleAffectorEx.AFFECT_ALL);

        Accumulator = 0;

        if (!EmSettings.GetSound().isEmpty())
        {
            SoundId = TheLayer.GetApp().PlaySound(EmSettings.GetSound(), true);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void DestroyParticle(ParticleEx particle, boolean forever)
    {
        if (!Particles.contains(particle, true))
        {
            return;
        }

        particle.SetActive(false);
        Particles.removeValue(particle, true);
        ParticlesPool.free(particle);

        if (forever)
        {
            particle.dispose();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        TheLayer.GetApp().Log("ParticleEmitter", "Dispose particle emitter (SIM:" + SimultaneousParticlesCount + " ITER:" + EmSettings.GetIterations() + ")");
        if (SoundId != 0)
        {
            TheLayer.GetApp().StopSound(EmSettings.GetSound(), SoundId);
        }

        while (Particles.size > 0)
        {
            ParticleEx particle = Particles.get(0);
            DestroyParticle(particle, true);
        }

        ParticlesPool.clear();
        Particles.clear();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean IsComplete()
    {
        if (!Running && (Particles.size == 0)) // all emitted particles are dead
        {
            return true;
        }

        if (MaxParticles > 0)
        {
            if (TotalParticlesCounter >= MaxParticles) // no more particles to emitt
            {
                if (Particles.size == 0) // all emitted particles are dead
                {
                    return true;
                }
            }
        }

        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetPosition(float x, float y)
    {
        X = x;
        Y = y;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetX()
    {
        return X;
    }

    public float GetY()
    {
        return Y;
    }

    public GameLayer GetLayer()
    {
        return TheLayer;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Reset()
    {
        while (Particles.size > 0)
        {
            ParticleEx particle = Particles.get(0);
            DestroyParticle(particle, false);
        }
        Particles.clear();

        Accumulator = 0;

        GenAccumulator = 0f;

        TotalParticlesCounter = 0;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Vector2 InitialImpulse = new Vector2();
    public void SetInitialImpulse(float ix, float iy)
    {
        InitialImpulse.set(ix, iy);
    }

    private void ResetParticle(ParticleEx particle)
    {
        particle.Reset();

        float life = EmSettings.GetLifeSettings().GetRandomHigh();
        particle.SetMaxLifeTime(life);

        particle.SetScale(EmSettings.IsPhysical() ? EmSettings.GetScaleSettings().GetScalingValues().get(0).GetValue() : EmSettings.GetScaleSettings().GetRandomLow());

        float r = EmSettings.HasFixedRotation() ? 0 : MathUtils.random(0f, 45.0f);
        particle.SetTransform(X, Y, r);

        float vx = EmSettings.GetVelocityXSettings().GetRandom();
        float vy = EmSettings.GetVelocityYSettings().GetRandom();
        float vr = EmSettings.HasFixedRotation() ? 0f : EmSettings.GetVelocityRSettings().GetRandom();

        if (particle.IsPhysical())
        {
            particle.SetVelocity((vx + InitialImpulse.x) * Globals.PIXELS_TO_METERS, (vy + InitialImpulse.y) * Globals.PIXELS_TO_METERS, vr * Globals.PIXELS_TO_METERS);
        }
        else
        {
            particle.SetVelX(vx);
            particle.SetVelY(vy);
            particle.SetVelR(vr);

            particle.SetAccY(-9.8f * Globals.METERS_TO_PIXELS * EmSettings.GetGravityScale());
        }

        particle.SetActive(true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void CreateParticle()
    {
        ParticleEx particle = ParticlesPool.obtain();

        ResetParticle(particle);

        Particles.add(particle);
        if (MaxParticles != 0)
        {
            ++TotalParticlesCounter;
        }

        Accumulator = 0f;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RemoveParticle(ParticleEx particle)
    {
        if (EmSettings.IsContinuous())
        {
            if (Running)
            {
                // Immediately reset particle to emulate a new one
                ResetParticle(particle);
            }
            else
            {
                DestroyParticle(particle, true);
            }
        }
        else
        {
            DestroyParticle(particle, false);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Stop()
    {
        Running = false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private int NeedEmit = 0;

    public void NeedEmit(int count)
    {
        NeedEmit = count;
    }

    public void Emit(int count)
    {
        for (int i = 0; i < count; ++i)
        {
            CreateParticle();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Update(float delta)
    {
        // Update all particles
        for (ParticleEx particle : Particles)
        {
            if (!particle.IsActive())
            {
                continue;
            }

            Affector.Affect(particle, delta);
            particle.Update(delta);
        }

        if (EmSettings.IsEmitOnDemand())
        {
            if (NeedEmit > 0)
            {
                Emit(NeedEmit);
                NeedEmit = 0;
            }
            return;
        }

        if (!Running)
        {
            return;
        }

        // Check interval of generate
        if (GenAccumulator > 0f)
        {
            GenAccumulator -= delta;

            if (GenAccumulator > 0f)
            {
                return;
            }

            //@TODO: move from here
            if (!EmSettings.GetSound().isEmpty())
            {
                TheLayer.GetApp().PlaySound(EmSettings.GetSound(), false);
            }
        }

        // Check maximum count
        if (MaxParticles > 0)
        {
            if (TotalParticlesCounter >= MaxParticles)
            {
                return;
            }
        }

        if (Particles.size < SimultaneousParticlesCount)
        {
            int particlesToGenerate = SimultaneousParticlesCount - Particles.size;
            if (EmSettings.GetGenTime() == 0)
            {
                for (int i = 0; i < particlesToGenerate; ++i)
                {
                    CreateParticle();
                }
            }
            else
            {
                Accumulator += delta;

                float particleGenTime = EmSettings.GetGenTime() / SimultaneousParticlesCount;

                if (Accumulator > particleGenTime)
                {
                    int generateSlowlyCount = Math.max(particlesToGenerate, (int)(Accumulator / particleGenTime));

                    for (int i = 0; i < generateSlowlyCount; ++i)
                    {
                        CreateParticle();
                    }
                }
            }

            if (Particles.size == SimultaneousParticlesCount)
            {
                if (GenAccumulator <= 0)
                {
                    GenAccumulator = EmSettings.GetGenInterval();
                }
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RenderDebug(ShapeRenderer shapes)
    {
        GameLayer.LayerCamera cam = TheLayer.GetCamera();
        CamPos.set(cam.GetPosition());

        for (ParticleEx particle : Particles)
        {
            if (!particle.IsActive())
            {
                continue;
            }
            shapes.rect(particle.GetX() - particle.GetWidth() / 2 - CamPos.x, particle.GetY() - particle.GetHeight() / 2 - CamPos.y, particle.GetOriginX(), particle.GetOriginY(), particle.GetWidth(), particle.GetHeight(), particle.GetScale(), particle.GetScale(), particle.GetR());
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Render(Batch batch)
    {
        if (EmSettings.IsAdditive())
        {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        }
        else
        {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        for (ParticleEx particle : Particles)
        {
            if (!particle.IsActive())
            {
                continue;
            }
            particle.Render(batch);
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetDebugString()
    {
        return String.format("%02.01f", GenAccumulator) + ": " + Particles.size + "(CUR)/" + ParticlesPool.peak + "(PEEK)/" + TotalParticlesCounter + "(TOT)";
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
/*
public class ParticleEffectEx extends ParticleEffect
{
    @Override
    protected ParticleEmitter newEmitter(BufferedReader reader) throws IOException
    {
        return new ParticleEmitterEx(reader);
    }

    public GameLayer GetLayer()
    {
        return null;
    }

    public void RemoveParticle(ParticleEx particle)
    {}
}
*/