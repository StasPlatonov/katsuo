package bmg.katsuo.gameplay.events;

import bmg.katsuo.IApplication;
import bmg.katsuo.gameplay.objects.PlayerInventory;
import bmg.katsuo.gameplay.objects.PlayerState;
import bmg.katsuo.gameplay.objects.ItemObject;
import bmg.katsuo.gameplay.objects.WeaponItemObject;
import bmg.katsuo.gameplay.weapons.WeaponMeasure;
import bmg.katsuo.utils.Event;

public class TakeItemEventListener implements bmg.katsuo.utils.EventListener<TakeItemEventArgs>
{
    private IApplication App;

    public TakeItemEventListener(IApplication app)
    {
        App = app;
    }

    @Override
    public void Process(Event<TakeItemEventArgs> event, TakeItemEventArgs args)
    {
        ItemObject item = args.Item;
        if (item.IsTaken() || item.GetValue() == 0)
        {
            return;
        }

        PlayerState state = args.Player.GetPState();

        switch (args.Item.GetType())
        {
            case ITEM_HEALTH:
            {
                if (state.GetHealth() == 100)
                {
                    return; // without Take();
                }
                state.SetHealth(state.GetHealth() + item.GetValue(), false);
                break;
            }
            case ITEM_POWER:
            {
                if (state.GetPower() == 100)
                {
                    return; // without Take();
                }
                state.SetPower(state.GetPower() + item.GetValue(), false);
                break;
            }
            case ITEM_COIN:
            {
                state.SetScore(state.GetScore() + item.GetValue());
                break;
            }
            case ITEM_BOX:
            {
                state.SetScore(state.GetScore() + item.GetValue());
                break;
            }
            case ITEM_WEAPON:
            {
                WeaponItemObject weaponItem = (WeaponItemObject) item;
                PlayerInventory inventory = args.Player.GetInventory();

                if (inventory.IsFull(weaponItem.GetWeaponType()))
                {
                    return; // without Take();
                }
                inventory.AddWeapon(weaponItem.GetWeaponType(), WeaponMeasure.Count(item.GetValue()));
                break;
            }
        }

        item.Take();

        state.SetCollected(state.GetCollected() + 1);
    }
};
//-------------------------------------------------------------------------------------------------------------------------