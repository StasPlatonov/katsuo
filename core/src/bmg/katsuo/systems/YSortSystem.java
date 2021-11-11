package bmg.katsuo.systems;

import java.util.Comparator;

import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class YSortSystem extends LayerSystem
{
    Array<String> IgnoreArray;

    public Comparator<Actor> YSort = new Comparator<Actor>()
    {
        @Override
        public int compare(Actor o1, Actor o2)
        {
            if (IgnoreArray.contains(o1.getName(), false) || IgnoreArray.contains(o2.getName(), false))
            {
                return 0;
            }
            return Float.compare(o2.getY(), o1.getY());
        }
    };
    //-------------------------------------------------------------------------------------------------------------------------

    public YSortSystem(String... ignore)
    {
        IgnoreArray = new Array<String>();
        if (ignore != null)
        {
            IgnoreArray.addAll(ignore);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectAdded(GameObject object, GameObject parent)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectChanged(GameObject object, int type, float value)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectRemoved(GameObject object, GameObject parent)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta, float timeScale)
    {
        GetLayer().getChildren().sort(YSort);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnRemove()
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
