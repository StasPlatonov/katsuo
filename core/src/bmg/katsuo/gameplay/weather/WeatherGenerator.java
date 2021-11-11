package bmg.katsuo.gameplay.weather;

import bmg.katsuo.IApplication;
import bmg.katsuo.objects.GameLayer;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import bmg.katsuo.systems.GameCameraSystem;
import bmg.katsuo.ui.RenderScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public abstract class WeatherGenerator
{
    protected TextureAtlas Atlas;
    protected GameLayer Layer;
    protected Box2dPhysicsSystem ThePhysics;
    private GameLayer.LayerCamera LayerCamera;
    protected List<String> RegionNames = new ArrayList<String>();
    protected List<TextureAtlas.AtlasRegion> Regions = new ArrayList<TextureAtlas.AtlasRegion>();

    protected enum GeneratorState
    {
        STATE_INACTIVE,
        STATE_STARTING,
        STATE_ACTIVE,
        STATE_STOPPING
    }
    protected GeneratorState State = GeneratorState.STATE_INACTIVE;
    protected float StateTime;
    protected float StateMaxTime;
    protected float Intencity = 0f; // particles per second
    protected float Accumulator = 0f;
    protected float LastGeneratedTime = 0f;

    //-------------------------------------------------------------------------------------------------------------------------

    public WeatherGenerator(TextureAtlas atlas, GameLayer layer, List<String> regions)
    {
        Atlas = atlas;
        Layer = layer;
        Intencity = 0f;
        ThePhysics = Layer.GetSystem(Box2dPhysicsSystem.class);
        GameCameraSystem gameCam = Layer.GetSystem(GameCameraSystem.class);
        LayerCamera = gameCam.GetCamera();

        RegionNames = regions;
        for (String regionName : RegionNames)
        {
            Regions.add(Atlas.findRegion(regionName));
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        Stop(0f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Start(float startingTime)
    {
        StateTime = 0f;
        StateMaxTime = startingTime;
        State = GeneratorState.STATE_STARTING;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Stop(float stoppingTime)
    {
        StateTime = 0f;
        StateMaxTime = stoppingTime;

        if (StateMaxTime == 0f)
        {
            State = GeneratorState.STATE_INACTIVE;
        }
        else
        {
            State = GeneratorState.STATE_STOPPING;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Rectangle ViewportRect = new Rectangle();

    private void GenerateParticles(Vector2 wind, int count)
    {
        if (RegionNames.isEmpty())
        {
            return;
        }

        ViewportRect.set(LayerCamera.GetViewBounds());

        RenderScreen renderScreen = (RenderScreen)Layer.GetApp().GetScreen(IApplication.ScreenType.SCREEN_GAME);
        float zoom = renderScreen.GetZoom();
        ViewportRect.height *= 1.1f;

        /*ViewportRect.x *= zoom;
        ViewportRect.y *= zoom;
        ViewportRect.width *= zoom;
        ViewportRect.height *= zoom;*/

        Generate(wind, count, ViewportRect);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void AddParticle(Actor particle)
    {
        Layer.addActor(particle);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected abstract void Generate(Vector2 wind, int count, Rectangle rect);
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetParameter(String name, String value)
    {
        if (name.equals("intensity"))
        {
            Intencity = Float.parseFloat(value);
        }
    }

    public String GetParameter(String name)
    {
        if (name.equals("intensity"))
        {
            return String.valueOf(Intencity);
        }
        return "";
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void UpdateGeneration(Vector2 wind)
    {
        float genDelta = StateTime - LastGeneratedTime;

        Accumulator += genDelta;

        float numParticles = Accumulator * Intencity;

        int partCount = Math.round((float)Math.ceil(numParticles));

        float timePerParticle = 1f / Intencity;

        if (partCount > 0)
        {
            GenerateParticles(wind, partCount);
            Accumulator -= partCount * timePerParticle;

            LastGeneratedTime = StateTime;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected Vector2 Wind = new Vector2();

    public void Update(Vector2 wind, float delta)
    {
        Wind.set(wind);

        boolean needSwitch = false;

        StateTime += delta;

        if (StateMaxTime > 0)
        {
            if (StateTime > StateMaxTime)
            {
                needSwitch = true;
                StateTime = 0;
                StateMaxTime = 0;
            }
        }

        switch (State)
        {
            case STATE_INACTIVE:
                return;

            case STATE_STARTING:
                if (needSwitch)
                {
                    State = GeneratorState.STATE_ACTIVE;
                }

                UpdateGeneration(wind);
                break;

            case STATE_ACTIVE:
                UpdateGeneration(wind);
                break;

            case STATE_STOPPING:
                if (needSwitch)
                {
                    State = GeneratorState.STATE_INACTIVE;
                }
                UpdateGeneration(wind);
                break;
        }

        UpdateWind(wind, delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void ApplyWind(Body body, float bodySize, Vector2 wind, float delta)
    {
        float density = body.getFixtureList().get(0).getDensity();
        float k = 1e-4f * density * bodySize * bodySize;
        float velX = body.getLinearVelocity().x;
        float velY = body.getLinearVelocity().y;
        // Apply drag force
        float velValue = body.getLinearVelocity().len();
        Vector2 force = new Vector2(-k * velValue * velX, -k * velValue * velY);
        body.applyForce(force, body.getWorldCenter(), true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void UpdateWind(Vector2 wind, float delta)
    {

    }
    //-------------------------------------------------------------------------------------------------------------------------
}
