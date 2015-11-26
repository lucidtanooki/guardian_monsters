package org.limbusdev.monsterworld.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.graphics.EntitySprite;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 21.11.15.
 */
public class OrthogonalTiledMapAndEntityRenderer extends OrthogonalTiledMapRenderer {
    /* ............................................................................ ATTRIBUTES .. */
    private OutdoorGameArea gameArea;
    private Array<EntitySprite> sprites;

    /* ........................................................................... CONSTRUCTOR .. */
    public OrthogonalTiledMapAndEntityRenderer(
            TiledMap map, OutdoorGameArea gameArea) {
        super(map, 1);
        this.gameArea = gameArea;
        this.sprites = new Array<EntitySprite>();
    }
    /* ............................................................................... METHODS .. */
    @Override
    public void render() {
        beginRender();
        int currentLayer = 0;
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                renderTileLayer((TiledMapTileLayer) layer);
                currentLayer++;
                if(layer.getName().equals("objects1")) {
                    // Draw entity sprites if visible
                    for(EntitySprite es : sprites)
                        if(es.visible)
                            es.draw(this.batch);
                }
            } else
                for(MapObject object : layer.getObjects())
                    renderObject(object);
        }
        endRender();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public void addEntitySprite(EntitySprite es) {
        sprites.add(es);
    }
}
