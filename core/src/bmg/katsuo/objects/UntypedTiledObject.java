package bmg.katsuo.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.scenes.scene2d.Actor;

class UntypedTiledObject extends Actor
{
    private Sprite TheSprite;

    public UntypedTiledObject(String name, TiledMapTileMapObject mapObject) throws Exception
    {
        setName(name);

        TiledMapTile tile = mapObject.getTile();
        if (tile == null)
        {
            throw new Exception("Failed to create UntypedTiledObject: tile is null");
        }

        final MapProperties props = mapObject.getProperties();

        final TextureRegion reg = tile.getTextureRegion();
        TheSprite = new Sprite(reg);

        float x = props.get("x", 0.0f, Float.class);
        float y = props.get("y", 0.0f, Float.class);
        float w = props.get("width", 16f, Float.class);
        float h = props.get("height", 16f, Float.class);
        float r = props.get("rotation", 0f, Float.class);
        float op = props.get("opacity", 1f, Float.class);

        TheSprite.setPosition(x, y);

        TheSprite.setAlpha(op);
        TheSprite.setOrigin(0f, 0f);
        TheSprite.setRotation(-r);
        TheSprite.setColor(Color.WHITE);
        TheSprite.setSize(w, h);
        TheSprite.setScale(getScaleX(), getScaleY());
        TheSprite.setFlip(false, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        TheSprite.draw(batch, parentAlpha);
    }

    static Actor Create(String name, MapObject mapObject) throws Exception
    {
        if ((mapObject == null) || !(mapObject instanceof TiledMapTileMapObject))
        {
            return null;
        }

        return new UntypedTiledObject(name, (TiledMapTileMapObject) mapObject);
    }
}

