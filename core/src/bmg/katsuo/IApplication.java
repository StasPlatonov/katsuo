package bmg.katsuo;

import bmg.katsuo.localization.Localization;
import bmg.katsuo.gameplay.*;
import bmg.katsuo.gameplay.objects.PlayerState;
import bmg.katsuo.input.AppInput;
import bmg.katsuo.managers.*;
import bmg.katsuo.network.ICloudServices;
import bmg.katsuo.network.ServerSelector;
import bmg.katsuo.objects.GameState;
import bmg.katsuo.render.Renderer;
import bmg.katsuo.render.TileLayerRenderer;
import bmg.katsuo.objects.IGameObjectFactory;
import bmg.katsuo.scripts.IScriptManager;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;

public interface IApplication
{
    boolean DEBUG_LOGGING = true;
    String NULL_STRING = "NULL";
    String COLON = ":";
    String NOT_SUPPORTED = "[NOT SUPPORTED]";

    class RenderOptions
    {
        RenderOptions()
        {
            Debug = false;
            UI = true;
            UIDebug = false;
            Entities = true;
            EntitiesDebug = false;
            Profiler = false;
            PhysicsDebug = false;
            LightsDebug = false;
        }

        public boolean Debug;

        public boolean UI;
        public boolean UIDebug;
        public boolean Entities;
        public boolean EntitiesDebug;
        public boolean Profiler;
        public boolean PhysicsDebug;
        public boolean LightsDebug;
        public boolean Background;
        public boolean Foreground;

    };

    Skin GetSkin();
    RenderOptions GetRenderOptions();
    Localization GetLocale();
    ResourceManager GetResources();
    Renderer GetRenderer();
    TileLayerRenderer GetTileRender();
    Preferences GetPreferences();
    AppInput GetInput();
    IGameObjectFactory GetObjectsFactory();
    Viewport GetViewport();
    GameState GetState();
    GamePlay GetGamePlay();
    SoundManager GetSoundManager();
    ContentManager GetContentManager();
    ScoreManager GetScoreManager();
    ServerSelector GetServerSelector();
    IScriptManager GetScriptManager();
    CharacterManager GetCharacterManager();

    void SetDebug(boolean debug);
    void DebugSpawnObject(String typeName, float x, float y);
    String GetLoadingDescription();

    void LoadMap(final String levelId, final String mapName);
    void UnloadMap(boolean lazy);
    void LoadLevel(final String levelName);

    void Debug(String tag, String msg);
    void Log(String tag, String msg);
    void Error(String tag, String msg);

    enum ScreenType
    {
        SCREEN_LOADING,
        SCREEN_MENU,
        SCREEN_GAME,
        SCREEN_SETTINGS,
        SCREEN_HIGHSCORES,
        SCREEN_LEVELS,
        SCREEN_CUSTOM_LEVELS,
        SCREEN_LEVEL_COMPLETE,
        SCREEN_GAME_COMPLETE,
        SCREEN_ABOUT,
        SCREEN_PLAYER_KILLED
    }
    Screen ShowScreen(ScreenType type, Map<String, String> properties, boolean instant);
    Screen ShowScreen(ScreenType type, boolean instant);
    Screen GetScreen(ScreenType type);
    Screen GetCurrentScreen();

    void ToggleConsole();

    void NextLevel(String currentLevelId);
    String GetLocalizedString(String id, Object... args);

    SoundsCollection GetSoundsCollection();
    long PlaySound(String soundId, boolean loop);
    long PlaySoundAt(String soundFile, float soundX, float soundY, boolean loop);
    void StopSound(String soundId, long id);
    Sound GetSound(String soundId);
    Array<String> GetSoundFiles(String soundId);
    void PlayMusic(String musicId, boolean loop);
    void AdjustSoundVolume(Sound sound, long soundId, float soundX, float soundY);

    PlayerState GetPlayerState();

    ResourcesCollection GetCommonResources();

    GameEvents GetEvents();

    void SetPaused(boolean paused);
    boolean IsPaused();

    Settings GetSettings();
    ICloudServices GetCloudServices();

    void FocusCamera(String objectName, boolean focus);

    void ShakeEffect(float x, float y, float strength, float distance, float duration);

    //void ShowHelper(String image, String text);

    enum MessageType
    {
        MT_INFO,
        MT_WARNING,
        MT_ERROR
    };
    void Message(String text, String title, float duration, MessageType type);
}
