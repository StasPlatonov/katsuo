package bmg.katsuo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;

import java.text.SimpleDateFormat;

public class Globals
{
    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;
/*
    public static final float GUI_WIDTH = 1024f;
    public static final float GUI_HEIGHT = 768f;
    public static final float SCREEN_WIDTH = 1024f;
    public static final float SCREEN_HEIGHT = 768f;
*/
    public static final float BG_WIDTH = 1024f;
    public static final float BG_HEIGHT = 768f;

    // Xiaomi

    //Emulation in HP

    public static final float GUI_WIDTH = 1024f;
    public static final float GUI_HEIGHT = 768f;
    public static final float SCREEN_WIDTH = 720;
    public static final float SCREEN_HEIGHT = 360f;

    //public static final float SCREEN_WIDTH = 2160f;
    //public static final float SCREEN_HEIGHT = 1080f;

/*
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;
    public static final float GUI_WIDTH = 800;
    public static final float GUI_HEIGHT = 600;
    public static final float SCREEN_WIDTH = 640;//320;
    public static final float SCREEN_HEIGHT = 480;//240;
*/
    public static final float HEIGHT_IN_UNITS = 10f;
    public static final float PIXELS_PER_UNIT = 32.f;
    //public static final float PHYS_TO_SCREEN_SCALE = PIXELS_PER_UNIT;
    //public static final float PHYSICS_UNIT_SIZE = 1f;

    public static final float PIXELS_PER_METER = 100.0f;
    public static final float PIXELS_TO_METERS = 1.f / PIXELS_PER_METER;
    public static final float METERS_TO_PIXELS = PIXELS_PER_METER;

    public static final int UI_FONT_SIZE = 20;
    public static final int UI_LARGE_FONT_SIZE = 48;
    public static final int CONSOLE_FONT_SIZE = 18;
    public static final int GAMEPLAY_FONT_SIZE = 18;

    public static final float GAMEPLAY_TOP_PANEL_SIZE = 0.1f;
    public static final float GAMEPLAY_BUTTONS_SIZE = 0.4f;

    public static final float PHYSICS_CACHE_IMAGE_SIZE = 32f;

    public static final Color DEBUG_COLOR = Color.GREEN;

    public static final String AppFolder = "Katsuo";

    public static final String PLAYER_ID = "player";
    public static final String PLAYER_LOCATION = "player_location";
    public static final float PLAYER_SIZE = 32f;

    public final static SimpleDateFormat DateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    public final static SimpleDateFormat DateTimeFormatScores = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static final float DISTANCE_TO_HEAR = PIXELS_PER_UNIT * 20;

    public static final int DefaultUpdateInterval = 60 * 60 * 1000;//24 * 60 * 60 * 1000;

    public static boolean FullVersion = true;

    public static final int BANNER_ADS_INTERVAL = 1000 * 60 * 2;
}
