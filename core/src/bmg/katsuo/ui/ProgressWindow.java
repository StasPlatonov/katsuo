package bmg.katsuo.ui;

import bmg.katsuo.Globals;
import bmg.katsuo.IApplication;
import bmg.katsuo.localization.Localization;
import bmg.katsuo.managers.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;

public class ProgressWindow extends Table
{
    private IApplication App;
    private Texture BGTexture = null;
    private Label TextLabel;
    private Image Animage;
    private Animation<AtlasSpriteEx> Anim;
    private float AnimationElapsed;
    private SpriteDrawable AnimDrawable;
    private int AnimSpeed = 1; // animation cycles per second
    private Color ProgressColor = new Color(0.2f, 0.9f, 0.2f, 1.0f);

    public ProgressWindow(IApplication app, Stage stage)
    {
        super(app.GetSkin());

        App = app;

        AnimationElapsed = 0f;

        float frameDuration = (float)AnimSpeed / 8F;
        Anim = App.GetCommonResources().CreateAnimation("progress", ResourceManager.UI_ATLAS, 8, 1, 1, 8, frameDuration, Animation.PlayMode.LOOP);

        for (AtlasSpriteEx sprite : Anim.getKeyFrames())
        {
            sprite.setColor(ProgressColor);
        }
        Anim.setPlayMode(LOOP);

        float wid = Globals.GUI_WIDTH;
        float hei = Globals.GUI_HEIGHT;

        // Nine patch bg
        Texture texture = new Texture(Gdx.files.internal("ui/shade.png"));
        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(texture, 4, 4, 4, 4));

        //Texture texture = new Texture(Gdx.files.internal("ui/gui_frame.png"));
        //NinePatchDrawable background = new NinePatchDrawable(new NinePatch(texture, 15, 15, 15, 15));

        // Single colored bg
        //BGTexture = TextureUtils.CreateColoredTexture(Color.GRAY);
        //Drawable background = new Image(BGTexture).getDrawable();

        setFillParent(true);
        setBackground(background);
        setWidth(wid);
        setHeight(hei);

        //Animage = new Image(AnimFake);
        Animage = new Image();
        add(Animage).width(48).height(48).space(10.0f);
        AnimDrawable = new SpriteDrawable(Anim.getKeyFrame(0f));
        Animage.setDrawable(AnimDrawable);
        //---------

        TextLabel = new Label("", App.GetSkin());
        Label.LabelStyle stl = new Label.LabelStyle(TextLabel.getStyle());
        stl.font = App.GetLocale().GetCurrentFont();
        TextLabel.setFontScale(1.5f);
        TextLabel.setStyle(stl);
        add(TextLabel);
        //---------

        stage.addActor(this);

        Hide(true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Localize()
    {
        final Localization locale = App.GetLocale();
        TextLabel.getStyle().font = locale.GetCurrentFont();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove()
    {
        if (BGTexture != null)
        {
            BGTexture.dispose();
        }

        Anim = null;
        return super.remove();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void act(float delta)
    {
        if ((Anim != null) && (getColor().a > 0F))
        {
            AnimationElapsed += delta * 1.0F;
            AnimDrawable.setSprite(Anim.getKeyFrame(AnimationElapsed));
        }

        super.act(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Show(String text, boolean instant)
    {
        Localize();

        TextLabel.setText(text);

        if (instant)
        {
            getColor().a = 1F;
        }
        else
        {
            getColor().a = 0F;
            addAction(Actions.fadeIn(0.5F));
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Hide(boolean instant)
    {
        if (instant)
        {
            getColor().a = 0F;
        }
        else
        {
            addAction(Actions.fadeOut(0.2F));
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
