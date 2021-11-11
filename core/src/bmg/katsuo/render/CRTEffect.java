package bmg.katsuo.render;

import bmg.katsuo.managers.ResourcesCollection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class CRTEffect extends PostProcessingEffect
{
    private int OffsetParam;
    private float Offset = 0.002f;
    private enum EffectState
    {
        STATE_WAITING,
        STATE_PHASING,
    }

    private EffectState State = EffectState.STATE_WAITING;
    private int NumPulses = 1;
    private float PulseAmplitude = 0f;
    private float PulseTime = 0f;
    private float Degree = 0f;
    private float WaitingTime = 0f;
    //----------------------------------------------------------------------------------------------

    public CRTEffect(ResourcesCollection resources)
    {
        super(resources, "crt");

        if (Shader != null)
        {
            OffsetParam = Shader.getUniformLocation("offset");
        }
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        super.Update(delta);

        if (State == EffectState.STATE_WAITING)
        {
            WaitingTime -= delta;
            if (WaitingTime <= 0f)
            {
                WaitingTime = 0f;
                State = EffectState.STATE_PHASING;
                Degree = 0f;
                NumPulses = 1;
                PulseAmplitude = MathUtils.random(0.001f, 0.006f);
                PulseTime = MathUtils.random(2.0f, 4.0f);
            }
        }
        else if (State == EffectState.STATE_PHASING)
        {
            Degree = (Degree + 180f * delta * PulseTime);
            Offset = 0.002f + PulseAmplitude * Math.abs(MathUtils.sinDeg(Degree));

            if (Degree > 180f * NumPulses)
            {
                WaitingTime = MathUtils.random(2f, 5f);
                State = EffectState.STATE_WAITING;
            }
        }
    }
    //----------------------------------------------------------------------------------------------

    @Override
    protected void ApplyShaderParameters(Texture lightmap)
    {
        super.ApplyShaderParameters(lightmap);

        if (Shader != null)
        {
            Shader.setUniformf(OffsetParam, Offset);
        }
    }
    //----------------------------------------------------------------------------------------------

}
