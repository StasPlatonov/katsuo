package bmg.katsuo.objects;

public class Types
{
    public final static short CATEGORY_DEFAULT = 1;

    public final static short MASK_DEFAULT = -1;

    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1 << 1; // ground
    public static final short PLAYER_BIT = 1 << 2; // player
    public static final short ENEMY_LETHAL_BIT = 1 << 3; // kill player
    public static final short ENEMY_WEAKNESS_BIT = 1 << 4; // damage player
    public static final short ITEM_BIT = 1 << 5; // pickable items
    public static final short WEAPON_BIT = 1 << 6; // weapons
    public static final short BACKGROUND_BIT = 1 << 7;
    public static final short SHADOW_CASTER_BIT = 1 << 8; // lightable
    public static final short TRIGGER_BIT = 1 << 9;
    public static final short BREAKABLE_BIT = 1 << 10;
    public static final short WEATHER_BIT = 1 << 11;
    public static final short LADDER_BIT = 1 << 12;
    public static final short LIGHTS_BIT = 1 << 13;

    public enum DamageType
    {
        DAMAGE_TOUCH,
        DAMAGE_BULLET,
        DAMAGE_EXPLODE,
        DAMAGE_BEAM,
        DAMAGE_DEATH_ZONE
    }

    public final static int JUMP_SOUND = 1;
    public final static int HIT_SOUND = 2;
    public final static int DIE_SOUND = 3;
    public final static int WEAPON_SWITCH_SOUND = 4;
    public final static int SHOOT_SOUND = 4;

    public final static int TURRET_IDLE_SOUND = 10;
    public final static int TURRET_ARMED_SOUND = 11;
    public final static int TURRET_SHOOT_SOUND = 12;

    public final static int GROUND_HIT_SOUND = 20;

}
