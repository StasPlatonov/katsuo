package bmg.katsuo.managers;

import bmg.katsuo.IApplication;
import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class MaterialManager
{
    private IApplication App;

    public MaterialManager(IApplication app)
    {
        App = app;
    }

    //-------------------------------------------------------------------------------------------------------------------------

    private final static Map<String, Material> Materials = new HashMap<String, Material>();
    private static void AddMaterial(Material material)
    {
        Materials.put(material.GetName(), material);
    }
    static {
        AddMaterial(new Material());
        AddMaterial(new Material("brick", Color.LIGHT_GRAY, 1f));
        AddMaterial(new Material("wood", Color.ORANGE, 0.7f));
        AddMaterial(new Material("grass", Color.LIME, 0.8f));
        AddMaterial(new Material("water", Color.SKY, 0.4f));
        AddMaterial(new Material("metal", Color.NAVY, 1f));
        AddMaterial(new Material("platform", Color.LIME, 5f));
    }

    public static Material GetMaterial(String material)
    {
        return Materials.containsKey(material) ? Materials.get(material) : Materials.get(Material.DEFAULT_MATERIAL_NAME);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
