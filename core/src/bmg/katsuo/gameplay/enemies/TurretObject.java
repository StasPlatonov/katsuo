package bmg.katsuo.gameplay.enemies;

import bmg.katsuo.Globals;
import bmg.katsuo.gameplay.objects.PlayerObject;
import bmg.katsuo.gameplay.objects.SimpleEffect;
import bmg.katsuo.gameplay.weapons.Bullet;
import bmg.katsuo.objects.BaseGameObject;
import bmg.katsuo.physics.RayCaster;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import bmg.katsuo.ui.AtlasSpriteEx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static bmg.katsuo.objects.Types.*;

public class TurretObject extends BaseGameObject
{
    private Box2dPhysicsSystem ThePhysics;
    private float ShootInterval;
    private float ShootIntervalRnd;

    private float IdleSoundInterval = 3f;

    private float Distance;
    private float FOV;
    private float BulletSpeed = 500f;

    private Vector2 PlayerPos = new Vector2();
    private Vector2 ViewDir = new Vector2();
    private Vector2 ToPlayer = new Vector2();
    private Vector2 FOVDebugUp = new Vector2();
    private Vector2 FOVDebugDown = new Vector2();

    boolean PlayerVisible = false;
    boolean PlayerInFOV;

    private Vector2 ShootPoint = new Vector2();
    private Vector2 DetectorPoint = new Vector2();
    Vector2 DetectorPointMeters = new Vector2();
    Vector2 PlayerPosMeters = new Vector2();

    private AtlasSpriteEx NormalSprite;
    private AtlasSpriteEx ArmedSprite;
    private AtlasSpriteEx ShootSprite;

    boolean flipX, flipY;

    public StateMachine<TurretObject, TurretState> FSM;
    private float StateTimeMs;
    private boolean NeedStabilize = false;
    private float StabilizeTimeMs;

    private RayCaster Checker;
    private Vector2 HitPoint = new Vector2();
    //-------------------------------------------------------------------------------------------------------------------------

    void UpdateFromMapObject(MapObject mapObject)
    {
        if (!(mapObject instanceof TiledMapTileMapObject))
        {
            return;
        }

        TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) mapObject;

        TiledMapTile tile = tiledObject.getTile();

        MapObjects tileObjects = tile.getObjects();

        flipX = tiledObject.isFlipHorizontally();
        flipY = tiledObject.isFlipVertically();

        SetFlip(flipX, flipY);

        float sx = tiledObject.getScaleX();
        float sy = tiledObject.getScaleY();
        float width = mapObject.getProperties().get("width", Float.class);
        float height = mapObject.getProperties().get("height", Float.class);

