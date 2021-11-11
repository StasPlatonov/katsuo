package bmg.katsuo.gameplay.objects;

import bmg.katsuo.controllers.Box2dController;
import bmg.katsuo.controllers.LightController;
import bmg.katsuo.controllers.PlayerController;
import bmg.katsuo.gameplay.*;
import bmg.katsuo.gameplay.enemies.SawObject;
import bmg.katsuo.gameplay.events.*;

import bmg.katsuo.gameplay.weapons.*;
import bmg.katsuo.gameplay.weapons.Weapons.*;
import bmg.katsuo.managers.CharacterParameters;
import bmg.katsuo.managers.CharacterStateAnimation;
import bmg.katsuo.managers.MaterialManager;
import bmg.katsuo.objects.GameObject;
import bmg.katsuo.physics.Collider;

import bmg.katsuo.ui.AtlasSpriteEx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.*;

import static bmg.katsuo.gameplay.GameEvents.EventId.*;
import static bmg.katsuo.objects.Types.*;

public class PlayerObject extends PlatformerObject
{
    private PlayerController PlayerCtrl;
    private long LastEnemyCollisionTime = 0;
    private GameObject LastCollidedEnemy = null;
    private final static long ENEMY_COLLISION_INTERVAL = 2 * 1000;

    // Weapons stuff
    public class WeaponParameters
    {
        public String Image;
        public Vector2 Size = new Vector2();
        public Vector2 HandOffset = new Vector2();
        public Vector2 FireOffset = new Vector2();
        public long FireRate;

        public WeaponParameters(String image, float width, float height, float hOffsX, float hOffsY, float fOffsX, float fOffsY, long fireRate)
        {
            Image = image;
            Size.set(width, height);
            HandOffset.set(hOffsX, hOffsY);
            FireOffset.set(fOffsX, fOffsY);
            FireRate = fireRate;
        }
    }
    private Map<WeaponType, WeaponParameters> WeaponsParameters = new HashMap<WeaponType, WeaponParameters>();
    private Map<WeaponType, Sprite> WeaponsRender = new HashMap<WeaponType, Sprite>();
    private WeaponType CurrentWeapon = WeaponType.WEAPON_BULLET;
    private Sprite CurrentWeaponRender = null;

    private float StartWalkTime = 0;
    private float StartRunTime = 0;
    private float StartHurtTime = 0;

    // Player's weapon properties
    private Vector2 WeaponPosition = new Vector2();
    private float WeaponAngle = 0f;
    //All weapons
    private long LastFireTime = 0;
    // Bomb
    public final static float BOMB_PLAYER_DISTANCE = 2f;
    // Beamgun
    private Beam TheBeam = null;

    private LightObject TheLight;

    private PlayerState PState;

    private Map<CharacterParameters.EffectType, AnimationEffectsPool> Effects = new HashMap<CharacterParameters.EffectType, AnimationEffectsPool>();

    private float JumpForce;

    private ItemObject ItemToTake;

    private boolean Movable;
    private Color GroundMaterialColor = new Color();
    //-------------------------------------------------------------------------------------------------------------------------

