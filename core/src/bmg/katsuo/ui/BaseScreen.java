package bmg.katsuo.ui;

import bmg.katsuo.localization.Localization;
import bmg.katsuo.managers.ResourceManager;
import bmg.katsuo.utils.TextureUtils;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import bmg.katsuo.Globals;
import bmg.katsuo.IApplication;

import java.util.HashMap;
import java.util.Map;

public class BaseScreen implements Screen
{
    int SPACING;

    protected IApplication App;
    protected Viewport UIViewport;
    Stage ScreenStage;
    Table ScreenTable;

    Table BackgroundWnd;
    Stack BackgroundStack;
    Table ButtonsTable;
    protected Texture SeparatorTexture;
    protected Texture TransparentTexture;

    //private Color BackgroundColor = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    private Color BackgroundColor = Color.BLACK;
    float ButtonWidth = Globals.GUI_WIDTH * 0.2f;

    TextureAtlas UIAtlas;

    protected static final float TopBackgroundOffset = 30;
    protected static final float BottomBackgroundOffset = 50;
    protected static final float ButtonsSpacing = 50;
    protected static final float ButtonSize = 100;

    private Image BackgroundImage;

    protected final static int BG_NONE = 0x00;
    protected final static int BG_WINDOW = 0x01;
    protected final static int BG_IMAGE = 0x02;

    protected BitmapFont LocalizedFont;
    protected String LocalizedFontName;

    protected Label TitleLabel;
    protected String Title;

    protected Map<String, String> Properties = new HashMap<String, String>();

    //----------------------------------------------------------------------------------------------

    BaseScreen(IApplication app) {
        this(app, BG_WINDOW | BG_IMAGE, "");
    }
    //----------------------------------------------------------------------------------------------

    BaseScreen(IApplication app, int option)
    {
        this(app, option, "");
    }
    //----------------------------------------------------------------------------------------------

    BaseScreen(IApplication app, String title) {
        this(app, BG_WINDOW | BG_IMAGE, title);
    }
    //----------------------------------------------------------------------------------------------

    BaseScreen(IApplication app, int option, String title) {
        SPACING = (int) (Globals.GUI_HEIGHT * 0.01f);

        App = app;

        UIAtlas = App.GetResources().GetAtlas(ResourceManager.UI_ATLAS);
        if (UIAtlas == null) {
            App.Error("UI", "Failed to get UI atlas");
        }

        SeparatorTexture = TextureUtils.CreateColoredTexture(Color.BROWN);
        TransparentTexture = TextureUtils.CreateColoredTexture(new Color(0, 0, 0, 0.0f));

        UIViewport = new ExtendViewport(Globals.GUI_WIDTH, Globals.GUI_HEIGHT);
        ScreenStage = new Stage(UIViewport, App.GetRenderer().GetBatch());

        ScreenTable = new Table(App.GetSkin());

        Localization locale = App.GetLocale();
        LocalizedFontName = "font_" + locale.GetCurrentLanguage();
        LocalizedFont = locale.GetCurrentFont();

        if ((option & BG_IMAGE) == BG_IMAGE) {
            //BackgroundImage = new Image(App.GetCommonResources().GetTexture("menu_bg"));
            //BackgroundImage.setWidth(Globals.GUI_WIDTH);
            //BackgroundImage.setHeight(Globals.GUI_HEIGHT);
            //ScreenStage.addActor(BackgroundImage);
        }

        if ((option & BG_WINDOW) == BG_WINDOW) {
            TextureRegion bgReg = UIAtlas.findRegion("gui_frame_2");
            NinePatchDrawable bgDr = new NinePatchDrawable(new NinePatch(bgReg, 15, 15, 15, 15));
            ScreenTable.setBackground(bgDr);
            //ScreenTable.setBackground(new TextureRegionDrawable(TransparentTexture));


            // Create stack with main screen table, title and buttons over it
            BackgroundStack = new Stack();
            BackgroundStack.setSize(Globals.GUI_WIDTH * 0.9f, Globals.GUI_HEIGHT * 0.9f);
            BackgroundStack.add(ScreenTable);

            CreateTitle(title);

            ButtonsTable = new Table(App.GetSkin());
            ButtonsTable.add().fillY().expandY(); // expander
            ButtonsTable.row();
            BackgroundStack.add(ButtonsTable);
            //-------------

            BackgroundWnd = new Table(App.GetSkin());
            BackgroundWnd.setFillParent(true);
            BackgroundWnd.setTouchable(Touchable.enabled);
            BackgroundWnd.add(BackgroundStack).padTop(TopBackgroundOffset).padBottom(BottomBackgroundOffset)
                    .width(Globals.GUI_WIDTH * 0.9f).height(Globals.GUI_HEIGHT * 0.9f);

            ScreenStage.addActor(BackgroundWnd);
        }
        else
        {
            ScreenStage.addActor(ScreenTable);
            ScreenTable.setFillParent(true);
        }

        ScreenTable.padTop(TopBackgroundOffset / 2 + 20); // content top border
        ScreenTable.padBottom(BottomBackgroundOffset / 2 + 20); // content bottom border
    }
    //----------------------------------------------------------------------------------------------

