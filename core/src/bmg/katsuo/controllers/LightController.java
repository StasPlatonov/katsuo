package bmg.katsuo.controllers;

import bmg.katsuo.Globals;
import bmg.katsuo.IApplication;
import bmg.katsuo.Settings;
import bmg.katsuo.objects.GameObjectController;
import bmg.katsuo.objects.GameObject;
import box2dLight.DirectionalLight;
import com.badlogic.gdx.graphics.Color;
import bmg.katsuo.systems.Box2dPhysicsSystem;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;

import static bmg.katsuo.objects.Types.*;

public class LightController extends GameObjectController
{
    public enum LightType
    {
        NoLight,
        PointLight,
        ConeLight,
        Directional,
    }

    private IApplication App;
    private GameObject TheObject;
    private Color LightColor;
    private int Rays;
    private float Distance;
    private LightType Type;
    private Vector2 Position = new Vector2();
    private float ConeDegrees;
    private float Direction = 0;
    private float RotSpeed = 0f;

    private float PulseSpeed = 0f;
    private float PulseMin = 0f;
    private float PulseAmp = 0f;
    private float PulsePhase = 0f;

    private float Softness = 0.5f;

    private Box2dPhysicsSystem World;
    private Light TheLight;

    //-------------------------------------------------------------------------------------------------------------------------

