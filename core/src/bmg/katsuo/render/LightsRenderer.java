package bmg.katsuo.render;

import bmg.katsuo.IApplication;
import bmg.katsuo.controllers.LightController;
import bmg.katsuo.gameplay.objects.LightObject;
import bmg.katsuo.managers.ResourceManager;
import bmg.katsuo.objects.GameLayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class LightsRenderer
{
    private IApplication App;

    private FrameBuffer LightsBuffer;
    private TextureRegion LightsBufferRegion;

    private int LightsResWidth;
    private int LightsResHeight;
    private Sprite PointLightSprite;
    private Sprite ConeLightSprite;

    private Matrix4 Identity = new Matrix4().idt();
    private Matrix4 LightMatrix = new Matrix4();
    //-------------------------------------------------------------------------------------------------------------------------

    public LightsRenderer(IApplication app)
    {
        App = app;

        TextureAtlas atlas = App.GetCommonResources().GetAtlas(ResourceManager.GAME_ATLAS);
        PointLightSprite = new Sprite(atlas.findRegion("light"));
        ConeLightSprite = new Sprite(atlas.findRegion("light-cone"));
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Render(Camera camera, List<LightObject> lights, float wHeight)
    {
        LightMatrix.set(camera.combined);

        GameLayer.LayerCamera cam = App.GetState().GetPlaygroundLayer().GetCamera();
        final Vector2 layerCamPos = cam.GetOriginalPosition();

        LightMatrix.translate(-layerCamPos.x, layerCamPos.y, 0f);

        Renderer renderer = App.GetRenderer();

        LightsBuffer.begin();

        renderer.GetBatch().enableBlending();
        renderer.GetBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        Gdx.gl.glClearColor(0f,0f,0f,1); // Ambient
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.BeginRender(LightMatrix);

        for (LightObject light : lights)
        {
            LightController lightCtrl = light.GetLightController();

            if (!lightCtrl.IsEnabled())
            {
                continue;
            }

            final Vector2 lightPos = lightCtrl.GetPosition();
            final float lightRadius = lightCtrl.GetDistance();

            Sprite sprite = PointLightSprite;

            if (lightCtrl.GetLightType() == LightController.LightType.PointLight)
            {
                sprite = PointLightSprite;
                float lx = lightPos.x - lightRadius;
                float ly = lightPos.y + lightRadius;

                sprite.setPosition(lx, wHeight - ly);
                sprite.setSize(lightRadius * 2f, lightRadius * 2f);
            }
            else if (lightCtrl.GetLightType() == LightController.LightType.ConeLight)
            {
                sprite = ConeLightSprite;

                float width = 2f * lightRadius * MathUtils.sin(lightCtrl.GetConeDegrees() * .5f * MathUtils.degreesToRadians);
                float height = lightRadius;

                float lx = lightPos.x - width * .5f;
                float ly = lightPos.y + lightRadius;

                sprite.setPosition(lx, wHeight - ly);
                sprite.setOrigin(width * .5f, height);
                sprite.setSize(width, height);
                // In cone light sprite the beam looks down, that corresponds of 90 degrees rotation
                sprite.setRotation(-(lightCtrl.GetDirection() + light.GetTrueRotation() - 90));
            }

            final Color lightColor = lightCtrl.GetColor();
            sprite.setColor(lightColor);

            sprite.draw(renderer.GetBatch());
        }

        renderer.EndRender();

        LightsBuffer.end();

        renderer.GetBatch().setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_COLOR);
        renderer.BeginRender(Identity);
        renderer.GetBatch().draw(LightsBufferRegion, -1, -1, 2, 2);
        renderer.EndRender();

        renderer.GetBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Resize(int width, int height)
    {
        if (LightsBuffer != null)
        {
            LightsBuffer.dispose();
        }

        LightsResWidth = width / 2;
        LightsResHeight = height / 2;

        LightsBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, LightsResWidth, LightsResHeight, false);
        LightsBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        LightsBufferRegion = new TextureRegion(LightsBuffer.getColorBufferTexture(),0,0/*lightBuffer.getHeight() - lowDisplayH*/, LightsResWidth, LightsResHeight);
        LightsBufferRegion.flip(false, false);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