    protected void Localize()
    {
        Localization locale = App.GetLocale();
        LocalizedFontName = "font_" + locale.GetCurrentLanguage();
        LocalizedFont = locale.GetCurrentFont();

        // Some screens has no title (RenderScreen)
        if (TitleLabel != null) {
            TitleLabel.getStyle().font = LocalizedFont;
            TitleLabel.setText(App.GetLocalizedString(Title, ""));
        }
    }
    //----------------------------------------------------------------------------------------------

    private void CreateTitle(String title) {
        boolean useWindowTile = false;

        Title = title;
        if (useWindowTile && BackgroundWnd instanceof Window) {
            TitleLabel = ((Window) BackgroundWnd).getTitleLabel();
            TitleLabel.setText(App.GetLocalizedString(Title, ""));
            TitleLabel.getStyle().font = LocalizedFont;
            TitleLabel.getStyle().fontColor = Color.YELLOW;
        } else {
            TitleLabel = new Label(App.GetLocalizedString(Title, ""), App.GetSkin(), LocalizedFontName, Color.GREEN);
        }

        TitleLabel.setFontScale(3.0f);
        TitleLabel.setAlignment(Align.center, Align.top);

        TextureRegion titleReg = UIAtlas.findRegion("bg");
        NinePatchDrawable titleDr = new NinePatchDrawable(new NinePatch(titleReg, 7, 7, 7, 7));
        TitleLabel.getStyle().background = titleDr;

        if (!useWindowTile) {
            Table titleTable = new Table();
            titleTable.add(TitleLabel).top().padTop(-TopBackgroundOffset);

            titleTable.row();
            titleTable.add().fillY().expandY(); // expander

            BackgroundStack.add(titleTable);
        }
    }
    //----------------------------------------------------------------------------------------------

    public IApplication GetApp() {
        return App;
    }
    //----------------------------------------------------------------------------------------------

    public Stage GetScreenStage()
    {
        return ScreenStage;
    }
    //----------------------------------------------------------------------------------------------

    public void SetBackgroundColor(Color backgroundColor) {
        BackgroundColor = backgroundColor;
    }
    //----------------------------------------------------------------------------------------------

    public void SetProperties(Map<String, String> properties)
    {
        Properties = properties;
    }
    //----------------------------------------------------------------------------------------------

