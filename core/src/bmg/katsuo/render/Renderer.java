package bmg.katsuo.render;

import bmg.katsuo.Globals;
import bmg.katsuo.localization.Localization;
import bmg.katsuo.managers.ResourceManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class Renderer implements ResourceManager.ResourceConsumer
{
    private ResourceManager Resources;
    private SpriteBatch Batch;
    private ShapeRenderer Shaper = new ShapeRenderer();
    private BitmapFont Font;
    private Color DefaultFontColor = new Color(0.2f, 1f, 0.2f, 0.9f);

    private static ShaderProgram DEFAULT_SHADER;

    public static ShaderProgram GetDefaultShader()
    {
        if (DEFAULT_SHADER == null)
            DEFAULT_SHADER = SpriteBatch.createDefaultShader();
        return DEFAULT_SHADER;
    }

    public Renderer(ResourceManager resources, Localization locale)
    {
        Resources = resources;
        Batch = new SpriteBatch();

        Font = locale.GetFont("en", Globals.GAMEPLAY_FONT_SIZE);
        //Font = locale.CreateFont("ru", (int)(Globals.SCREEN_HEIGHT * 0.035f * Gdx.graphics.getDensity()));

        Resources.AddConsumer(this);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        Batch.dispose();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetFontHeight()
    {
        return Font.getLineHeight();
    }
    //-------------------------------------------------------------------------------------------------------------------------
/*
    public RenObject CreateBox(int w, int h, String textureName)
    {
        return new RenObject(w, h, textureName, Resources.GetTexture(textureName));
    }
    //-------------------------------------------------------------------------------------------------------------------------
*/
    @Override
    public void ResourcesLoaded(ResourceManager manager)
    {
        /*for (IRenObject object : Objects)
        {
            object.ResourcesLoaded(manager);
        }*/
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void BeginRender(Matrix4 transform, Matrix4 proj)
    {
        Batch.setTransformMatrix(transform);
        Batch.setProjectionMatrix(proj);
        Batch.begin();
    }

    public void BeginRender(Matrix4 proj)
    {
        Batch.setProjectionMatrix(proj);
        Batch.begin();
    }

    public void EndRender()
    {
        Batch.end();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void BeginShapes(Matrix4 proj, ShapeRenderer.ShapeType type, Color color)
    {
        Shaper.setProjectionMatrix(proj);
        Shaper.begin(type);
        Shaper.setColor(color);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void EndShapes()
    {
        Shaper.end();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RenderText(String text, float x, float y)
    {
        Font.setColor(DefaultFontColor);
        Font.draw(Batch, text, x, y);
    }

    public void RenderText(String text, float x, float y, Color color)
    {
        Font.setColor(color);
        Font.draw(Batch, text, x, y);
    }

    public void RenderText(String text, float x, float y, Color color, BitmapFont font)
    {
        font.setColor(color);
        font.draw(Batch, text, x, y);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public SpriteBatch GetBatch()
    {
        return Batch;
    }
    //-------------------------------------------------------------------------------------------------------------------------
/*
    public void RenderObjects()
    {
        for (IRenObject obj : Objects)
        {
            obj.Render(this);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void RenderShapes(List<IRenObject> objects, ShapeRenderer.ShapeType type, Color color)
    {
        Shaper.setColor(color);
        Shaper.begin(type);

        for (IRenObject obj : objects)
        {
            obj.RenderDebug(this, type);
        }

        Shaper.end();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RenderDebug()
    {
        RenderShapes(Objects, ShapeRenderer.ShapeType.Line, Globals.DEBUG_COLOR);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public int GetObjectsCount()
    {
        return Objects.size();
    }
    //-------------------------------------------------------------------------------------------------------------------------
*/
    public void DrawLine(float x1, float y1, float x2, float y2)
    {
        Shaper.line(x1, y1, x2, y2);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void DrawLine(float x1, float y1, float x2, float y2, Color color)
    {
        Shaper.line(x1, y1, x2, y2, color, color);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void DrawRect(float x, float y, float w, float h)
    {
        Shaper.rect(x, y, w, h);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void DrawCircle(float x, float y, float r)
    {
        Shaper.circle(x, y, r);
    }

    public void DrawCircle(float x, float y, float r, int segments)
    {
        Shaper.circle(x, y, r, segments);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void DrawCone(float x, float y, float radius, float angleFrom, float angleTo, int segments)
    {
        Shaper.arc(x, y, radius, angleFrom, angleTo - angleFrom, segments);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
