package bmg.katsuo.objects;

import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.managers.SoundsCollection;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;

public class BaseGameObject extends GameObject
{
    public Rectangle getInteractionRect()
    {
        return InteractionRect;
    }

    public void setInteractionRect(Rectangle interactionRect)
    {
        InteractionRect = interactionRect;
    }

    protected SoundsCollection Sounds;

    protected Rectangle InteractionRect;

	@Override
    public void Init(MapProperties props)
    {
        super.Init(props);
        Sounds = GetApp().GetSoundsCollection();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private static boolean IsPhysicsRequired(MapProperties props)
    {
        Iterator<String> keys = props.getKeys();
        while (keys.hasNext())
        {
            final String prop = keys.next();
            if (prop.startsWith("phys_"))
            {
                return true;
            }
        }
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void CreateControllers()
    {
        Box2dPhysicsSystem physics = GetLayer().GetSystem(Box2dPhysicsSystem.class);

        if (IsPhysicsRequired(GetProperties()))
        {
            // Add physics only if object's layer has physics system (objects in background layer should not have physics)
            if (physics != null)
            {
                AddController(new Box2dController(physics));
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
