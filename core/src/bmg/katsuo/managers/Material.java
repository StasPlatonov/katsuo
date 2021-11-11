package bmg.katsuo.managers;

import com.badlogic.gdx.graphics.Color;

public class Material
{
    public final static String DEFAULT_MATERIAL_NAME = "default";

    private String Name;
    private Color TheColor;

    private float Friction;
    private float Restitution;

    private String StepSound;
    private String HitSound;

    public Material()
    {
        this(DEFAULT_MATERIAL_NAME, Color.WHITE, 1f, 0f);
    }

    public Material(String name, Color color, float friction)
    {
        this(name, color, friction, 0f);
    }

    public Material(String name, Color color, float friction, float restitution)
    {
        this(name, color, friction, restitution, name + "-step", name + "-hit");
    }

    public Material(String name, Color color, float friction, float restitution, String stepSound, String hitSound)
    {
        Name = name;
        TheColor = new Color(color);

        Friction = friction;
        Restitution = restitution;

        StepSound = stepSound;
        HitSound = hitSound;
    }

    public String GetName()
    {
        return Name;
    }

    public void SetName(String name)
    {
        Name = name;
    }

    public Color GetColor()
    {
        return TheColor;
    }

    public void SetColor(Color theColor)
    {
        TheColor = theColor;
    }

    public float GetFriction()
    {
        return Friction;
    }

    public void SetFriction(float friction)
    {
        Friction = friction;
    }

    public float GetRestitution()
    {
        return Restitution;
    }

    public void SetRestitution(float restitution)
    {
        Restitution = restitution;
    }

    public String GetStepSound()
    {
        return StepSound;
    }

    public void SetStepSound(String stepSound)
    {
        StepSound = stepSound;
    }

    public String GetHitSound()
    {
        return HitSound;
    }

    public void SetHitSound(String hitSound)
    {
        HitSound = hitSound;
    }
}
