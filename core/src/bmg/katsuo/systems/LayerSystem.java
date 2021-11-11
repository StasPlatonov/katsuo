package bmg.katsuo.systems;

import bmg.katsuo.objects.GameLayer;
import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.maps.MapProperties;
import bmg.katsuo.utils.Event;
import bmg.katsuo.utils.EventListener;

public abstract class LayerSystem extends AbstractSystem
{
    private EventListener<GameObject> ObjectAddedListener;
    private EventListener<GameObject> ObjectRemovedListener;
    private EventListener<GameObject> ObjectControllerChangedListener;
    //-------------------------------------------------------------------------------------------------------------------------

    public LayerSystem()
    {
        ObjectAddedListener = new GameObjectAddedListener();
        ObjectRemovedListener = new GameObjectRemovedListener();
        ObjectControllerChangedListener = new GameObjectControllerChangedListener();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void AddedToLayer(GameLayer layer)
    {
        layer.ObjectAddedEvent.Add(ObjectAddedListener);
        layer.ObjectRemovedEvent.Add(ObjectRemovedListener);
        layer.ObjectControllerChangedEvent.Add(ObjectControllerChangedListener);
        Init(layer.GetProperties());
    }

    @Override
    public void RemovedFromLayer(GameLayer layer)
    {
        layer.ObjectAddedEvent.Remove(ObjectAddedListener);
        layer.ObjectRemovedEvent.Remove(ObjectRemovedListener);
        layer.ObjectControllerChangedEvent.Remove(ObjectControllerChangedListener);
        OnRemove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * called when the gamelayer and tmx is fully loaded
     *
     * @param properties
     */
    public abstract void Init(MapProperties properties);
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * A new object has been added to the layer
     *
     * @param object
     */
    public abstract void OnObjectAdded(GameObject object, GameObject parent);

    /**
     * an object has been altered drastically
     * right now that means that its managers have been changed
     *
     * @param object
     */
    public abstract void OnObjectChanged(GameObject object, int type, float value);

    /**
     * a game object has been removed from this
     */
    public abstract void OnObjectRemoved(GameObject object, GameObject parent);
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * on layer Update
     *
     * @param delta
     */
    public abstract void Update(float delta, float timeScale);
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * when the layer is removed
     */
    public abstract void OnRemove();
    //-------------------------------------------------------------------------------------------------------------------------


    public class GameObjectAddedListener implements EventListener<GameObject>
    {
        @Override
        public void Process(Event<GameObject> event, GameObject object)
        {
            OnObjectAdded(object, null);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public class GameObjectRemovedListener implements EventListener<GameObject>
    {
        @Override
        public void Process(Event<GameObject> event, GameObject object)
        {
            OnObjectRemoved(object, null);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public class GameObjectControllerChangedListener implements EventListener<GameObject>
    {
        @Override
        public void Process(Event<GameObject> event, GameObject object)
        {
            OnObjectChanged(object, GameObject.GameObjectChangeType.CONTROLLER, 1);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
