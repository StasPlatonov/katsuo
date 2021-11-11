package bmg.katsuo;

import bmg.katsuo.network.NetworkServiceFactory;
import com.badlogic.gdx.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings
{
    private Preferences Prefs;

    // ------------------ Public settings
    private String PlayerName;
    private float MusicVolume = 0.25f;
    private float SoundsVolume = 0.25f;

    public static final int SKILL_EASY = 0;
    public static final int SKILL_NORMAL = 1;
    public static final int SKILL_HARD = 2;
    public static final int SKILL_INSANE = 3;
    private int Skill = SKILL_EASY;

    private String Language;

    public enum PlayerControlsType
    {
        CONTROLS_JOYSTICK,
        CONTROLS_BUTTONS
    };
    private PlayerControlsType ControlsType = PlayerControlsType.CONTROLS_BUTTONS;

    public enum PlayerHandType
    {
        LEFT_HANDED,
        RIGHT_HANDED
    };
    private PlayerHandType HandType = PlayerHandType.RIGHT_HANDED;

    // --------------   Hidden settings
    private int LaunchNumber = 0;
    private boolean Debug = false;

    public static final int LOG_CATEGORY_ERROR = 0;
    public static final int LOG_CATEGORY_WARNING = 1;
    public static final int LOG_CATEGORY_INFORMATION = 2;
    public static final int LOG_CATEGORY_DEBUG = 3;
    private int LogCategory = LOG_CATEGORY_ERROR;
    private int LogLevel = 0;

    private String Servers;

    private Date LastLaunch;

    private boolean AdsEnabled = false;
    private int ReachedLevel = 0;

    public enum LightingTypes
    {
        LIGHTING_BOX2D,
        LIGHTING_CUSTOM
    };

    public LightingTypes GetLightingType()
    {
        return LightingType;
    }

    public void SetLightingType(LightingTypes lightingType)
    {
        LightingType = lightingType;
    }

    private LightingTypes LightingType = LightingTypes.LIGHTING_BOX2D;

    //----------------------------------------------------------------------------------------------

    Settings(Preferences prefs)
    {
        Prefs = prefs;

        Load();
    }
    //----------------------------------------------------------------------------------------------

    void dispose()
    {
        Save();
    }
    //----------------------------------------------------------------------------------------------

    private void Load()
    {
        // ------------ Public settings
        PlayerName = Prefs.getString("Name", "Player");
        MusicVolume = Prefs.getFloat("MusicVolume", 0.5f);
        SoundsVolume = Prefs.getFloat("SoundsVolume", 0.5f);
        //Controls = Prefs.getInteger("Controls", 0);
        Skill = Prefs.getInteger("Skill", SKILL_EASY);
        Language = Prefs.getString("Language", "en");

        // ------------ Hidden settings
        LaunchNumber = Prefs.getInteger("LaunchNumber", 0);
        ++LaunchNumber;

        Servers = Prefs.getString("Servers", NetworkServiceFactory.DefaultServers);

        Debug = Prefs.getBoolean("Debug", false);
        LogCategory = Prefs.getInteger("LogCategory", LOG_CATEGORY_ERROR);
        LogLevel = Prefs.getInteger("LogLevel", 0);

        try
        {
            LastLaunch = Fmt.parse(Prefs.getString("LastLaunch", Fmt.format(new Date())));
        }
        catch (Exception e)
        {
            LastLaunch = new Date();
        }

        AdsEnabled = Prefs.getBoolean("AdsEnabled", true);

        ReachedLevel = Prefs.getInteger("ReachedLevel", 0);

        //LightingType = LightingTypes.LIGHTING_BOX2D;
        LightingType = LightingTypes.LIGHTING_CUSTOM;
    }
    //----------------------------------------------------------------------------------------------

    private final static SimpleDateFormat Fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    public void Save()
    {
        // ------- Public settings
        Prefs.putString("Name", PlayerName);
        Prefs.putFloat("MusicVolume", MusicVolume);
        Prefs.putFloat("SoundsVolume", SoundsVolume);
        //Prefs.putInteger("Controls", Controls);
        Prefs.putInteger("Skill", Skill);
        Prefs.putString("Language", Language);

        // ------- Hidden settings
        Prefs.putInteger("LaunchNumber", LaunchNumber);
        Prefs.putString("Servers", Servers);
        Prefs.putBoolean("Debug", Debug);
        Prefs.putInteger("LogCategory", LogCategory);
        Prefs.putInteger("LogLevel", LogLevel);

        Prefs.putString("LastLaunch", Fmt.format(LastLaunch));

        Prefs.putBoolean("AdsEnabled", AdsEnabled);
        Prefs.putInteger("ReachedLevel", ReachedLevel);

        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public String getPlayerName()
    {
        return PlayerName;
    }

    public void setPlayerName(String name)
    {
        PlayerName = name;
        Prefs.putString("Name", PlayerName);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public int getLaunchNumber() { return LaunchNumber; }

    public float getMusicVolume() { return MusicVolume; }
    public void setMusicVolume(float volume)
    {
        MusicVolume = volume;
        Prefs.putFloat("MusicVolume", MusicVolume);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public float getSoundsVolume() { return SoundsVolume; }
    public void setSoundsVolume(float volume)
    {
        SoundsVolume = volume;
        Prefs.putFloat("SoundsVolume", SoundsVolume);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public boolean getDebug() { return Debug; }
    public void setDebug(boolean debug)
    {
        Debug = debug;
        Prefs.putBoolean("Debug", Debug);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public int getSkill()
    {
        return Skill;
    }

    public void setSkill(int skill)
    {
        Skill = skill;
        Prefs.putInteger("Skill", Skill);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public String getLanguage()
    {
        return Language;
    }

    public void setLanguage(String language)
    {
        Language = language;
        Prefs.putString("Language", Language);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public int getLogCategory()
    {
        return LogCategory;
    }

    public void setLogCategory(int cat)
    {
        LogCategory = cat;
        Prefs.putInteger("LogCategory", LogCategory);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public int getLogLevel()
    {
        return LogLevel;
    }

    public void setLogLevel(int lev)
    {
        LogLevel = lev;
        Prefs.putInteger("LogLevel", LogLevel);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public String getServers()
    {
        return Servers;
    }

    public void setServers(String servers)
    {
        Servers = servers;
        if (Servers.isEmpty())
        {
            Servers = NetworkServiceFactory.DefaultServers;
        }
        Prefs.putString("Servers", Servers);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public PlayerControlsType GetControlsType()
    {
        return ControlsType;
    }
    //----------------------------------------------------------------------------------------------

    public PlayerHandType GetHandType()
    {
        return HandType;
    }
    //----------------------------------------------------------------------------------------------

    public boolean IsAdsEnabled()
    {
        return AdsEnabled;
    }

    public void SetAdsEnabled(boolean enabled)
    {
        AdsEnabled = enabled;
        Prefs.putBoolean("AdsEnabled", AdsEnabled);
        Prefs.flush();
    }
    //----------------------------------------------------------------------------------------------

    public int GetReachedLevel()
    {
        return ReachedLevel;
    }

    public void SetReachedLevel(int level)
    {
        ReachedLevel = level;
        Prefs.putInteger("ReachedLevel", ReachedLevel);
        Prefs.flush();
    }
}