    protected void ResizeScreen(int width, int height) {
        UIViewport.update(width, height, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void resize(int width, int height) {
        ResizeScreen(width, height);
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void show()
    {
        Localize();

        if (!App.GetInput().getProcessors().contains(ScreenStage, true))
        {
            App.GetInput().addProcessor(0, ScreenStage);
        }

        //App.Debug("", "Show screen " + getClass().getSimpleName());
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void hide()
    {
        if (App.GetInput().getProcessors().contains(ScreenStage, true))
        {
            App.GetInput().removeProcessor(ScreenStage);
        }

        //App.Debug("", "Hiding screen " + getClass().getSimpleName());
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void pause() {
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void resume() {
    }
    //----------------------------------------------------------------------------------------------

    public void Update(float delta) {
        ScreenStage.act(delta);
    }
    //----------------------------------------------------------------------------------------------

    protected void Render()
    {
        if (!App.GetRenderOptions().UI)
        {
            return;
        }

        ScreenStage.setDebugAll(App.GetRenderOptions().UIDebug);

        //if (BackgroundWnd != null) {
        //    BackgroundWnd.setDebug(true, true);
        //}
        //ScreenTable.setDebug(true, true);

        ScreenStage.draw();
    }
  //----------------------------------------------------------------------------------------------

    @Override
    public final void render(float delta)
    {
        Update(delta);

        Render();
/*
        Renderer renderer = TheGame.GetRenderer();
        renderer.BeginRender(ScreenStage.getViewport().getCamera().combined);

        //Calendar.getInstance().get(Calendar.YEAR)
        final String mark = String.format("v. 1.0.%d", TheGame.getSettings().getLaunchNumber());
        renderer.RenderText(mark, GUI_WIDTH * 0.85f, Font.getLineHeight(), Color.WHITE, Font);

        renderer.EndRender();*/
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void dispose()
    {
        SeparatorTexture.dispose();
        TransparentTexture.dispose();
        //ClickSound.dispose();
        ScreenStage.dispose();

    }
    //----------------------------------------------------------------------------------------------

    protected class ButtonClickHandler
    {
        public void onButtonClicked() {};
    };
    //----------------------------------------------------------------------------------------------

    protected TextButton CreateButton(String title, final ButtonClickHandler handler)
    {
        final TextButton result = new TextButton(title, App.GetSkin(), "default");
        result.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                //ClickSound.play(0.4f);
                App.PlaySound("click", false);
                handler.onButtonClicked();
            }
        });

        return result;
    }
    //----------------------------------------------------------------------------------------------

    protected void SetButtonHandler(TextButton button, final ButtonClickHandler handler)
    {
        button.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                //ClickSound.play(0.4f);
                App.PlaySound("click", false);
                handler.onButtonClicked();
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    ImageButton CreateImageButton(int id, TextureRegion up, TextureRegion dwn, final ButtonClickHandler handler)
    {
        final ImageButton result = new ImageButton(new TextureRegionDrawable(up), new TextureRegionDrawable(dwn));
        result.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                App.PlaySound("click", false);
                handler.onButtonClicked();
            }
        });

        return result;
    }
    //----------------------------------------------------------------------------------------------

    Button CreateImageButton(TextureRegion up, TextureRegion dwn, final ButtonClickHandler handler)
    {
        final Button result = new Button(new TextureRegionDrawable(up), new TextureRegionDrawable(dwn));
        result.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                App.PlaySound("click", false);
                handler.onButtonClicked();
            }
        });

        return result;
    }
    //----------------------------------------------------------------------------------------------

    public static ImageButton CreateImageButton(int id, TextureRegion up, TextureRegion dwn, final ClickListener listener)
    {
        final ImageButton result = new ImageButton(new TextureRegionDrawable(up), new TextureRegionDrawable(dwn));
        result.addListener(listener);

        return result;
    }

    public static Button CreateImageButton(TextureRegion up, TextureRegion dwn, final ClickListener listener)
    {
        final Button result = new Button(new TextureRegionDrawable(up), new TextureRegionDrawable(dwn));
        result.addListener(listener);

        return result;
    }
    //----------------------------------------------------------------------------------------------

    public TextureAtlas GetAtlas()
    {
        return UIAtlas;
    }
    //----------------------------------------------------------------------------------------------
}
