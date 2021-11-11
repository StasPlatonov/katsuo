package bmg.katsuo.gameplay.objects;

import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.objects.BaseGameObject;
import bmg.katsuo.physics.Collider;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapProperties;

import static bmg.katsuo.objects.Types.*;

public class ItemObject extends BaseGameObject
{
    private final static int IST_UNKNOWN = -1;
    private final static int IST_IDLE = 0;

    private int State = IST_UNKNOWN;

    protected int Value = 0;

    public enum ItemType
    {
        ITEM_UNKNOWN,
        ITEM_HEALTH,
        ITEM_POWER,
        ITEM_COIN,
        ITEM_BOX,
        ITEM_WEAPON
    };

    private ItemType Type;

    private String UsageSound;
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        properties.put("phys_category", (int)ITEM_BIT | GROUND_BIT);
        properties.put("phys_mask", PLAYER_BIT | GROUND_BIT);
        properties.put("phys_object_type", "trigger");

        super.Init(properties);

        final String itemType = properties.get("item_type", "", String.class);

        Value = properties.get("item_value", 0, Integer.class);
        UsageSound = properties.get("sound", itemType, String.class);

        EnableAnimations(true);

        if (itemType.equals("health"))
        {
            SetType(ItemType.ITEM_HEALTH);
            CreateAnimation(IST_IDLE, "health_idle", "crystal-32-green", 8, .08f, Animation.PlayMode.LOOP);
            AnimController.Randomize("health_idle");
        }
        else if (itemType.equals("power"))
        {
            SetType(ItemType.ITEM_POWER);
            CreateAnimation(IST_IDLE, "power_idle", "coins_animation", 8, 3, 9, 8, .08f, Animation.PlayMode.LOOP);
            AnimController.Randomize("power_idle");
        }
        else if (itemType.equals("coin"))
        {
            SetType(ItemType.ITEM_COIN);
            CreateAnimation(IST_IDLE, "coin_idle", "coins_animation", 8, 3, 1, 8, .08f, Animation.PlayMode.LOOP);
            AnimController.Randomize("coin_idle");
        }
        /*else
        {
            GetApp().Error("", "Unknown item type " + itemType);
            SetType(ITEM_UNKNOWN);
        }*/

        SetState(IST_IDLE);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void SetType(ItemType type)
    {
        Type = type;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected boolean SetState(Integer state)
    {
        if (state == State)
        {
            return false;
        }

        State = state;
        SetStateAnimation(State);

        return true;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean Taken = false;

    public boolean IsTaken()
    {
        return Taken;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Take()
    {
        GetApp().PlaySound(UsageSound, false);

        new AnimationEffect(GetLayer(), this, GetTrueX() - 8, GetTrueY() - 8, 48f, 48f, "collected", 0.05f);

        Taken = true;

        Value = 0;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        if (Taken)
        {
            // Do not collide after taken
            Box2dController physContr = GetController(Box2dController.class);
            physContr.SetFreezeBits(NOTHING_BIT, NOTHING_BIT);

            GetLayer().RemoveGameObject(this);
            //remove();
        }

        super.Update(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
        super.OnCollide(collider, type);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public ItemType GetType()
    {
        return Type;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public int GetValue()
    {
        return Value;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String GetDebugString()
    {
        return getName() + ": " + GetValue();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
