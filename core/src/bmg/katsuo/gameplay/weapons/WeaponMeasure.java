package bmg.katsuo.gameplay.weapons;

public class WeaponMeasure
{
    public enum MeasureType
    {
        MEASURE_DISABLED,
        MEASURE_COUNT,
        MEASURE_INFINITE
    };

    public WeaponMeasure(MeasureType type)
    {
        this(type, 0, 0);
    }

    public WeaponMeasure(MeasureType type, int count, int maxCount)
    {
        Type = type;
        Count = count;
        MaxCount = maxCount;
    }

    public static WeaponMeasure Disabled(int maxCount)
    {
        return new WeaponMeasure(MeasureType.MEASURE_DISABLED, 0, maxCount);
    }

    public static WeaponMeasure None()
    {
        return Count(0, 0);
    }

    public static WeaponMeasure Infinite()
    {
        return new WeaponMeasure(MeasureType.MEASURE_INFINITE, -1, 0);
    }

    public static WeaponMeasure Count(int count)
    {
        return Count(count, 0);
    }

    public static WeaponMeasure Count(int count, int maxCount)
    {
        return new WeaponMeasure(MeasureType.MEASURE_COUNT, count, maxCount);
    }

    public MeasureType Type;
    public int Count;
    public int MaxCount;
}