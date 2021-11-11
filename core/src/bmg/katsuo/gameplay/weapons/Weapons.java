package bmg.katsuo.gameplay.weapons;

import java.util.HashMap;
import java.util.Map;

public class Weapons
{
    public enum WeaponType
    {
        WEAPON_BULLET,
        WEAPON_BEAM,
        WEAPON_GRENADE,
        WEAPON_SMOKE_GRENADE,
        WEAPON_BOMB
    }

    public static Map<WeaponType, String> WeaponIcons = new HashMap<WeaponType, String>();

    public Weapons()
    {
        WeaponIcons.put(WeaponType.WEAPON_BULLET, "");
        WeaponIcons.put(WeaponType.WEAPON_GRENADE, "");
        WeaponIcons.put(WeaponType.WEAPON_SMOKE_GRENADE, "");
        WeaponIcons.put(WeaponType.WEAPON_BEAM, "");
        WeaponIcons.put(WeaponType.WEAPON_BOMB, "");
    }

    public static WeaponType FromString(String type)
    {
        if (type.equals("bullet"))
            return WeaponType.WEAPON_BULLET;
        if (type.equals("grenade"))
            return WeaponType.WEAPON_GRENADE;
        if (type.equals("smokegrenade"))
            return WeaponType.WEAPON_SMOKE_GRENADE;
        if (type.equals("beam"))
            return WeaponType.WEAPON_BEAM;
        if (type.equals("bomb"))
            return WeaponType.WEAPON_BOMB;

        return WeaponType.WEAPON_BULLET;
    }

    public static String ToUserString(WeaponType type)
    {
        switch (type)
        {
            case WEAPON_BULLET:
                return "weapon_pistol";
            case WEAPON_GRENADE:
                return "weapon_grenade";
            case WEAPON_SMOKE_GRENADE:
                return "weapon_smoke";
            case WEAPON_BEAM:
                return "weapon_beamgun";
            case WEAPON_BOMB:
                return "weapon_bomb";
        }
        return "weapon_pistol";
    }

    public static String ToHelperImage(WeaponType type)
    {
        switch (type)
        {
            case WEAPON_BULLET:
                return "pistol-icon";
            case WEAPON_GRENADE:
                return "grenade-icon";
            case WEAPON_SMOKE_GRENADE:
                return "smoke-grenade-icon";
            case WEAPON_BEAM:
                return "beamgun-icon";
            case WEAPON_BOMB:
                return "bomb-icon";
        }
        return "pistol-icon";
    }

}
