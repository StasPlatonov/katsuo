package bmg.katsuo.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureUtils
{
    public static Texture CreateColoredTexture(Color color)
    {
        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(color);
        overlayPixmap.fill();

        Texture result = new Texture(overlayPixmap);

        overlayPixmap.dispose();
        return result;
    }
}
