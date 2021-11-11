package bmg.katsuo.ui;

import bmg.katsuo.utils.TextureUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import bmg.katsuo.IApplication;
import bmg.katsuo.managers.ResourceManager;
import bmg.katsuo.render.Renderer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class LoadingScreen extends BaseScreen
{
    OrthographicCamera MainCamera;

    //-------------------------------------------------------------------------------------------------------------------------
    Label loadingLabel;
    Table LogoTable;
    Texture LogoBackgroundTexture;
    Image LogoImage;

    public LoadingScreen(IApplication app)
    {
        super(app);

        MainCamera = new OrthographicCamera();
        MainCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //return;

        LogoBackgroundTexture = TextureUtils.CreateColoredTexture(Color.BLACK);

        LogoTable = new Table();
        LogoTable.setFillParent(true);
        LogoTable.setBackground(new Image(LogoBackgroundTexture).getDrawable());
        LogoTable.setTouchable(Touchable.enabled); // to disable underneath ui

        LogoImage = new Image(App.GetResources().GetTexture(ResourceManager.LOADING_LOGO));
        LogoTable.add(LogoImage);

        LogoTable.row();

        loadingLabel = new Label( "Loading", App.GetSkin(), LocalizedFontName, Color.GOLD);
        loadingLabel.setFontScale(2.0f);
        LogoTable.add(loadingLabel).spaceTop(10f);

        ScreenStage.addActor(LogoTable);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void dispose()
    {
        LogoBackgroundTexture.dispose();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Color BGColor = new Color(0.0f, 0.0f, 0.0f, 1);
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Localize()
    {
        super.Localize();

        loadingLabel.getStyle().font = App.GetLocale().GetCurrentFont();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Render()
    {
        /*
        ResourceManager resources = App.GetResources();
        Gdx.gl.glClearColor(BGColor.r, BGColor.g, BGColor.b, BGColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Renderer render = App.GetRenderer();
        render.BeginRender(MainCamera.combined);

        int px = Gdx.graphics.getWidth() / 2 - 50;
        int py = Gdx.graphics.getHeight() / 2;
        render.RenderText(App.GetLocale().GetLocalizedString("loading", (int)(resources.GetProgress() * 100f), resources.GetLoadedResourceCount(), resources.GetLoadedResourceCount() + resources.GetEnqueuedResourceCount()), px, py, Color.WHITE);

        int px2 = Gdx.graphics.getWidth() / 2 - 50;
        int py2 = Gdx.graphics.getHeight() / 2 - (int)render.GetFontHeight();
        render.RenderText(App.GetLoadingDescription(), px2, py2, Color.GREEN);
        render.EndRender();
        */

        super.Render();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta)
    {
        super.Update(delta);

        ResourceManager resources = App.GetResources();

        loadingLabel.setText(App.GetLocalizedString("loading", (int)(resources.GetProgress() * 100), resources.GetLoadedResourceCount(), resources.GetLoadedResourceCount() + resources.GetEnqueuedResourceCount()));
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void resize(int width, int height)
    {
        MainCamera.setToOrtho(false, width, height);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
