package bmg.katsuo.ui;

import bmg.katsuo.Globals;
import bmg.katsuo.IApplication;
import bmg.katsuo.Settings;
import bmg.katsuo.managers.ResourceManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class GameplayWindow extends Table
{
    private RenderScreen Owner;
    private IApplication App;
    private TextureAtlas Atlas;

    private Label HealthLabel;
    private ProgressBar HealthBar;
    private Label LivesLabel;
    private Label PowerLabel;
    private ProgressBar PowerBar;

    private java.util.List<TextureRegionDrawable> DrawableNumbers = new ArrayList<TextureRegionDrawable>();
    private java.util.List<Image> NumberImages = new ArrayList<Image>();
    private final int NumberOfDigits = 5;

    PlayerControlPanel ControlPanel;

    public GameplayWindow(RenderScreen owner, Table table, Settings settings)
    {
        final float topPanelHeight = Globals.GUI_HEIGHT * Globals.GAMEPLAY_TOP_PANEL_SIZE;
        final float spacing = 10;
        final float healthColumnWidth = Globals.GUI_WIDTH * 0.2f;

        Owner = owner;
        App = owner.GetApp();
        Atlas = App.GetCommonResources().GetAtlas(ResourceManager.UI_ATLAS);

        //VerticalGroup firstColumn = new VerticalGroup();
        //firstColumn.columnAlign(Align.left);

        Table healthPowerTable = new Table();

        // Health
        healthPowerTable.row().height(topPanelHeight / 2).spaceBottom(spacing);
        Image heartImg = new Image(Atlas.findRegion("heart"));
        healthPowerTable.add(heartImg).size(topPanelHeight / 2).spaceRight(spacing);

        Stack healthProgressGroup = new Stack();
        healthProgressGroup.setName("health_group");
        HealthBar = CreateProgress("health", topPanelHeight / 2, Color.LIME);
        healthProgressGroup.addActor(HealthBar);

        HealthLabel = new Label("0%", App.GetSkin(), Owner.LocalizedFontName, Color.WHITE);
        HealthLabel.setAlignment(Align.center);
        healthProgressGroup.addActor(HealthLabel);

        healthPowerTable.add(healthProgressGroup).width(healthColumnWidth);

        // Power
        healthPowerTable.row().height(topPanelHeight / 2);
        Image powerImg = new Image(Atlas.findRegion("powerup"));
        healthPowerTable.add(powerImg).size(topPanelHeight / 2).spaceRight(spacing);

        Stack powerProgressGroup = new Stack();
        powerProgressGroup.setName("power_group");
        PowerBar = CreateProgress("power", topPanelHeight / 2, Color.SKY);
        powerProgressGroup.addActor(PowerBar);

        PowerLabel = new Label("0%", App.GetSkin(), Owner.LocalizedFontName, Color.WHITE);
        PowerLabel.setAlignment(Align.center);
        powerProgressGroup.addActor(PowerLabel);
        healthPowerTable.add(powerProgressGroup).width(healthColumnWidth);

        table.add(healthPowerTable);

        // Add lives column
        Table livesTable = new Table();
        LivesLabel = new Label("x3", App.GetSkin(), Owner.LocalizedFontName, Color.WHITE);
        LivesLabel.setFontScale(2f);
        livesTable.add(LivesLabel).padRight(4).padLeft(4);
        livesTable.row();
        livesTable.add().height(topPanelHeight * .7f);

        table.add(livesTable).spaceRight(spacing * 4);

        // Score
        Image scoreImg = new Image(Atlas.findRegion("coin"));
        table.add(scoreImg).size(topPanelHeight / 2).spaceRight(spacing);

        HorizontalGroup scoreGroup = new HorizontalGroup();
        scoreGroup.setName("score_group");

        for (int i = 0; i < 10; ++i)
        {
            DrawableNumbers.add(new TextureRegionDrawable(Atlas.findRegion("number_" + i)));
        }

        for (int i = 0; i < NumberOfDigits; ++i)
        {
            Image img = new Image(DrawableNumbers.get(0));

            scoreGroup.addActor(img);

            NumberImages.add(img);
        }

        table.add(scoreGroup).height(topPanelHeight);

        table.add().expandX().fillX(); // expander

        Button pauseButton = BaseScreen.CreateImageButton(Atlas.findRegion("button_pause"), Atlas.findRegion("button_pause_pressed"), new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Owner.SetPaused(!App.IsPaused());
            }
        });
        pauseButton.getColor().a = 0.5f;
        pauseButton.setName("pause_button");
        table.add(pauseButton).size(BaseScreen.ButtonSize);

        // central expander
        table.row();
        table.add().colspan(6).grow();

        // Create panel for player controls
        float controlsPanelHeight = Globals.GUI_HEIGHT * Globals.GAMEPLAY_BUTTONS_SIZE;

        table.row().height(controlsPanelHeight);
        ControlPanel = new PlayerControlPanel(Owner, settings, controlsPanelHeight);
        table.add(ControlPanel).fillX().colspan(6); // resize controls panel to fill width
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        ControlPanel.dispose();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private ProgressBar CreateProgress(String name, float height, Color color)
    {
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        boolean vertical = false;

        float min = 0;
        float max = 100;;
        float step_size = 1;

        TextureRegion bg_texture = Atlas.findRegion("progress_bg");
        TextureRegion fg_texture = Atlas.findRegion("progress_fg");

        int sizeBg = 10;
        NinePatch bgNP = new NinePatch(bg_texture, sizeBg, sizeBg, sizeBg, sizeBg);
        style.background = new NinePatchDrawable(bgNP);
        style.background.setMinHeight(height);
        //style.background.setMinWidth(w);
        style.background.setTopHeight(4);
        style.background.setBottomHeight(4);
        style.background.setLeftWidth(3);
        style.background.setRightWidth(3);

        int sizeFg = 8;
        NinePatch fgNP = new NinePatch(fg_texture, sizeFg, sizeFg, sizeFg, sizeFg);
        fgNP.setColor(color);
        style.knobBefore = new NinePatchDrawable(fgNP);
        style.knobBefore.setMinHeight(height * 0.8f);
        //style.knobBefore.setMinWidth(w);

        style.knob = null;

        ProgressBar bar = new ProgressBar(min, max, step_size, vertical, style);
        bar.setName(name);
        return bar;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private final Color StartColor = new Color(Color.RED);
    private final Color MiddleColor = new Color(Color.YELLOW);
    private final Color FinishColor = new Color(Color.LIME);
    private Color HealthProgressColor = new Color();
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetHealth(int health)
    {
        HealthLabel.setText(health + "%");
        HealthBar.setValue(health);

        float k = health / 100f;

        if (k <= 0.5f)
        {
            float realK = k * 2f;
            HealthProgressColor.set(MathUtils.lerp(StartColor.r, MiddleColor.r, realK), MathUtils.lerp(StartColor.g, MiddleColor.g, realK), MathUtils.lerp(StartColor.b, MiddleColor.b, realK), 1f);
        }
        else
        {
            float realK = (k - 0.5f) * 2f;
            HealthProgressColor.set(MathUtils.lerp(MiddleColor.r, FinishColor.r, realK), MathUtils.lerp(MiddleColor.g, FinishColor.g, realK), MathUtils.lerp(MiddleColor.b, FinishColor.b, realK), 1f);
        }

        ((NinePatchDrawable)(HealthBar.getStyle().knobBefore)).getPatch().setColor(HealthProgressColor);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetLives(int lives)
    {
        LivesLabel.setText("x" + lives);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetPower(int power)
    {
        PowerLabel.setText(power + "%");
        PowerBar.setValue(power);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private enum ScoreState
    {
        STATE_IDLE,
        STATE_CHANGING
    }

    private ScoreState State = ScoreState.STATE_IDLE;
    private final float ScoreChangeTime = 1.0f;
    private float Time = 0f;
    private int TargetScore;
    private int LastScore;
    //-------------------------------------------------------------------------------------------------------------------------

    private void SetCurrentScore(int score)
    {
        final String formatStr = "%0" + NumberOfDigits + "d";
        final String scoreStr = String.format(formatStr, score);

        for (int i = 0; i < NumberOfDigits; ++i)
        {
            int scoreCharIdx = scoreStr.length() - 1 - i;
            if (scoreCharIdx < 0)
            {
                NumberImages.get(NumberOfDigits - 1 - i).setDrawable(DrawableNumbers.get(0));
            }
            else {
                int digit = Character.getNumericValue(scoreStr.charAt(NumberOfDigits - 1 - i));
                NumberImages.get(NumberOfDigits - 1 - i).setDrawable(DrawableNumbers.get(digit));
            }
        }

        LastScore = score;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetScore(int score)
    {
        //ScoreLabel.setText(String.valueOf(score));

        TargetScore = score;
        Time = ScoreChangeTime;
        State = ScoreState.STATE_CHANGING;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void act(float delta)
    {
        super.act(delta);

        if (State == ScoreState.STATE_CHANGING)
        {
            Time -= delta;

            if (Time <= 0f)
            {
                State = ScoreState.STATE_IDLE;
                SetCurrentScore(TargetScore);
                Time = 0f;
            }
            else
            {
                float k = 1f - Time / ScoreChangeTime;
                int currentScore = MathUtils.ceil(MathUtils.lerp((float)LastScore, (float)TargetScore, k));
                SetCurrentScore(currentScore);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}