package bmg.katsuo.gameplay;

import bmg.katsuo.gameplay.events.*;
import bmg.katsuo.utils.Event;
import bmg.katsuo.utils.EventListener;

import java.util.HashMap;
import java.util.Map;

public class GameEvents
{
    public enum EventId
    {
        EVENT_LEVEL_START,
        EVENT_TAKE_ITEM,
        EVENT_DAMAGE_ENEMY,
        EVENT_DAMAGED_BY_ENEMY,
        EVENT_CHECKPOINT,
        EVENT_EOL,
        EVENT_EOG,
        EVENT_HEALTH_CHANGED,
        EVENT_SCORE_CHANGED,
        EVENT_WEAPON_CHANGED,
        EVENT_PLAYER_KILLED
    };

    private Map<EventId, Event> Events = new HashMap<EventId, Event>();
    //-------------------------------------------------------------------------------------------------------------------------

    public GameEvents()
    {
        Events.put(EventId.EVENT_LEVEL_START, new Event<LevelStartEventArgs>());
        Events.put(EventId.EVENT_TAKE_ITEM, new Event<TakeItemEventArgs>());
        Events.put(EventId.EVENT_DAMAGE_ENEMY, new Event<DamageEnemyEventArgs>());
        Events.put(EventId.EVENT_DAMAGED_BY_ENEMY, new Event<DamagedByEnemyEventArgs>());
        Events.put(EventId.EVENT_CHECKPOINT, new Event<CheckpointEventArgs>());
        Events.put(EventId.EVENT_EOL, new Event<EOLEventArgs>());
        Events.put(EventId.EVENT_EOG, new Event<EOGEventArgs>());
        Events.put(EventId.EVENT_SCORE_CHANGED, new Event<ScoreChangedEventArgs>());
        Events.put(EventId.EVENT_WEAPON_CHANGED, new Event<WeaponChangedEventArgs>());
        Events.put(EventId.EVENT_HEALTH_CHANGED, new Event<HealthChangedEventArgs>());
        Events.put(EventId.EVENT_PLAYER_KILLED, new Event<PlayerKilledEventArgs>());
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void AddEventListener(EventId eventId, EventListener listener)
    {
        if (!Events.containsKey(eventId))
        {
            return;
        }

        Event event = Events.get(eventId);

        event.Add(listener);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void Event(EventId eventId, EventArgs args)
    {
        if (!Events.containsKey(eventId))
        {
            return;
        }

        Event event = Events.get(eventId);

        event.Fire(args);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