        for (MapObject tileObj : tileObjects)
        {
            if (tileObj.getName() == null)
            {
                continue;
            }
            if (tileObj.getName().equals("shoot_point"))
            {
                final RectangleMapObject rect = (RectangleMapObject)tileObj;
                final Rectangle r = rect.getRectangle();

                ShootPoint.set((flipX ? (width - r.x) : r.x) * sx, (flipY ? (height - r.y) : r.y) * sy);
                ShootPoint.rotate(getRotation());
                ShootPoint.add(GetTrueX(), GetTrueY());
            }
            else
            if (tileObj.getName().equals("detector_point"))
            {
                final RectangleMapObject rect = (RectangleMapObject)tileObj;
                final Rectangle r = rect.getRectangle();

                DetectorPoint.set((flipX ? (width - r.x) : r.x) * sx, (flipY ? (height - r.y) : r.y) * sy);
                DetectorPoint.rotate(getRotation());
                DetectorPoint.add(GetTrueX(), GetTrueY());

                DetectorPointMeters.set(DetectorPoint).scl(Globals.PIXELS_TO_METERS);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        super.Init(properties);

        ThePhysics = GetLayer().GetSystem(Box2dPhysicsSystem.class);

        ShootIntervalRnd = ShootInterval = properties.get("shoot_interval", 1f, Float.class);

        Distance = properties.get("distance", 100f, Float.class);
        FOV = properties.get("fov", 10f, Float.class);

        String type = properties.get("type", "bullet", String.class);

        Sounds.Add(getClass().getSimpleName(), TURRET_IDLE_SOUND, properties.get("idle_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), TURRET_ARMED_SOUND, properties.get("armed_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), TURRET_SHOOT_SOUND, properties.get("shoot_sound", "", String.class));

        ShootPoint.set(GetTrueX() + getWidth() * .5f, GetTrueY() + getHeight() *.5f);
        DetectorPoint.set(ShootPoint);

        setOrigin(0f, 0f);

        final String sprite = GetSpriteId();

        NormalSprite = (AtlasSpriteEx)GetState().GetSprite(sprite);
        NormalSprite.setOrigin(getOriginX(), getOriginY());
        ArmedSprite = (AtlasSpriteEx)GetState().GetSprite(sprite + "-armed");
        ArmedSprite.setOrigin(getOriginX(), getOriginY());
        ShootSprite = (AtlasSpriteEx)GetState().GetSprite(sprite + "-shoot");
        ShootSprite.setOrigin(getOriginX(), getOriginY());

        UpdateFromMapObject(GetMapObject());

        FSM = new DefaultStateMachine<TurretObject, TurretState>(this, TurretState.SLEEP);
        StateTimeMs = MathUtils.random(0f, IdleSoundInterval);

        Checker = new RayCaster(ThePhysics);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean IsPlayerDetected()
    {
        return PlayerVisible;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void OnStartSleep()
    {
        StateTimeMs = 0f;
        //GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), IdleSound), getX(), getY(), false);
        SetRawSprite(NormalSprite);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void DoSleep()
    {
        if (StateTimeMs > IdleSoundInterval)
        {
            FSM.changeState(TurretState.SLEEP_ARMED);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void OnStartSleepArmed()
    {
        StateTimeMs = 0f;
        //GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), IdleSound), getX(), getY(), false);
        SetRawSprite(ArmedSprite);
    }

    public void DoSleepArmed()
    {
        if (StateTimeMs > IdleSoundInterval)
        {
            FSM.changeState(TurretState.SLEEP);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void OnStartShoot()
    {
        StateTimeMs = 0;
        SetRawSprite(ArmedSprite);
        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), TURRET_ARMED_SOUND), getX(), getY(), false);
    }

    public void OnStopShoot()
    {
        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), TURRET_IDLE_SOUND), getX(), getY(), false);
    }

    public void DoShoot()
    {
        if (PlayerVisible & (ShootInterval != 0f))
        {
            if (NeedStabilize)
            {
                if (StateTimeMs > StabilizeTimeMs)
                {
                    SetRawSprite(ArmedSprite);
                    NeedStabilize = false;
                }
            }

            if (StateTimeMs >= ShootIntervalRnd)
            {
                Shoot(1);

                SetRawSprite(ShootSprite);
                NeedStabilize = true;

                StateTimeMs -= ShootIntervalRnd;

                ShootIntervalRnd = ShootInterval + MathUtils.random(-1f, 1f) * ShootInterval * 0.5f;

                StabilizeTimeMs = Math.min(0.1f, ShootIntervalRnd * .5f);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean DetectPlayer(float delta)
    {
        if (!GetLayer().GetState().GetPlayerPos(PlayerPos))
        {
            return false;
        }

        PlayerObject player = (PlayerObject)GetLayer().GetGameObject(Globals.PLAYER_ID);
        if (player.IsDying())
        {
            return false;
        }

        PlayerPosMeters.set(PlayerPos).scl(Globals.PIXELS_TO_METERS);

        ViewDir.set(GetFlipX() ? -1 : 1, 0f);
        ViewDir.rotate(getRotation());

        ToPlayer.set(PlayerPos).sub(DetectorPoint);

        float angle = ToPlayer.angle(ViewDir);
        float distSq = ToPlayer.len2();

        FOVDebugUp.set(ViewDir).nor().scl(Distance);
        FOVDebugUp.rotate(-FOV * .5f);
        FOVDebugDown.set(FOVDebugUp);
        FOVDebugDown.rotate(FOV);

        FOVDebugUp.add(DetectorPoint);
        FOVDebugDown.add(DetectorPoint);

        PlayerInFOV = ((Math.abs(angle) <= FOV) && (distSq < Distance * Distance));

        if (!PlayerInFOV)
        {
            return false;
        }

        HitPoint.set(0f, 0f);
        boolean result = Checker.CheckNearest(DetectorPointMeters, PlayerPosMeters, PLAYER_BIT, (GROUND_BIT | ITEM_BIT), HitPoint);
        HitPoint.scl(Globals.METERS_TO_PIXELS);

        return result;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        PlayerVisible = DetectPlayer(delta);

        StateTimeMs += delta;

        FSM.update();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Shoot(int numShots)
    {
        Shoot(ShootPoint.x, ShootPoint.y, PlayerPos.x, PlayerPos.y);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void FireBullet(float startX, float startY, float speedX, float speedY)
    {
        Bullet bullet = new Bullet(this, ThePhysics, startX, startY, speedX, speedY, 1f, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Vector2 BulletVelocity = new Vector2();

    private Vector2 Tmp = new Vector2();

    public void Shoot(float sourceX, float sourceY, float targetX, float targetY)
    {
        BulletVelocity.set(targetX, targetY).sub(sourceX, sourceY).nor();
        BulletVelocity.scl(BulletSpeed * Globals.PIXELS_TO_METERS);

        FireBullet(sourceX, sourceY, BulletVelocity.x, BulletVelocity.y);

        final float flameWidth = 64;
        final float flameHeight = 64;
        Tmp.set(1f, 0f);
        float rotation = Tmp.angle(BulletVelocity);

        Tmp.set(flameWidth * .35f, 0f);
        Tmp.rotate(rotation);
        new SimpleEffect(GetLayer(), sourceX + Tmp.x, sourceY + Tmp.y, flameWidth, flameHeight, rotation, "bullet_flame", 0.01f, 0.02f, 0.02f, 1f);

        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), TURRET_SHOOT_SOUND), getX(), getY(), false);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!getDebug())
        {
            return;
        }
        shapes.setColor(PlayerInFOV ? Color.CORAL : Color.LIGHT_GRAY);
        shapes.line(DetectorPoint, FOVDebugUp);
        shapes.line(DetectorPoint, FOVDebugDown);
        shapes.line(FOVDebugUp, FOVDebugDown);

        shapes.setColor(Color.YELLOW);
        shapes.circle(DetectorPoint.x, DetectorPoint.y, 2);

        shapes.setColor(Color.RED);
        shapes.circle(ShootPoint.x, ShootPoint.y, 2);

        if (PlayerInFOV)
        {
            shapes.setColor(PlayerVisible ? Color.RED : Color.GREEN);
            shapes.line(DetectorPoint, HitPoint);
            shapes.circle(HitPoint.x, HitPoint.y, 2);

            shapes.setColor(Color.YELLOW);
            shapes.circle(PlayerPos.x, PlayerPos.y, 2);
        }

        super.drawDebug(shapes);
    }
}
