package bmg.katsuo.render;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

import bmg.katsuo.objects.GameLayer;
import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;

public class TileLayerRenderer
{
    static protected final int NUM_VERTICES = 20;
    private float UnitScale;
    private Rectangle ViewBounds;
    private float Vertices[] = new float[NUM_VERTICES];
    //-------------------------------------------------------------------------------------------------------------------------

    public TileLayerRenderer()
    {
        ViewBounds = new Rectangle();
        UnitScale = 1.0f;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void UpdateAnimations()
    {
        AnimatedTiledMapTile.updateAnimationBaseTime();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RenderTileLayer(GameObject object, TiledMapTileLayer layer, Batch batch)
    {
        GameLayer.LayerCamera cam = object.GetLayer().GetCamera();

        ViewBounds = ViewBounds.set(cam.GetViewBounds());
        ViewBounds.x -= layer.getTileWidth();
        ViewBounds.width += layer.getTileWidth() * 2;
        ViewBounds.y -= layer.getTileHeight();
        ViewBounds.height += layer.getTileHeight() * 2;

        UnitScale = (object.getScaleX() + object.getScaleY()) * .5f;
        final Color batchColor = batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b,
                batchColor.a * layer.getOpacity());

        final int layer_width = layer.getWidth();
        final int layer_height = layer.getHeight();

        final float layer_tilewidth = layer.getTileWidth() * UnitScale;
        final float layer_tileheight = layer.getTileHeight() * UnitScale;

        final float offset_x = 0;//object.getX() * UnitScale;
        final float offset_y = 0;//object.getY() * UnitScale;

        final int col1 = Math.max(0, (int) ((ViewBounds.x - offset_x) / layer_tilewidth));
        final int col2 = Math.min(layer_width,
                (int) ((ViewBounds.x + ViewBounds.width + layer_tilewidth - offset_x) / layer_tilewidth));

        final int row1 = Math.max(0, (int) ((ViewBounds.y - offset_y) / layer_tileheight));
        final int row2 = Math.min(layer_height,
                (int) ((ViewBounds.y + ViewBounds.height + layer_tileheight - offset_y) / layer_tileheight));

        float y = row2 * layer_tileheight + offset_y;
        float xStart = col1 * layer_tilewidth + offset_x;
        final float[] vertices = this.Vertices;

        for (int row = row2; row >= row1; row--)
        {
            float x = xStart;
            for (int col = col1; col < col2; col++)
            {
                final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null)
                {
                    x += layer_tilewidth;
                    continue;
                }
                final TiledMapTile tile = cell.getTile();
                if (tile != null)
                {
                        final boolean flipX = cell.getFlipHorizontally();
                        final boolean flipY = cell.getFlipVertically();
                        final int rotations = cell.getRotation();

                        TextureRegion region = tile.getTextureRegion();

                        if (region == null)
                        {
                            object.GetState().GetApp().Error("TileLayerREnderer", "Texture region of tile " + tile.getId() + " is null");
                            continue;
                        }

                        float x1 = x + tile.getOffsetX() * UnitScale;
                        float y1 = y + tile.getOffsetY() * UnitScale;
                        float x2 = x1 + region.getRegionWidth() * UnitScale;
                        float y2 = y1 + region.getRegionHeight() * UnitScale;

                        float u1 = region.getU();
                        float v1 = region.getV2();
                        float u2 = region.getU2();
                        float v2 = region.getV();

                        vertices[X1] = x1;
                        vertices[Y1] = y1;
                        vertices[C1] = color;
                        vertices[U1] = u1;
                        vertices[V1] = v1;

                        vertices[X2] = x1;
                        vertices[Y2] = y2;
                        vertices[C2] = color;
                        vertices[U2] = u1;
                        vertices[V2] = v2;

                        vertices[X3] = x2;
                        vertices[Y3] = y2;
                        vertices[C3] = color;
                        vertices[U3] = u2;
                        vertices[V3] = v2;

                        vertices[X4] = x2;
                        vertices[Y4] = y1;
                        vertices[C4] = color;
                        vertices[U4] = u2;
                        vertices[V4] = v1;

                        if (flipX)
                        {
                            float temp = vertices[U1];
                            vertices[U1] = vertices[U3];
                            vertices[U3] = temp;
                            temp = vertices[U2];
                            vertices[U2] = vertices[U4];
                            vertices[U4] = temp;
                        }
                        if (flipY)
                        {
                            float temp = vertices[V1];
                            vertices[V1] = vertices[V3];
                            vertices[V3] = temp;
                            temp = vertices[V2];
                            vertices[V2] = vertices[V4];
                            vertices[V4] = temp;
                        }
                        if (rotations != 0)
                        {
                            switch (rotations)
                            {
                                case Cell.ROTATE_90:
                                {
                                    float tempV = vertices[V1];
                                    vertices[V1] = vertices[V2];
                                    vertices[V2] = vertices[V3];
                                    vertices[V3] = vertices[V4];
                                    vertices[V4] = tempV;

                                    float tempU = vertices[U1];
                                    vertices[U1] = vertices[U2];
                                    vertices[U2] = vertices[U3];
                                    vertices[U3] = vertices[U4];
                                    vertices[U4] = tempU;
                                    break;
                                }
                                case Cell.ROTATE_180:
                                {
                                    float tempU = vertices[U1];
                                    vertices[U1] = vertices[U3];
                                    vertices[U3] = tempU;
                                    tempU = vertices[U2];
                                    vertices[U2] = vertices[U4];
                                    vertices[U4] = tempU;
                                    float tempV = vertices[V1];
                                    vertices[V1] = vertices[V3];
                                    vertices[V3] = tempV;
                                    tempV = vertices[V2];
                                    vertices[V2] = vertices[V4];
                                    vertices[V4] = tempV;
                                    break;
                                }
                                case Cell.ROTATE_270:
                                {
                                    float tempV = vertices[V1];
                                    vertices[V1] = vertices[V4];
                                    vertices[V4] = vertices[V3];
                                    vertices[V3] = vertices[V2];
                                    vertices[V2] = tempV;

                                    float tempU = vertices[U1];
                                    vertices[U1] = vertices[U4];
                                    vertices[U4] = vertices[U3];
                                    vertices[U3] = vertices[U2];
                                    vertices[U2] = tempU;
                                    break;
                                }
                            }
                        }

                        batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
                }
                x += layer_tilewidth;
            }
            y -= layer_tileheight;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
