package bmg.katsuo.gameplay.objects;

import bmg.katsuo.controllers.LightController;
import bmg.katsuo.objects.BaseGameObject;
import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import static bmg.katsuo.controllers.LightController.LightType.*;

// Static/dynamic light source
public class LightObject extends BaseGameObject
{
    private LightController LightContr;
    //private Box2dPhysicsSystem ThePhysics;
    private LightController.LightType Type = NoLight;
    private Body AttachedChain = null;

    //-------------------------------------------------------------------------------------------------------------------------

    public LightObject()
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        super.Init(properties);

        //ThePhysics = GetLayer().GetSystem(Box2dPhysicsSystem.class);

        String typeStr = properties.get("light_type", "point", String.class);
        if (typeStr.equals("point"))
            Type = PointLight;
        else if (typeStr.equals("cone"))
            Type = ConeLight;
        else
        {
            Type = NoLight;
            GetApp().Error(TAG, "Unknown light type: " + typeStr);
        }

        int strength = properties.get("strength", 100, Integer.class);
        if (strength == 0)
        {
            strength = Math.max(Math.round(getWidth() * .5f), Math.round(getHeight() * .5f));
        }
        final Color color = properties.get("color", Color.class);

        float direction = properties.get("direction", -90f, Float.class);
        float rotSpeed = properties.get("rot_speed", 0f, Float.class);
        float pulseSpeed = properties.get("pulse_speed", 0f, Float.class);
        float pulseMin = properties.get("pulse_min", 0f, Float.class);
        float pulseAmp = properties.get("pulse_amp", 0f, Float.class);

        float degrees = properties.get("degrees", 45f, Float.class);

        float softness = properties.get("softness", 0.5f, Float.class);

        boolean enabled = properties.get("enabled", true, Boolean.class);

        if ((Type != NoLight) && (strength > 0))
        {
            LightContr = new LightController(Type, color, 32, strength, degrees, direction, softness);

            LightContr.SetPulse(pulseSpeed, pulseMin, pulseAmp);
            LightContr.SetRotationSpeed(rotSpeed);

            AddController(LightContr);

            LightContr.SetEnabled(enabled);

            final String attached = properties.get("attached", "", String.class);
            GameObject attObj = GetLayer().GetGameObject(attached);
            if (attObj instanceof RopeObject)
            {
                RopeObject attCh = (RopeObject)attObj;
                AttachedChain = attCh.GetLastBody();

                LightContr.AttachTo(AttachedChain);
            }
        }
        SetSpriteId(NO_SPRITE);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public LightController GetLightController()
    {
        return LightContr;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove()
    {
        return super.remove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        if (!IsUpdatable() || !isVisible())
        {
            return;
        }

        if (AttachedChain != null)
        {
            final Vector2 pos = LightContr.GetPosition();
            setPosition(pos.x - getWidth() * .5f, pos.y - getHeight() * .5f);

            //setRotation(AttachedChain.getAngle() * MathUtils.radiansToDegrees/* - 90*/);
        }

        super.Update(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnAction(GameObject sender, String action)
    {
        if (!action.isEmpty() && LightContr != null)
        {
            LightContr.SetEnabled(!LightContr.IsEnabled());
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
