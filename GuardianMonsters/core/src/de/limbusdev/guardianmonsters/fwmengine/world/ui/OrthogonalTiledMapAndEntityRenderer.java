package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Sort;

import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;


/**
 * Renderer for *.tmx files. This renderer renders map files of the FWM-Engine.
 *
 * Map Structure: Documents/TiledMapStructure.md
 *
 * Copyright 2017 by Georg Eckert
 */
public class OrthogonalTiledMapAndEntityRenderer extends OrthogonalTiledMapRenderer {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<EntitySprite> sprites;
    private ArrayMap<String,Animation> objectAnimations;
    private ArrayMap<Integer,Animation> tileAnimations;
    private Media media;
    private float elapsedTime;
    private WeatherAnimator weatherAnimator;

    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Orthogonal Tiled Map Renderer for tiled maps with entities and other objects. For structure
     * description see Documents/TiledMapStructure.md
     * @param map
     */
    public OrthogonalTiledMapAndEntityRenderer(TiledMap map) {
        super(map, 1);
        this.sprites = new Array<EntitySprite>();

        this.objectAnimations = new ArrayMap<String,Animation>();
        this.tileAnimations = new ArrayMap<Integer, Animation>();

        this.media = Services.getMedia();
        this.elapsedTime = 0;
        this.weatherAnimator = new WeatherAnimator(map);

        for(MapLayer layer : map.getLayers()) {
            if(layer.getName().contains("animatedObjects")) {
                setUpObjectAnimations(layer);
            }
            if(layer.getName().contains("animatedTiles")) {
                setUpTileAnimations(layer);
            }
        }

    }
    /* ............................................................................... METHODS .. */
    @Override
    public void render() {
        // Elapsed Time for Animations
        elapsedTime += Gdx.graphics.getDeltaTime();

        // Sprite Z-Sorting
        sortSpritesByDepth();

        beginRender();
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                // Render graphical layer
                renderTileLayer((TiledMapTileLayer) layer);

            } else {
                // Handle object layer
                for (MapObject object : layer.getObjects()) {
                    renderObject(object);
                }

                if(layer.getName().equals("people")) {
                    // Draw entity sprites if visible
                    for(EntitySprite es : sprites) {
                        if (es.visible) {
                            es.draw(this.batch);
                        }
                    }
                }
            }
        }

        // Render Weather Effects
        weatherAnimator.render(batch);


        endRender();
    }

    @Override
    public void renderObject(MapObject object) {
        String objName = object.getName();

        // Don't render objects with empty name
        if(objName != null && objName.equals("animatedObject")) {
            renderAnimatedObject(object);
        }

        String objType = object.getProperties().get("type", String.class);
        if(objType != null && objType.equals("animatedTile")) {
            renderAnimatedTile(object);
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
    public void addEntitySprite(EntitySprite es) {
        sprites.add(es);
    }

    public void setUpTileAnimations(MapLayer mapLayer) {
        try {
            for (MapObject mo : mapLayer.getObjects()) {
                int id = mo.getProperties().get("index", Integer.class);
                if(!tileAnimations.containsKey(id)) {
                    Animation a = media.getTileAnimation(id);
                    if(mo.getProperties().containsKey("frameDuration")){
                        String frmDur = mo.getProperties().get("frameDuration", String.class);
                        a.setFrameDuration(Float.parseFloat(frmDur));
                    }
                    tileAnimations.put(id,a);
                }
            }
        } catch (Exception e) {
            System.err.println("No Animated Tiles Layer available");
        }
    }

    public void setUpObjectAnimations(MapLayer mapLayer) {
        try {
            for (MapObject mo : mapLayer.getObjects()) {
                String id = mo.getProperties().get("index", String.class);
                if(!objectAnimations.containsKey(id)) {
                    Animation a = media.getObjectAnimation(id);
                    if(mo.getProperties().containsKey("frameDuration")){
                        String frmDur = mo.getProperties().get("frameDuration", String.class);
                        a.setFrameDuration(Float.parseFloat(frmDur));
                    }
                    objectAnimations.put(id,a);
                }
            }
        } catch (Exception e) {
            System.err.println("No Animated Objects Layer available");
        }
    }

    private void renderAnimatedTile(MapObject o) {
        RectangleMapObject r = (RectangleMapObject) o;
        this.batch.draw(
            tileAnimations.get(o.getProperties().get("index", Integer.class)).getKeyFrame(elapsedTime),
            r.getRectangle().getX(),
            r.getRectangle().getY());
    }

    private void renderAnimatedObject(MapObject o) {
        RectangleMapObject r = (RectangleMapObject) o;
        this.batch.draw(
            objectAnimations.get(o.getProperties().get("index", String.class)).getKeyFrame(elapsedTime),
            r.getRectangle().getX(),
            r.getRectangle().getY());
    }

    /**
     * Sorts sprites, so people in the background are drawn behind those in the front
     */
    private void sortSpritesByDepth() {
        Sort.instance().sort(sprites,new SpriteZComparator());
    }
}
