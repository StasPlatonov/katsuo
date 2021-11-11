package bmg.katsuo.utils;

import com.badlogic.gdx.utils.SnapshotArray;

public class Event<T>
{
    SnapshotArray<EventListener<T>> Listeners;
    //-------------------------------------------------------------------------------------------------------------------------

    public Event()
    {
        Listeners = new SnapshotArray<EventListener<T>>();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Add(EventListener<T> listener)
    {
        if (Listeners.contains(listener, true))
        {
            return;
        }
        Listeners.add(listener);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Remove(EventListener<T> listener)
    {
        Listeners.removeValue(listener, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RemoveAllListeners()
    {
        Listeners.clear();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void Fire(T object)
    {
        final Object[] list = Listeners.begin();
        for (int i = 0; i < Listeners.size; i++)
        {
            EventListener<T> listener = (EventListener<T>) list[i];
            listener.Process(this, object);
        }
        Listeners.end();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
