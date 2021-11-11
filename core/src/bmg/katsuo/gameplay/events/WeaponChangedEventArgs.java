package bmg.katsuo.gameplay.events;

import bmg.katsuo.gameplay.weapons.WeaponMeasure;
import bmg.katsuo.gameplay.weapons.Weapons;

public class WeaponChangedEventArgs implements EventArgs
{
    public Weapons.WeaponType WType;
    public WeaponMeasure Measure;

    public WeaponChangedEventArgs(Weapons.WeaponType type, WeaponMeasure measure)
    {
        WType = type;
        //Count = count;
        Measure = measure;
    }
}