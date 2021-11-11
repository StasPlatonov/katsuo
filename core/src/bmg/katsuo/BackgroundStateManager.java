package bmg.katsuo;

import bmg.katsuo.gameplay.objects.PlayerObject;
import bmg.katsuo.managers.StateManager;
import bmg.katsuo.objects.*;
import bmg.katsuo.systems.*;
import bmg.katsuo.systems.particles.ParticleSystem;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class BackgroundStateManager extends StateManager
{
    private static final String TAG = "BackgroundStateManager";

    private IApplication App;
    private Box2dPhysicsSystem ThePhysics;

    private ParallaxMapperSystem Parallaxer;
    private GameLayer Playground;

    private ParticleSystem Particles;

    private PlayerObject PlatformPlayer;
    private bmg.katsuo.controllers.PlayerController PlayerController = null;

    private GameCameraSystem GameCam;
    //-------------------------------------------------------------------------------------------------------------------------

    public BackgroundStateManager(IApplication app)
    {
        App = app;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetPlayer(PlayerObject player)
    {
        PlatformPlayer = player;
        PlayerController = PlatformPlayer.GetController(bmg.katsuo.controllers.PlayerController.class);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(GameState state, float delta)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void AddLayerSystems(GameState state)
    {
        Parallaxer = new ParallaxMapperSystem(state.GetPlaygroundLayer());
        state.GetBackgroundLayer().AddSystem(Parallaxer);

        GameCam = new GameCameraSystem(3);
        state.GetPlaygroundLayer().AddSystem(GameCam);

        Particles = new ParticleSystem();
        state.GetPlaygroundLayer().AddSystem(Particles);

        ThePhysics = new Box2dPhysicsSystem(1f / Globals.PIXELS_TO_METERS, 0, -9.8f);
        state.GetPlaygroundLayer().AddSystem(ThePhysics);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(GameState state)
    {
        GameLayer bgLayer = state.GetBackgroundLayer();
        Array<GameObject> bgObjects = bgLayer.GetObjects();

        Map<String, GameObject> NameToLayerMap = new HashMap<String, GameObject>();
        for (GameObject bgObject : bgObjects)
        {
            if (bgObject instanceof ScrollingBackground)
            {
                if (!bgObject.getName().isEmpty())
                {
                    final MapProperties bgObjProps = bgObject.GetProperties();

                    float xScale = bgObjProps.get("x_scale", 0f, Float.class);
                    float yScale = bgObjProps.get("y_scale", 0f, Float.class);

                    Parallaxer.AddMapping(bgObject, xScale, yScale, true);

                    NameToLayerMap.put(bgObject.getName(), bgObject);
                }
            }
        }

        // move objects from background to corresponding layer
        for (GameObject bgObject : bgObjects)
        {
            if (!(bgObject instanceof ScrollingBackground))
            {
                final MapProperties bgObjProps = bgObject.GetProperties();

                final String layerName = bgObjProps.get("layer", "", String.class);
                if (!layerName.isEmpty())
                {
                    if (NameToLayerMap.containsKey(layerName))
                    {
                        bgObject.getParent().removeActor(bgObject);
                        NameToLayerMap.get(layerName).addActor(bgObject);
                    }
                }
            }
        }

        // layers
        Playground = state.GetPlaygroundLayer();
        //Playground.GetCamera().SetZoom(2.0f);

        Playground.GetCamera().SetCentered();
        //GameCam.AddFocus(Playground.GetGameObject("enemy_01"));

        state.GetForegroundLayer().setVisible(false);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void dispose(GameState state)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
