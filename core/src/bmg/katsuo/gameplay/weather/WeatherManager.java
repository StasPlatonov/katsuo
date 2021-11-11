package bmg.katsuo.gameplay.weather;

import bmg.katsuo.objects.GameLayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class WeatherManager
{
    public final static String WEATHER_RAIN = "rain";
    public final static String WEATHER_SNOW = "snow";
    public final static String WEATHER_DUST = "dust";

    private TextureAtlas Atlas;
    private GameLayer Layer;
    private TiledMap Map;

    private Vector2 Wind = new Vector2();
    private Vector2 SourceWind = new Vector2();
    private Vector2 TargetWind = new Vector2();
    private float WindChangeTime = 0f;
    boolean NeedChangeWind = false;

    private ArrayMap<String, WeatherGenerator> Generators = new ArrayMap<String, WeatherGenerator>();
    //-------------------------------------------------------------------------------------------------------------------------

    public WeatherManager(TextureAtlas atlas, GameLayer layer)
    {
        Atlas = atlas;
        Layer = layer;
        Map = layer.GetState().GetMapData();
        //Wind.set(-5f, 0f);
        SetWind(-5f, 0f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        for (ObjectMap.Entry<String, WeatherGenerator> entry : Generators)
        {
            entry.value.dispose();
        }

        Generators.clear();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Vector2 GetWind()
    {
        return Wind;
    }

    public void SetWind(float wx, float wy)
    {
        SourceWind.set(Wind);
        TargetWind.set(wx, wy);
        NeedChangeWind = true;
        WindChangeTime = 0f;
        //Wind.set(wx, wy);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public WeatherGenerator GetGenerator(String name)
    {
        if (!Generators.containsKey(name))
        {
            return null;
        }

        return Generators.get(name);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RemoveGenerator(String name)
    {
        if (Generators.containsKey(name))
        {
            Generators.removeKey(name);
        }
    }

    public WeatherGenerator AddRainGenerator(String name, List<String> regions, boolean phys, float startingTime)
    {
        WeatherGenerator rain = phys ? new RainPhysGenerator(Atlas, Layer, regions) : new RainGenerator(Atlas, Layer, regions);
        AddGenerator(name, rain);

        rain.Start(startingTime);

        return rain;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public SnowGenerator AddSnowGenerator(String name, List<String> regions, float startingTime)
    {
        SnowGenerator snow = new SnowGenerator(Atlas, Layer, regions);
        AddGenerator(name, snow);

        snow.Start(startingTime);

        return snow;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public WeatherGenerator AddDustGenerator(String name, List<String> regions, DustGenerator.DustType type, float startingTime)
    {
        WeatherGenerator dust = new DustGenerator(Atlas, Layer, regions, type);
        AddGenerator(name, dust);

        dust.Start(startingTime);

        return dust;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void AddGenerator(String name, WeatherGenerator generator)
    {
        Generators.put(name, generator);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Update(float delta)
    {
        if (NeedChangeWind)
        {
            WindChangeTime += delta;
            float k = Math.min(1f, WindChangeTime / 5f);
            if (k >= 1f)
            {
                NeedChangeWind = false;
                Wind.set(TargetWind);
            }
            else
            {
                Wind.x = MathUtils.lerp(SourceWind.x, TargetWind.x, k);
                Wind.y = MathUtils.lerp(SourceWind.y, TargetWind.y, k);
            }
        }
        for (ObjectMap.Entry<String, WeatherGenerator> entry : Generators)
        {
            entry.value.Update(Wind, delta);
        }
        //Gdx.app.log("", "children: " + Layer.getChildren().size);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