    private short GetMaskBits()
    {
        return GROUND_BIT | ENEMY_WEAKNESS_BIT | ENEMY_LETHAL_BIT | ITEM_BIT | TRIGGER_BIT | LADDER_BIT | BREAKABLE_BIT | WEAPON_BIT;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private CharacterParameters TheCharacter;
    private Map<Integer, Float> AnimationsDuration = new HashMap<Integer, Float>();
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        properties.put("phys_object_type", "dynamic");
        properties.put("phys_friction", 5f);
        properties.put("phys_category", (int)PLAYER_BIT);
        properties.put("phys_mask", (int)GetMaskBits());

        final String characterName = properties.get("character", "Greeny", String.class);
        CharacterParameters character = GetLayer().GetApp().GetCharacterManager().GetCharacter(characterName);
        properties.put("phys_link", character == null ? "" : character.PhysicsName);

        super.Init(properties);

        ApplyCharacter(character);

        SetState(ES_IDLE);

        //Script = GetApp().GetScriptManager().LoadScript(getClass().getSimpleName(), getClass().getSimpleName());

        WeaponsParameters.put(WeaponType.WEAPON_BULLET, new WeaponParameters("pistol-weapon", 16f, 16f, 10f, 6f, 0f, 0f, 200L));
        WeaponsParameters.put(WeaponType.WEAPON_GRENADE, new WeaponParameters("grenade-green-weapon", 16f, 16f, 14f, 6f, 10f, 0f, 800L));
        WeaponsParameters.put(WeaponType.WEAPON_SMOKE_GRENADE, new WeaponParameters("flash-grenade-weapon", 16f, 16f, 14f, 6f, 10f, 0f, 800L));
        WeaponsParameters.put(WeaponType.WEAPON_BEAM, new WeaponParameters("beamgun-weapon", 16f, 16f, 10f, 6f, 0f, 0f, 1000L));
        WeaponsParameters.put(WeaponType.WEAPON_BOMB, new WeaponParameters("bomb-weapon", 16f, 16f, 14f, 6f, 0f, 0f, 1500L));

        InitWeapons(WeaponsParameters);

        //@TODO: for animated characters weapon still frozen on the same position
        CurrentWeaponRender = WeaponsRender.get(WeaponType.WEAPON_BULLET);

        Sounds.Add(getClass().getSimpleName(), JUMP_SOUND, properties.get("jump_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), HIT_SOUND, properties.get("hit_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), DIE_SOUND, properties.get("die_sound", "", String.class));
        Sounds.Add(getClass().getSimpleName(), WEAPON_SWITCH_SOUND, properties.get("weapon_sound", "", String.class));

        CreatePlayerLight(100, Color.valueOf("#BBBB99AA"));
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void ApplyCharacter(CharacterParameters character)
    {
        final Map<Integer, CharacterStateAnimation> animations = character.GetAnimations();

        ClearAnimations();
        AnimationsDuration.clear();

        // Create animations
        final Set<Integer> states = animations.keySet();
        for (int state : states)
        {
            final CharacterStateAnimation stateAnimation = animations.get(state);

            // Create smart animation or simple
            Animation animation;
            if (stateAnimation.AnimationSuffix.isEmpty())
            {
                final String animationName = character.Name + stateAnimation.AnimationName;
                animation = CreateAnimationSmart(state, getName() + "_" + stateAnimation.AnimationName, animationName, stateAnimation.FrameDuration, stateAnimation.Mode, stateAnimation.StopOnLastFrame);
            }
            else
            {
                final String animationName = character.Name + stateAnimation.AnimationSuffix;
                animation = CreateAnimation(state, getName() + "_" + stateAnimation.AnimationSuffix, animationName, stateAnimation.FrameDuration, stateAnimation.Mode, stateAnimation.StopOnLastFrame);
            }
            AnimationsDuration.put(state, animation.getAnimationDuration());
        }

        // Create effects
        final Map<CharacterParameters.EffectType, AnimationEffectParameters> effects = character.GetEffects();

        final Set<CharacterParameters.EffectType> effectTypes = effects.keySet();
        for (CharacterParameters.EffectType effectType : effectTypes)
        {
            final AnimationEffectParameters effectParams = effects.get(effectType);
            Effects.put(effectType, new AnimationEffectsPool(GetLayer(), this, effectParams));
        }

        TheCharacter = character;

        GetApp().Log(TAG, "Character of '" + getName() + "' set to '" + character.Name + "'");
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void CreatePlayerLight(int radius, Color color)
    {
        MapProperties lightProps = new MapProperties();
        lightProps.put("type", "LightObject");
        lightProps.put("light_type", "point");
        lightProps.put("strength", radius);
        lightProps.put("color", color);
        TheLight = (LightObject)GetState().CreateObject(GetLayer(), "player_light", lightProps);
        if (PhysController != null)
        {
            Body body = PhysController.GetBody();
            LightController lightContr = TheLight.GetController(LightController.class);
            lightContr.AttachTo(body);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void CreateControllers()
    {
        super.CreateControllers();

        PlayerCtrl = new PlayerController(GetApp().GetInput());
        AddController(PlayerCtrl);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void InitWeapons(Map<WeaponType, WeaponParameters> weapons)
    {
        Iterator<WeaponType> wIt = WeaponsParameters.keySet().iterator();
        while (wIt.hasNext())
        {
            WeaponType wType = wIt.next();
            final WeaponParameters wParams = WeaponsParameters.get(wType);

            if (!wParams.Image.isEmpty())
            {
                Sprite sprite = GetState().GetSprite(wParams.Image);
                sprite.setSize(wParams.Size.x, wParams.Size.y);
                WeaponsRender.put(wType, sprite);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected PlatformerState CreatePlatformerState()
    {
        PState = new PlayerState(this, 100, 100, 0);
        return PState;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void InitPhysics(Box2dController controller)
    {
        super.InitPhysics(controller);
 /*
        float widthK = GetProperties().get("phys_width_k", 1.0f, Float.class);
        float heightK = GetProperties().get("phys_height_k", 1.0f, Float.class);

        // Sides sensor to slide of walls
        FixtureDef edgesFixtureDef = new FixtureDef();
        edgesFixtureDef.density = 1f;
        edgesFixtureDef.friction = 0f; // slide forever
        edgesFixtureDef.filter.categoryBits = body.getFixtureList().get(0).getFilterData().categoryBits;
        edgesFixtureDef.filter.maskBits = body.getFixtureList().get(0).getFilterData().maskBits;

        float physWidth = getWidth() * widthK;
        float physHeight = getHeight() * heightK;
        // Left vertical edge
        Vector2 v1 = new Vector2(0f, physHeight * 0.1f).sub(physWidth * .5f,physHeight * .5f).scl(Globals.PIXELS_TO_METERS);
        Vector2 v2 = new Vector2(0f, physHeight * 0.9f).sub(physWidth * .5f,physHeight * .5f).scl(Globals.PIXELS_TO_METERS);

        // Right vertical edge
        Vector2 v3 = new Vector2(physWidth, physHeight * 0.1f).sub(physWidth * .5f,physHeight * .5f).scl(Globals.PIXELS_TO_METERS);
        Vector2 v4 = new Vector2(physWidth, physHeight * 0.9f).sub(physWidth * .5f,physHeight * .5f).scl(Globals.PIXELS_TO_METERS);

        // Left edge
        EdgeShape leftEdgeShape = new EdgeShape();
        leftEdgeShape.set(v1, v2);

        edgesFixtureDef.shape = leftEdgeShape;
        body.createFixture(edgesFixtureDef).setUserData(this);
        leftEdgeShape.dispose();

        // Right edge
        EdgeShape rightEdgeShape = new EdgeShape();
        rightEdgeShape.set(v3, v4);

        edgesFixtureDef.shape = rightEdgeShape;
        body.createFixture(edgesFixtureDef).setUserData(this);
        rightEdgeShape.dispose();
*/
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public WeaponType GetWeaponType()
    {
        return CurrentWeapon;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetWeaponType(WeaponType type)
    {
        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), WEAPON_SWITCH_SOUND), getX(), getY(), false);

        CurrentWeapon = type;

        CurrentWeaponRender = WeaponsRender.containsKey(CurrentWeapon) ? WeaponsRender.get(CurrentWeapon) : null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetWeaponParameters(float x, float y, float angle)
    {
        WeaponPosition.set(x, y);
        WeaponAngle = angle;

        if (TheBeam != null)
        {
            TheBeam.SetPosition(WeaponPosition);
            TheBeam.SetAngle(WeaponAngle);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetMovable(boolean movable)
    {
        Movable = movable;
    }

    public boolean IsMovable()
    {
        return Movable;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private float GetAnimationDuration(int state)
    {
        return AnimationsDuration.containsKey(state) ? AnimationsDuration.get(state) : 0f;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void UpdateStates(float delta)
    {
        super.UpdateStates(delta);

        if (GetCurrentState() == ES_HURT)
        {
            if (GetLife() - StartHurtTime > GetAnimationDuration(ES_HURT))
            {
                SetState(ES_IDLE);
            }
        }
        switch (GetCurrentState())
        {
            case ES_IDLE:
            case ES_WALK:
            case ES_RUN:
            case ES_JUMP:
            case ES_FALL:
            case ES_HURT:
            case ES_ATTACK:
            case ES_CLIMB:
            case ES_SLIDING:
            {
                float weaponBaseX = GetTrueX() + ((GetDirection() == MoveDirection.MD_RIGHT) ? getWidth() : 0f);
                float weaponBaseY = GetTrueY() + getHeight() / 2;
                float weaponAngle = (GetDirection() == MoveDirection.MD_RIGHT) ? 0f : 180f;

                    /*
                    // Shoot to cursor
                    final Vector2 cur = GetApp().GetState().GetCursorPosition();

                    float weaponAngle = (float)Math.toDegrees(Math.atan2(cur.y - WeaponPosition.y, cur.x - WeaponPosition.x));
                    if (weaponAngle < 0f)
                    {
                        weaponAngle += 360f;
                    }
                    */

                SetFlip(GetDirection() == MoveDirection.MD_LEFT, GetFlipY());

                if (CurrentWeaponRender != null)
                {
                    CurrentWeaponRender.setFlip(GetDirection() == MoveDirection.MD_LEFT, false);

                    final WeaponParameters weaponParams = WeaponsParameters.get(CurrentWeapon);

                    // Weapon position
                    float weaponX = GetDirection() == MoveDirection.MD_RIGHT ? weaponBaseX - weaponParams.HandOffset.x : weaponBaseX + weaponParams.HandOffset.x - weaponParams.Size.x;
                    float weaponY = weaponBaseY - weaponParams.Size.y * .5f - weaponParams.HandOffset.y;
                    CurrentWeaponRender.setPosition(weaponX, weaponY);

                    // Fire position
                    float fireX = GetDirection() == MoveDirection.MD_RIGHT ? weaponX + weaponParams.Size.x - weaponParams.FireOffset.x : weaponX + weaponParams.FireOffset.x;
                    float fireY = weaponY + weaponParams.Size.y * .5f - weaponParams.FireOffset.y;
                    SetWeaponParameters(fireX, fireY, weaponAngle);
                }
                else
                {
                    SetWeaponParameters(weaponBaseX, weaponBaseY, weaponAngle);
                }

                break;
            }

            case ES_DYING:
            {
                if (State.GetStateTime() > GetAnimationDuration(ES_DYING))
                {
                    SetState(ES_DEAD);
                }

                break;
            }

            case ES_DEAD:
            {
                if (State.GetStateTime() > GetAnimationDuration(ES_DEAD))
                {
                    SetKilled();
                    break;
                }

                float k = State.GetStateTime() / GetAnimationDuration(ES_DEAD);
                getColor().a = 1f - k;

                PhysController.SetSpeed(0f, 1.5f * k);

                break;
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        super.Update(delta);

        CheckFeelWalls();

        if (TheBeam != null)
        {
            if (TheBeam.IsDestroyed())
            {
                TheBeam.dispose();
                TheBeam = null;
            }
            else
            {
                final Vector2 beamDir = TheBeam.GetDirection();

                //float scale = 0.02f;
                //PhysController.Impulse(-beamDir.x * scale, -beamDir.y * scale, 0f, 0f);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetStartWalkTime()
    {
        return StartWalkTime;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Color UpdateGroundColor()
    {
        //@TODO: landing on a different ground types will cause bug
        if (!GroundFixtures.isEmpty() && GroundFixtures.get(0).getUserData() instanceof GameObject)
        {
            GameObject object = (GameObject)(GroundFixtures.get(0).getUserData());
            GroundMaterialColor.set(MaterialManager.GetMaterial(object.GetMaterial()).GetColor());
        }
        return GroundMaterialColor;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void OnLanded(float landY)
    {
        // Set dust color in dependence of material player landed on.
        AnimationEffect dustEffect = Effects.get(CharacterParameters.EffectType.ET_LAND_DUST).obtain();
        dustEffect.SetColor(UpdateGroundColor());
        dustEffect.Start(GetTrueX() + getWidth() * .5f - dustEffect.GetWidth() * .5f, landY);

        PlayerCtrl.OnLanded();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);

        if (!IsDying())
        {
            if ((CurrentWeaponRender != null) && (GetCurrentState() != ES_CLIMB))
            {
                float op = getColor().a * parentAlpha;
                CurrentWeaponRender.getColor().a = op;
                CurrentWeaponRender.draw(batch, op);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean CanCollideEnemy(GameObject enemy)
    {
        if (LastCollidedEnemy == enemy)
        {
            return (TimeUtils.millis() - LastEnemyCollisionTime) > ENEMY_COLLISION_INTERVAL;
        }
        else
        {
            return true;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnCollide(Collider collider, Collider.CollisionType type)
    {
        super.OnCollide(collider, type);

        Fixture fixture = collider.GetFixture();
        final int category = fixture.getFilterData().categoryBits;

        final Vector2 contactPoint = collider.GetManifold().getPoints()[0];

        if (type == Collider.CollisionType.COLLISION_END)
        {
            if ((category & ITEM_BIT) == ITEM_BIT)
            {
                if (collider.GetObject() instanceof ItemObject)
                {
                    ItemObject item = (ItemObject) collider.GetObject();
                    if ((item != null) && (item == ItemToTake)) // some items (e.g. thrown grenades) are not GameObject
                    {
                        ItemToTake = null;
                    }
                }
            }
            return;
        }

        if ((category & ITEM_BIT) == ITEM_BIT)
        {
            if (collider.GetObject() instanceof ItemObject)
            {
                ItemObject item = (ItemObject) collider.GetObject();
                if (item != null) // some items (e.g. thrown grenades) are not GameObject
                {
                    GetApp().GetEvents().Event(GameEvents.EventId.EVENT_TAKE_ITEM, new TakeItemEventArgs(this, item));
                    if (!item.IsTaken())
                    {
                        ItemToTake = item;
                    }
                }
            }
        }
        else if ((category & ENEMY_WEAKNESS_BIT) == ENEMY_WEAKNESS_BIT)
        {
            if (CanCollideEnemy(collider.GetObject()))
            {
                LastEnemyCollisionTime = TimeUtils.millis();
                LastCollidedEnemy = collider.GetObject();

                GetApp().GetEvents().Event(EVENT_DAMAGED_BY_ENEMY, new DamagedByEnemyEventArgs(this, collider.GetObject(), contactPoint, collider.GetObject().GetDamageType()));
            }
        }
        else if ((category & WEAPON_BIT) == WEAPON_BIT)
        {
            if (collider.GetFixture().getUserData() instanceof Bullet)
            {
                Bullet bullet = (Bullet)collider.GetFixture().getUserData();
                Damage(bullet.GetDamage(), contactPoint, DamageType.DAMAGE_BULLET);

                //Vector2 dir = new Vector2(bullet.GetSpeed()).nor();
                //CrateGhost(dir);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void InitKill()
    {
        if (IsDying())
        {
            return;
        }

        if (Script == null)
        {
            GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), DIE_SOUND), getX(), getY(), false);
        }
        else
        {
            Script.ExecuteFunction(getClass().getSimpleName(), "kill", this);
        }

        PhysController.SetFreezeBits(PLAYER_BIT, GROUND_BIT);

        setColor(Color.LIGHT_GRAY);

        if ((LastDamageType == DamageType.DAMAGE_EXPLODE) || !IsOnGround())
        {
            Explode();

            AnimController.SetPaused(true);
            setVisible(false);
            SetState(ES_DYING);
        }
        else
        {
            SetState(ES_DYING);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Explode()
    {
        AtlasSpriteEx render = Render;

        int numParts = 9;

        int nx = (int)Math.sqrt(numParts);
        int ny = nx;

        float partW = getWidth() / nx;
        float partH = getHeight() / ny;

        //
        float deltaU = Math.abs(Render.getU2() - Render.getU());
        float deltaV = Math.abs(Render.getV2() - Render.getV());

        float partDeltaU = partW * deltaU / getWidth();
        float partDeltaV = partH * deltaV / getHeight();

        for (int y = 0; y < ny; ++y)
        {
            for (int x = 0; x < nx; ++x)
            {
                AtlasSpriteEx sprite = new AtlasSpriteEx(Render.getAtlasRegion());

                float u = Render.getU() + x * partDeltaU;
                float u2 = u + partDeltaU;
                float v = Render.getV() + y * partDeltaV;
                float v2 = v + partDeltaV;

                sprite.setU(u);
                sprite.setU2(u2);
                sprite.setV(v);
                sprite.setV2(v2);

                BrokenPart part = new BrokenPart(GetLayer(), ThePhysics, GetTrueX() + x * partW, GetTrueY() + y * partH, partW, partH, 2f, sprite);

                float fx = MathUtils.random(-0.01f, 0.01f);
                float fy = MathUtils.random(-0.01f, 0.01f);
                part.Push(fx, fy);

                GetLayer().addActor(part);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    // Revert some properties after death
    public void Reborn()
    {
        PhysController.SetFreezeBits(PLAYER_BIT, GetMaskBits());
        setColor(Color.WHITE);
        SetState(ES_IDLE);
        setVisible(true);
        AnimController.SetPaused(false);
        PhysController.GetBody().setType(BodyDef.BodyType.DynamicBody);
        PhysController.GetBody().setGravityScale(1f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void SetKilled()
    {
        super.SetKilled();

        GetApp().GetEvents().Event(GameEvents.EventId.EVENT_PLAYER_KILLED, new PlayerKilledEventArgs());
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Kick(Vector2 contact)
    {
        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), HIT_SOUND), getX(), getY(), false);

        PlayerController entContr = GetController(PlayerController.class);
        if ((entContr == null))
        {
            return;
        }

        // Calculate direction of force (opposite from contact)
        final Vector2 pos = PhysController.GetPosition();

        float kickSpeed = 2f;
        // P = mv
        float impX = (contact.x > pos.x ? -1f : 1f) * kickSpeed * PhysController.GetMass();
        float impY = 1f * kickSpeed * PhysController.GetMass();//2;

        PhysController.Impulse(impX, impY, 0f, 0f);

        addAction(Actions.repeat(4,
                Actions.sequence(
                        Actions.fadeOut(.1f),
                        Actions.fadeIn(.1f)
                ))
        );
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Jump(float kx, float ky)
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        JumpStartedTime = GetLife();

        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), JUMP_SOUND), getX(), getY(), false);

        SetState(PlatformerObject.ES_JUMP, true);

        PhysController.SetSpeed(kx, ky);

        SetDirection(kx > 0 ? MoveDirection.MD_RIGHT : MoveDirection.MD_LEFT);

        JumpForce = PhysController.GetMass() * 9.8f * 1.2f;

        if (!IsOnGround())
        {
            AnimationEffect airJumpEffect = Effects.get(CharacterParameters.EffectType.ET_AIR_JUMP).obtain();
            airJumpEffect.Start(GetTrueX(), GetTrueY());
        }
        else {
            AnimationEffect jumpEffect = Effects.get(CharacterParameters.EffectType.ET_JUMP).obtain();
            jumpEffect.Start(GetTrueX()+ getWidth() * .5f - jumpEffect.GetWidth() * .5f, GetTrueY());
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Jumping()
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }
        PhysController.SetForce(0f, JumpForce);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Walking(PlatformerObject.MoveDirection direction, float maxSpeed, float delta)
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        float targetSpeed = (direction == MoveDirection.MD_LEFT) ? -maxSpeed : maxSpeed;

        SetDirection(direction);

        if (GetCurrentState() != ES_WALK)
        {
            StartWalkTime = GetLife();
        }

        SetState(ES_WALK);

        PhysController.SetSpeed(MathUtils.lerp(PhysController.GetSpeed().x, targetSpeed, 0.9f), PhysController.GetSpeed().y);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Running(PlatformerObject.MoveDirection direction, float maxSpeed, float delta)
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        float targetSpeed = (direction == MoveDirection.MD_LEFT) ? -maxSpeed : maxSpeed;

        // Can not run while hurting
        if (IsHurting())
        {
            return;
        }

        SetDirection(direction);

        if (GetCurrentState() != ES_RUN)
        {
            StartRunTime = GetLife();
            AnimationEffect walkPushEffect = Effects.get(CharacterParameters.EffectType.ET_WALK_PUSH).obtain();
            walkPushEffect.SetColor(UpdateGroundColor());
            walkPushEffect.SetFlipX(GetDirection() == MoveDirection.MD_LEFT);
            walkPushEffect.Start(GetTrueX() + getWidth() * .5f - walkPushEffect.GetWidth() * .5f, GetTrueY());
        }

        SetState(ES_RUN);

        PhysController.SetSpeed(MathUtils.lerp(PhysController.GetSpeed().x, targetSpeed, 0.9f), PhysController.GetSpeed().y);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Flying(PlatformerObject.MoveDirection direction, float maxSpeed, float delta)
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        SetDirection(direction);

        //SetState(ES_WALK);

        float targetSpeed = (direction == MoveDirection.MD_LEFT) ? -maxSpeed : maxSpeed;

        PhysController.SetSpeed(MathUtils.lerp(PhysController.GetSpeed().x, targetSpeed, 0.9f), PhysController.GetSpeed().y);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Climbing(PlatformerObject.MoveDirection direction, float maxSpeed, float delta)
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        SetDirection(direction);

        SetState(ES_CLIMB);

        final float impulse = ((direction == MoveDirection.MD_LEFT) ? -1f : 1f) *PhysController.GetMass() * maxSpeed * delta;

        PhysController.SetForce(impulse, 0f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Falling()
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        SetState(ES_FALL);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void StopMoving()
    {
        if (!IsHurting())
        {
            SetState(ES_IDLE);
        }

        StartWalkTime = 0;
        StartRunTime = 0;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void StopClimb()
    {
        // Stop immediately on ladder (stick)
        PhysController.SetSpeed(0f, 0f);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Sliding(boolean left)
    {
        if (!IsUpdatable() || IsDying() || !IsMovable())
        {
            return;
        }

        SetDirection(left ? MoveDirection.MD_LEFT : MoveDirection.MD_RIGHT);
        SetState(ES_SLIDING);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean IsHurting()
    {
        return GetCurrentState() == ES_HURT;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Hurt()
    {
        if (GetCurrentState() == ES_HURT)
        {
            return;
        }

        if (GetCurrentState() != ES_HURT)
        {
            StartHurtTime = GetLife();
        }

        SetState(ES_HURT);
/*
        if (Hurting)
        {
            return;
        }

        Hurting = true;
        addAction(Actions.sequence(
                    Actions.repeat(4,
                        Actions.sequence(
                            Actions.fadeOut(.1f),
                            Actions.fadeIn(.1f)
                        )
                    ),
                    Actions.run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Hurting = false;
                        }
                    })
                )
        );*/
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Damage(float damage, DamageType type)
    {
        if (IsDying())
        {
            return;
        }

        GetApp().PlaySoundAt(Sounds.GetRandom(getClass().getSimpleName(), HIT_SOUND), getX(), getY(), false);

        super.Damage(damage, type);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Damage(float damage, Vector2 contactPoint, DamageType type)
    {
        Damage(damage, type);

        if (IsDying())
        {
            return;
        }

        switch (type)
        {
            case DAMAGE_TOUCH:
            {
                if (Script == null)
                {
                    Kick(contactPoint);
                }
                else
                {
                    Script.ExecuteFunction(getClass().getSimpleName(), "kick", this, contactPoint);
                }
                break;
            }

            case DAMAGE_BULLET:
            {
                Hurt();
                break;
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public PlayerInventory GetInventory()
    {
        return PState.GetInventory();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public PlayerState GetPState()
    {
        return PState;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void FireBullet(float startX, float startY, float speedX, float speedY)
    {
        Bullet bullet = new Bullet(this, ThePhysics, startX, startY, speedX, speedY, 1f);

        float impX = bullet.GetMass() * speedX * 10;
        float impY = bullet.GetMass() * speedY * 10;

        // Do not apply recoil on ladders
        if (GetCurrentState() != ES_CLIMB)
        {
            PhysController.Impulse(-impX, -impY, 0f, 0f);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void FireGrenade(float startX, float startY, float speedX, float speedY)
    {
        Grenade grenade = new Grenade(this, ThePhysics, startX, startY, speedX, speedY);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void FireSmokeGrenade(float startX, float startY, float speedX, float speedY)
    {
        SmokeGrenade grenade = new SmokeGrenade(this, ThePhysics, startX, startY, speedX, speedY);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Vector2 tmp = new Vector2();

    public void FireBeam(float angle)
    {
        //new AnimationEffect(GetLayer(), WeaponPosition.x - 48f, WeaponPosition.y - 48f + 2f, 96, 96, "portal", 0.03f);
        new AnimationEffect(GetLayer(), this, WeaponPosition.x - 48f, WeaponPosition.y - 48f + 2f, 96, 96, "portal", 0.03f);
        TheBeam = new Beam(this, ThePhysics, angle);

        //opposite direction
        tmp.set(1f, 0f);
        tmp.rotate(angle + 180f);

        // Do not apply recoil on ladders
        if (GetCurrentState() != ES_CLIMB)
        {
            // impulse = mass * speed
            float impX = 0.5f * 1f * tmp.x;
            float impY = 0.5f * 1f * tmp.y;
            PhysController.Impulse(impX, impY, 0f, 0f);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void FireBomb(float startX, float startY, float speedX, float speedY)
    {
        Bomb bomb = new Bomb(this, ThePhysics, startX, startY, speedX, speedY);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Fire()
    {
        long current = TimeUtils.millis();

        // Check availability
        final PlayerInventory inventory = PState.GetInventory();
        if (!inventory.IsWeaponAvailable(CurrentWeapon))
        {
            GetApp().PlaySound("noweapon", false);
            new Tooltip(this, GetApp().GetLocale().GetLocalizedString("weapon_empty"), 0.4f);
            return;
        }

        final WeaponParameters weaponParams = WeaponsParameters.get(CurrentWeapon);

        // Check fire rate
        if (current - LastFireTime < weaponParams.FireRate)
        {
            return;
        }

        float weaponBaseX = GetTrueX() + ((GetDirection() == MoveDirection.MD_RIGHT) ? getWidth() : 0f);
        float weaponBaseY = GetTrueY() + getHeight() / 2;

        final Vector2 playerSpeed = PhysController.GetSpeed();

        if (Script == null)
        {
            if (CurrentWeapon == WeaponType.WEAPON_BULLET)
            {
                GetApp().PlaySound("bullet", false);

                float bulletSpeed = 4f;
                float bvx = bulletSpeed * ((GetDirection() == MoveDirection.MD_RIGHT) ? 1.0f : -1.0f);
                float bvy = 0f;

                FireBullet(WeaponPosition.x, WeaponPosition.y, bvx, bvy);
            }
            else if (CurrentWeapon == WeaponType.WEAPON_GRENADE)
            {
                GetApp().PlaySound("throw", false);

                float grenadeSpeed = 1f;
                float bvx = grenadeSpeed * ((GetDirection() == MoveDirection.MD_RIGHT) ? 1f : -1f);
                float bvy = grenadeSpeed * 1f;

                float fireX = (GetDirection() == MoveDirection.MD_RIGHT) ? weaponBaseX + 2f : weaponBaseX - weaponParams.Size.x - 2f;
                float fireY = weaponBaseY;

                FireGrenade(fireX, fireY, playerSpeed.x + bvx, playerSpeed.y + bvy);
            }
            else if (CurrentWeapon == WeaponType.WEAPON_SMOKE_GRENADE)
            {
                GetApp().PlaySound("throw", false);

                float grenadeSpeed = 1f;
                float bvx = grenadeSpeed * ((GetDirection() == MoveDirection.MD_RIGHT) ? 1f : -1f);
                float bvy = grenadeSpeed * 1f;

                float fireX = (GetDirection() == MoveDirection.MD_RIGHT) ? weaponBaseX + 2f : weaponBaseX - weaponParams.Size.x - 2f;
                float fireY = weaponBaseY;

                FireSmokeGrenade(fireX, fireY, playerSpeed.x + bvx, playerSpeed.y + bvy);
            }
            else if (CurrentWeapon == WeaponType.WEAPON_BEAM)
            {
                GetApp().PlaySound("beam", false);
                float angle = (GetDirection() == MoveDirection.MD_RIGHT) ? 0 : 180f;
                FireBeam(angle);
            }
            else if (CurrentWeapon == WeaponType.WEAPON_BOMB)
            {
                GetApp().PlaySound("throw", false);

                float bx = WeaponPosition.x + ((GetDirection() == MoveDirection.MD_RIGHT) ? (BOMB_PLAYER_DISTANCE) : -(BOMB_PLAYER_DISTANCE + Bomb.BOMB_SIZE));
                float by = WeaponPosition.y;

                float bvx = 0.2f * ((GetDirection() == MoveDirection.MD_RIGHT) ? 1f : -1f);
                float bvy = 0.2f;

                FireBomb(bx, by, playerSpeed.x + bvx, playerSpeed.y + bvy);
            }

            inventory.AddWeapon(CurrentWeapon, WeaponMeasure.Count(-1));

            // check current collided item
            CheckItemToTake(CurrentWeapon);
        }
        else
        {
            Script.ExecuteFunction(getClass().getSimpleName(), "Fire", this);
        }
        LastFireTime = TimeUtils.millis();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void CheckItemToTake()
    {
        CheckItemToTake(CurrentWeapon);
    }

    private void CheckItemToTake(WeaponType currentWeapon)
    {
        if (ItemToTake == null)
        {
            return;
        }

        if (ItemToTake instanceof WeaponItemObject)
        {
            WeaponItemObject weaponItem = (WeaponItemObject) ItemToTake;
            if (weaponItem.GetWeaponType() != currentWeapon)
            {
                return;
            }
        }

        GetApp().GetEvents().Event(GameEvents.EventId.EVENT_TAKE_ITEM, new TakeItemEventArgs(this, ItemToTake));
        ItemToTake = null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String GetDebugString()
    {
        return super.GetDebugString() + String.format(" SPD=%.2f", PhysController.GetSpeed().len());
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetGodMode(boolean godMode)
    {
        PState.SetGodMode(godMode);
    }

    public boolean IsGodMode()
    {
        return PState.IsGodMode();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void OnKilled()
    {
        setVisible(false);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
