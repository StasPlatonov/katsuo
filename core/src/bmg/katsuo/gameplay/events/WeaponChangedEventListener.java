package bmg.katsuo.gameplay.events;

import bmg.katsuo.IApplication;
import bmg.katsuo.utils.Event;

public class WeaponChangedEventListener implements bmg.katsuo.utils.EventListener<WeaponChangedEventArgs>
{
    private IApplication App;

    public WeaponChangedEventListener(IApplication app)
    {
        App = app;
    }

    @Override
    public void Process(Event<WeaponChangedEventArgs> event, WeaponChangedEventArgs args)
    {
    }
}

