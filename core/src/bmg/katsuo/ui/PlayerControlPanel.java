package bmg.katsuo.ui;

import bmg.katsuo.Globals;
import bmg.katsuo.IApplication;
import bmg.katsuo.Settings;
import bmg.katsuo.controllers.PlayerController;
import bmg.katsuo.gameplay.GameEvents;
import bmg.katsuo.gameplay.events.WeaponChangedEventArgs;
import bmg.katsuo.gameplay.objects.PlayerObject;
import bmg.katsuo.gameplay.weapons.WeaponMeasure;
import bmg.katsuo.gameplay.weapons.Weapons;
import bmg.katsuo.input.InputDefaults;
import bmg.katsuo.managers.ResourceManager;
import bmg.katsuo.systems.Box2dPhysicsSystem;
import bmg.katsuo.utils.Event;
import bmg.katsuo.utils.EventListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class PlayerControlPanel extends Table
{
    private IApplication App;
    private TextureAtlas UIAtlas;
    private TextureAtlas Atlas;
    private Touchpad Touch;

    private final float Opacity = 0.3f;
    private final float MoveButtonsSpacing = 30f;
    private final float ActionButtonsOffsetPercent = .75f;
    private final float ActionButtonsSpacing = 30f;

    private Table DisplayPanel;
    private Table WeaponsPanel;

    private Map<Weapons.WeaponType, WeaponSlot> WeaponsSlots = new HashMap<Weapons.WeaponType, WeaponSlot>();

    private NinePatchDrawable BackgroundDrawable;
    private NinePatchDrawable BackgroundSelectedDrawable;
    //-------------------------------------------------------------------------------------------------------------------------

    PlayerControlPanel(RenderScreen owner, Settings settings, float height)
    {
        super(owner.GetApp().GetSkin());

        setTouchable(Touchable.enabled);

        App = owner.GetApp();
        Atlas = App.GetCommonResources().GetAtlas(ResourceManager.UI_ATLAS);
        UIAtlas = owner.GetAtlas();

        setHeight(height);

        //BackgroundTexture = TextureUtils.CreateColoredTexture(new Color(0, 0, 0, 0.9f));
        TextureRegion bgReg = Atlas.findRegion("bg_weapon");
        BackgroundDrawable = new NinePatchDrawable(new NinePatch(bgReg, 7, 7, 7, 7));
        bgReg = Atlas.findRegion("bg_weapon_selected");
        BackgroundSelectedDrawable = new NinePatchDrawable(new NinePatch(bgReg, 7, 7, 7, 7));

        // Add some buttons spacing
        float movePanelWidth = getHeight();

        float extraWidth = 0f;
        if (settings.GetControlsType() == Settings.PlayerControlsType.CONTROLS_BUTTONS)
        {
            extraWidth = MoveButtonsSpacing * 1.5f;
        }
        Actor movePanel = CreateMovePanel(settings.GetControlsType(), this, movePanelWidth + extraWidth, movePanelWidth);
        DisplayPanel = CreateDisplayPanel(movePanelWidth / 2);
        Actor actionsPanel = CreateActionsPanel(settings.GetHandType(), getHeight());

        if (settings.GetHandType() == Settings.PlayerHandType.RIGHT_HANDED)
        {
            add(movePanel).width(movePanelWidth + extraWidth).height(movePanelWidth);
            add(DisplayPanel).fill().expand(); // spacer
            add(actionsPanel);
        }
        else
        {
            add(actionsPanel);
            add(DisplayPanel).fill().expand(); // spacer
            add(movePanel).width(movePanelWidth + extraWidth).height(movePanelWidth);
        }

        App.GetEvents().AddEventListener(GameEvents.EventId.EVENT_WEAPON_CHANGED, new EventListener<WeaponChangedEventArgs>()
        {
            @Override
            public void Process(Event<WeaponChangedEventArgs> event, WeaponChangedEventArgs args)
            {
                if (!WeaponsSlots.containsKey(args.WType))
                {
                    return;
                }

                final WeaponSlot slot = WeaponsSlots.get(args.WType);

                slot.Set(args.Measure);
            }
        });
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        //BackgroundTexture.dispose();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private PlayerController GetPlayerController()
    {
        if (App.GetState() == null)
        {
            return null;
        }

        if (App.GetState().GetPlaygroundLayer() == null)
        {
            return null;
        }

        PlayerObject player = (PlayerObject)App.GetState().GetPlaygroundLayer().GetGameObject(Globals.PLAYER_ID);

        return player.GetController(PlayerController.class);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Table CreateMovePanel(Settings.PlayerControlsType controlsType, Table controlsPanel, float width, float height)
    {
        Table moveButtonsPanel = new Table(App.GetSkin());
        moveButtonsPanel.pad(0f);
        moveButtonsPanel.setWidth(width);//height);
        moveButtonsPanel.setHeight(height);

        if (controlsType == Settings.PlayerControlsType.CONTROLS_JOYSTICK)
        {
            Touch = new Touchpad(0, App.GetSkin());
            Touch.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor)
                {
                    float px = ((Touchpad) actor).getKnobPercentX();
                    float py = ((Touchpad) actor).getKnobPercentY();

                    float gx = 9.8f * px;
                    float gy = (py > 0) ? (-9.8f + 9.8f * py) : (-9.8f);

                    if (App.GetState() != null)
                    {
                        Box2dPhysicsSystem b2ds = App.GetState().GetPlaygroundLayer().GetSystem(Box2dPhysicsSystem.class);
                        if (b2ds != null)
                        {
                            b2ds.SetGravity(gx, gy);
                        }
                    }
                }
            });

            moveButtonsPanel.add(Touch).fill().expand();
        }
        else //if (controlsType == Settings.PlayerControlsType.CONTROLS_BUTTONS)
        {
            Button leftButton = new Button(new TextureRegionDrawable(Atlas.findRegion("button_left")), new TextureRegionDrawable(Atlas.findRegion("button_left_pressed")));
            leftButton.addListener(new ClickListener()
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    //@TODO: refactor this!!!
                    PlayerController playerContr = GetPlayerController();
                    if (playerContr != null)
                    {
                        playerContr.SetNeedMoveLeft(true);
                    }

                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                {
                    //@TODO: refactor this!!!
                    PlayerController playerContr = GetPlayerController();
                    if (playerContr != null)
                    {
                        playerContr.SetNeedMoveLeft(false);
                    }

                    super.touchUp(event, x, y, pointer, button);
                }
            });
            leftButton.setName("left_button");

            Button rightButton = new Button(new TextureRegionDrawable(Atlas.findRegion("button_right")), new TextureRegionDrawable(Atlas.findRegion("button_right_pressed")));
            rightButton.addListener(new ClickListener()
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    //@TODO: refactor this!!!
                    PlayerController playerContr = GetPlayerController();
                    if (playerContr != null)
                    {
                        playerContr.SetNeedMoveRight(true);
                    }

                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                {
                    //@TODO: refactor this!!!
                    PlayerController playerContr = GetPlayerController();
                    if (playerContr != null)
                    {
                        playerContr.SetNeedMoveRight(false);
                    }

                    super.touchUp(event, x, y, pointer, button);
                }
            });
            rightButton.setName("right_button");

            float buttonSize = height / 2;

            moveButtonsPanel.row();
            moveButtonsPanel.add().height(height / 4).colspan(2).fillY().expandY();

            moveButtonsPanel.row().height(buttonSize);
            moveButtonsPanel.add(leftButton).fill().expand().spaceRight(MoveButtonsSpacing);
            moveButtonsPanel.add(rightButton).fill().expand();
        }

        moveButtonsPanel.getColor().a = Opacity;
        return moveButtonsPanel;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Table CreateActionsPanel(Settings.PlayerHandType handType, float height)
    {
        Button jumpButton = new Button(new TextureRegionDrawable(Atlas.findRegion("button_x")), new TextureRegionDrawable(Atlas.findRegion("button_x_pressed")));
        App.GetInput().AddInputMapping(InputDefaults.JUMP_BUTTON, new UIButtonInputMapping(jumpButton));
        jumpButton.setName("jump_button");

        Button fireButton = new Button(new TextureRegionDrawable(Atlas.findRegion("button_y")), new TextureRegionDrawable(Atlas.findRegion("button_y_pressed")));
        App.GetInput().AddInputMapping(InputDefaults.FIRE_BUTTON, new UIButtonInputMapping(fireButton));
        fireButton.setName("fire_button");

        float actButtonsPanelHalfSize = height / 2;
        float buttonSize = actButtonsPanelHalfSize;

        Table actionButtonsPanel = new Table(App.GetSkin());
        actionButtonsPanel.setWidth(height);
        actionButtonsPanel.setHeight(height);

        actionButtonsPanel.row().height(height);

        float offset = MathUtils.lerp(0, actButtonsPanelHalfSize, ActionButtonsOffsetPercent);

        Table tJump = new Table();
        tJump.row().height(actButtonsPanelHalfSize - buttonSize / 2 + offset / 2);
        tJump.add().fillX().expandX();
        tJump.row();
        tJump.add(jumpButton).height(buttonSize).fillX().expandX();
        tJump.row();
        tJump.add().fillY().expandY();

        Table tFire = new Table();
        tFire.row().height(actButtonsPanelHalfSize - buttonSize / 2 - offset / 2);
        tFire.add().fillX().expandX();
        tFire.row();
        tFire.add(fireButton).height(buttonSize).fillX().expandX();
        tFire.row();
        tFire.add().fillY().expandY();

        if (handType == Settings.PlayerHandType.RIGHT_HANDED)
        {
            actionButtonsPanel.add(tJump).width(actButtonsPanelHalfSize);
            actionButtonsPanel.add(tFire).width(actButtonsPanelHalfSize).spaceLeft(ActionButtonsSpacing);
        }
        else
        {
            actionButtonsPanel.add(tFire).width(actButtonsPanelHalfSize);
            actionButtonsPanel.add(tJump).width(actButtonsPanelHalfSize).spaceLeft(ActionButtonsSpacing);
        }

        actionButtonsPanel.getColor().a = Opacity;
        return actionButtonsPanel;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private class WeaponSlot extends Table
    {
        private Label CountLabel;
        private Button TheButton;
        private Weapons.WeaponType Type;
        private boolean Enabled;
        //private Image Selection;
        private boolean Selected;

        public WeaponSlot(float width, float height, String image, Weapons.WeaponType type)
        {
            //setBackground(new Image(BackgroundTexture).getDrawable());
            setBackground(BackgroundDrawable);

            Type = type;
            setSize(width, height);
            setTouchable(Touchable.enabled);

            TheButton = new Button(new TextureRegionDrawable(Atlas.findRegion(image)), new TextureRegionDrawable(Atlas.findRegion(image)));
            TheButton.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    SelectWeapon(Type);
                }
            });
            add(TheButton).size(width);

            row();

            CountLabel = new Label("0", App.GetSkin(), "homespun-14", Color.WHITE);
            CountLabel.setFontScale(1.5f);
            CountLabel.setAlignment(Align.center);
            add(CountLabel);//.size(width, (height - width) * .5f);

            //row().height(20f);
            //Selection = new Image(new TextureRegionDrawable(Atlas.findRegion("selection")));
            //add(Selection).size(width, (height - width) * .5f);
            //Selection.setVisible(false);

            SetEnabled(true);
            SetSelected(false);
        }

        private void SetEnabled(boolean enabled)
        {
            Enabled = enabled;

            TheButton.setColor(1f, 1f, 1f, Enabled ? 1.0f : 0.4f);
            TheButton.setTouchable(Enabled ? Touchable.enabled : Touchable.disabled);
            CountLabel.setVisible(Enabled);
        }

        public boolean IsSelected()
        {
            return Selected;
        }

        public void SetSelected(boolean selected)
        {
            Selected = selected;
            //Selection.setVisible(Selected);
            //Selection.addAction(selected ? Actions.fadeIn(0.2f) : Actions.fadeOut(0.2f));

            setBackground(Selected ? BackgroundSelectedDrawable : BackgroundDrawable);
        }

        public void Set(WeaponMeasure measure)
        {
            if (measure.Type == WeaponMeasure.MeasureType.MEASURE_DISABLED)
            {
                SetEnabled(false);
            }
            else if (!Enabled)
            {
                SetEnabled(true);

                // First time we've got weapon - switch to it
                SelectWeapon(Type);
            }

            CountLabel.setText(measure.Type == WeaponMeasure.MeasureType.MEASURE_INFINITE ? "999" : String.valueOf(measure.Count));
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private WeaponSlot AddWeaponSlot(Table table, float width, float height, String image, Weapons.WeaponType type)
    {
        final WeaponSlot slot = new WeaponSlot(width, height, image, type);
        table.add(slot).width(width).height(height).spaceRight(4f);
        WeaponsSlots.put(type, slot);
        return slot;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Table CreateWeaponsPanel(float height)
    {
        float slotWidth = height * 0.8f;
        float slotHeight = height;

        Table table = new Table();
        table.setTouchable(Touchable.enabled);

        table.row().height(height);

        table.add().expandX().fillX(); // left expander

        WeaponSlot bulletSlot = AddWeaponSlot(table, slotWidth, slotHeight, "pistol-icon", Weapons.WeaponType.WEAPON_BULLET);
        bulletSlot.SetSelected(true);
        AddWeaponSlot(table, slotWidth, slotHeight, "grenade-icon", Weapons.WeaponType.WEAPON_GRENADE);
        AddWeaponSlot(table, slotWidth, slotHeight, "smoke-grenade-icon", Weapons.WeaponType.WEAPON_SMOKE_GRENADE);
        AddWeaponSlot(table, slotWidth, slotHeight,"bomb-icon", Weapons.WeaponType.WEAPON_BOMB);
        AddWeaponSlot(table, slotWidth, slotHeight,"beamgun-icon", Weapons.WeaponType.WEAPON_BEAM);

        table.add().expandX().fillX(); // right expander

        return table;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Table CreateDisplayPanel(float height)
    {
        Table table = new Table();
        table.setTouchable(Touchable.enabled);

        table.row();
        table.add().grow();

        float weaponsPanelHeight = height * 0.72f;

        table.row().height(weaponsPanelHeight);

        WeaponsPanel = CreateWeaponsPanel(weaponsPanelHeight);
        table.add(WeaponsPanel).expandX().fillX().padBottom(-RenderScreen.FramePadding);

        return table;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SelectWeapon(Weapons.WeaponType type)
    {
        if (!WeaponsSlots.containsKey(type))
        {
            return;
        }

        Iterator<WeaponSlot> slots = WeaponsSlots.values().iterator();
        while (slots.hasNext())
        {
            WeaponSlot slot = slots.next();
            // Check already selected
            if (slot.IsSelected() && slot.Type == type)
            {
                return;
            }

            slot.SetSelected(false);
        }

        WeaponSlot slot = WeaponsSlots.get(type);
        slot.SetSelected(true);

        if (App.GetState() != null && App.GetState().GetPlaygroundLayer() != null)
        {
            PlayerObject player = (PlayerObject) App.GetState().GetPlaygroundLayer().GetGameObject(Globals.PLAYER_ID);
            if (player != null)
            {
                player.SetWeaponType(type);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
