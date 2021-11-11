package bmg.katsuo.systems.particles;

import bmg.katsuo.utils.ControlValuesInterpolator;
import com.badlogic.gdx.graphics.Color;

public class ParticleAffectorEx
{
    public final static short AFFECT_NONE = 0x00;
    public final static short AFFECT_POSITION = 0x01;
    public final static short AFFECT_SCALE = 0x02;
    public final static short AFFECT_OPACITY = 0x04;
    public final static short AFFECT_COLOR = 0x08;
    public final static short AFFECT_ROTATION = 0x10;
    public final static short AFFECT_ALL = AFFECT_POSITION | AFFECT_SCALE | AFFECT_OPACITY | AFFECT_COLOR | AFFECT_ROTATION;

    private short Flags = AFFECT_NONE;

    ParticleEmitterSettings Settings;
    ControlValuesInterpolator interp = new ControlValuesInterpolator();
    //-------------------------------------------------------------------------------------------------------------------------

    public ParticleAffectorEx(ParticleEmitterSettings settings, short flags)
    {
        Settings = settings;
        Flags = flags;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Affect(ParticleEx particle, float delta)
    {
        if (Flags == AFFECT_NONE)
        {
            return;
        }

        float k = particle.GetLife() / particle.GetMaxLife(); // 0..1

        if ((Flags & AFFECT_POSITION) != 0)
        {
            if (!particle.IsPhysical())
            {
                particle.SetX(particle.GetX() + particle.GetVelX() * delta);
                particle.SetY(particle.GetY() + particle.GetVelY() * delta);

                particle.SetVelX(particle.GetVelX() + particle.GetAccX() * delta);
                particle.SetVelY(particle.GetVelY() + particle.GetAccY() * delta);
            }
        }

        if ((Flags & AFFECT_ROTATION) != 0)
        {
            if (!particle.IsPhysical())
            {
                particle.SetR(particle.GetR() + particle.GetVelR() * delta);
                particle.SetVelR(particle.GetVelR() + particle.GetAccR() * delta);
            }
        }

        if ((Flags & AFFECT_SCALE) != 0)
        {
            if (!particle.IsPhysical())
            {
                // @TODO: physical particles do not support scaling yet
                float scale = interp.GetFloat(Settings.GetScaleSettings().GetScalingValues(), k);
                particle.SetScale(scale);
            }
        }

        if ((Flags & AFFECT_OPACITY) != 0)
        {
            float op = interp.GetFloat(Settings.GetOpacitySettings().GetScalingValues(), k);
            particle.SetOpacity(op);
        }

        if ((Flags & AFFECT_COLOR) != 0)
        {
            final Color color = interp.GetColor(Settings.GetColorSettings().GetColorValues(), k);
            particle.SetColor(color);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
