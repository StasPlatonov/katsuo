package bmg.katsuo.utils;

public class ControlValue<T>
{
    float Life;
    T Value;

    public ControlValue(float life, T value)
    {
        Life = life;
        Value = value;
    }

    public float GetLife()
    {
        return Life;
    }

    public T GetValue()
    {
        return Value;
    }
}