    public LightController(LightType type, Color color, int rays, float distance, float cone_degrees, float direction, float softness)
    {
        this.LightColor = color;
        this.Type = type;
        this.Rays = rays;
        this.Distance = distance;
        this.ConeDegrees = cone_degrees;
        Direction = direction;
        RotSpeed = 0f;
        PulseSpeed = 0f;
        PulsePhase = 0f;
        Softness = softness;
    }
    //-------------------------------------------------------------------------------------------------------------------------
/*
    public LightController(LightType type, Color color, int rays, float distance, float cone_degrees, float direction)
    {
        this(type, color, rays, distance, cone_degrees, direction, 0.5f);
    }

    public LightController(LightType type, Color color, int rays, float distance)
    {
        this(type, color, rays, distance, 90 / 2f, 0f, 0.5f);
    }*/
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(GameObject object)
    {
        App = object.GetApp();
        App.Log("", "LightController.Init()");
        TheObject = object;

        GetWorld(object);
        CreateLight(object);
        /*Box2dController boxController = object.GetController(Box2dController.class);
        if (boxController != null)
        {
            TheLight.attachToBody(boxController.GetBody());
        }*/
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        App.Log("", "LightController.dispose()");

        DestroyLight();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetRotationSpeed(float rs)
    {
        RotSpeed = rs;
    }

    public void SetPulse(float speed, float pulseMin, float pulseAmplitude)
    {
        PulseSpeed = speed;
        PulseMin = pulseMin;
        PulseAmp = pulseAmplitude;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetDirection(float direction)
    {
        Direction = direction;

        if (Attached)
        {
            return;
        }
        if (TheLight != null)
        {
            TheLight.setDirection(Direction + TheObject.GetTrueRotation());
        }
    }

    public float GetDirection()
    {
        return Direction;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Vector2 GetPosition()
    {
        return Position;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * checks if point is inside the light
     *
     * @param x
     * @param y
     * @return
     */
    public boolean Contains(float x, float y)
    {
        if (TheLight != null)
        {
            return TheLight.contains(x * Globals.PIXELS_TO_METERS, y * Globals.PIXELS_TO_METERS);
        }
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void CreateLight(GameObject object)
    {
        if (World == null || TheLight != null || (Type == LightType.NoLight))
        {
            return;
        }

        Position.set(object.GetTrueX() + object.getWidth() * .5f, object.GetTrueY() + object.getHeight() * .5f);

        if (object.GetApp().GetSettings().GetLightingType() != Settings.LightingTypes.LIGHTING_BOX2D)
        {
            return;
        }

        float x = Position.x * Globals.PIXELS_TO_METERS;
        float y = Position.y * Globals.PIXELS_TO_METERS;

        switch (Type)
        {
            case PointLight:
            {
                PointLight light = new PointLight(World.GetLights(), Rays, LightColor, Distance * Globals.PIXELS_TO_METERS, x, y);
                TheLight = light;
                break;
            }
            case ConeLight:
            {
                ConeLight light = new ConeLight(World.GetLights(), Rays, LightColor, Distance * Globals.PIXELS_TO_METERS, x, y, Direction + object.GetTrueRotation(), ConeDegrees * .5f);
                TheLight = light;
                break;
            }

            case Directional:
            {
                DirectionalLight light = new DirectionalLight(World.GetLights(), Rays, LightColor, Direction + TheObject.GetTrueRotation());
                TheLight = light;
                break;
            }
        }

        TheLight.setSoftnessLength(TheLight.getDistance() * Softness);

        TheLight.setContactFilter((short)(LIGHTS_BIT/*CATEGORY_DEFAULT*/), (short)0, (short)(GROUND_BIT | SHADOW_CASTER_BIT/* | ITEM_BIT | BREAKABLE_BIT*/));

        //TheLight.setStaticLight(true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetSoftnessLengthK(float k)
    {
        if (TheLight != null)
            TheLight.setSoftnessLength(TheLight.getDistance() * k);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetXRay(boolean enabled)
    {
        if (TheLight != null)
            TheLight.setXray(enabled);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean Attached = false;
    private Body AttachedToBody;

    public void AttachTo(Body body)
    {
        if (TheLight != null)
        {
            TheLight.attachToBody(body);
            if (body != null)
            {
                TheLight.setIgnoreAttachedBody(true);
                //TheLight.setStaticLight(false);
            }
            else
            {
                //TheLight.setStaticLight(true);
            }
        }

        AttachedToBody = body;
        Attached = (body != null);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(GameObject object, float delta)
    {
        if (!IsEnabled() || !object.IsUpdatable() || (Type == LightType.NoLight))
        {
            return;
        }

        if ((World != null))
        {
            float x = (object.GetTrueX() + (object.getWidth() * .5f));
            float y = (object.GetTrueY() + (object.getHeight() * .5f));

            if (Attached)
            {
                float rot = AttachedToBody.getAngle() * MathUtils.radiansToDegrees;

                if (TheLight != null)
                {
                    Position = TheLight.getPosition();
                    Position.scl(Globals.METERS_TO_PIXELS);
                }
                else
                {
                    Vector2 pos = AttachedToBody.getPosition();
                    Position.set(pos).scl(Globals.METERS_TO_PIXELS);
                }

                Direction = rot - TheObject.GetTrueRotation();
                //TheObject.setRotation(rot);
            }
            else
            {
                Position.set(x, y);

                if (TheLight != null)
                {
                    TheLight.setPosition(Position.x * Globals.PIXELS_TO_METERS, Position.y * Globals.PIXELS_TO_METERS);
                }
            }

            if (PulseSpeed != 0f)
            {
                PulsePhase = (PulsePhase + delta * PulseSpeed) % 360f;

                float opacity = PulseMin + PulseAmp * (float) Math.sin(PulsePhase * MathUtils.degreesToRadians);
                SetOpacity(opacity);
            }

            if ((Type != LightType.PointLight) && (RotSpeed != 0f))
            {
                if (!Attached)
                {
                    float direction = (Direction + delta * RotSpeed) % 360f;
                    SetDirection(direction);
                }
            }

            if (Type == LightType.ConeLight)
            {
                if (!Attached)
                {
                    //SetDirection(Direction); // Update from object's rotation
                }
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Remove(GameObject object)
    {
        DestroyLight();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetConeDegree(float degree)
    {
        ConeDegrees = degree;

        if ((Type == LightType.ConeLight) && (TheLight != null))
        {
            ConeLight coneLight = (ConeLight) TheLight;
            coneLight.setConeDegree(ConeDegrees);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public LightType GetLightType()
    {
        return Type;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Color GetColor()
    {
        return LightColor;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void DestroyLight()
    {
        if (TheLight != null)
        {
            AttachTo(null);

            World.DestroyLight(TheLight);
        }
        TheLight = null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SetDistance(float distance)
    {
        Distance = distance;

        if (TheLight != null)
        {
            TheLight.setDistance(Distance * Globals.PIXELS_TO_METERS);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetDistance()
    {
        return Distance;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetConeDegrees()
    {
        return ConeDegrees;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetOpacity(float opacity)
    {
        LightColor.a = opacity;

        if (TheLight != null)
        {
            //Color color = TheLight.getColor();
            //color.a = opacity;
            TheLight.setColor(LightColor);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void SetEnabled(boolean enabled)
    {
        super.SetEnabled(enabled);

        if (TheLight != null)
        {
            TheLight.setActive(enabled);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void GetWorld(GameObject object)
    {
        if (World == null)
        {
            World = object.GetLayer().GetSystem(Box2dPhysicsSystem.class);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
