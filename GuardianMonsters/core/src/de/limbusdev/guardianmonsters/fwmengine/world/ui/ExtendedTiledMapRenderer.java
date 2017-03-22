package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
public class ExtendedTiledMapRenderer extends OrthogonalTiledMapRenderer {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<AnimatedPersonSprite> sprites;
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
    public ExtendedTiledMapRenderer(TiledMap map) {
        super(map, 1);

        this.sprites = new Array<>();

        this.objectAnimations = new ArrayMap<>();
        this.tileAnimations = new ArrayMap<>();

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

                if(layer.getName().contains("people")) {
                    // Draw entity sprites if visible
                    for(AnimatedPersonSprite es : sprites) {
                        if (es.visible) {
                            es.update(elapsedTime);
                            es.draw(this.batch);
                        }
                    }
                }

            }
        }

        // Render Weather Effects
        weatherAnimator.render(batch, elapsedTime);


        endRender();
    }

    @Override
    public void renderObject(MapObject object) {

        String objName = object.getName();
        // Don't render objects with empty nameID
        if(objName != null && objName.equals("animatedObject")) {
            renderAnimatedObject(object);
        }

        String objType = object.getProperties().get("type", String.class);
        if(objType != null && objType.equals("animatedTile")) {
            renderAnimatedTile(object);
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
    public void addEntitySprite(AnimatedPersonSprite as) {
        sprites.add(as);
    }

    /**
     * Handles layers containing "animatedTiles" in their nameID and loads the needed animations
     * @param mapLayer  animatedTiles[number] layer
     */
    public void setUpTileAnimations(MapLayer mapLayer) {
        try {
            System.out.println("Setting up tile animation for layer: " + mapLayer.getName());
            for (MapObject mo : mapLayer.getObjects()) {
                String index = mo.getProperties().get("index", String.class);
                int id = Integer.parseInt(index);
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
            e.printStackTrace();
        }
    }

    /**
     * Handles layers containing "animatedObjects" in their nameID and loads the needed animations
     * @param mapLayer  animatedObjects[number] layer
     */
    public void setUpObjectAnimations(MapLayer mapLayer) {
        try {
            System.out.println("Setting up object animation for layer: " + mapLayer.getName());
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


    // .............................................................................. RENDER METHODS
    private void renderAnimatedTile(MapObject o) {
        RectangleMapObject r = (RectangleMapObject) o;
        int index = Integer.parseInt(o.getProperties().get("index", String.class));
        Animation a = tileAnimations.get(index);

        // Render multiple tiles
        int cols, rows;
        int objWidth, objHeight;
        objWidth = MathUtils.round(r.getRectangle().getWidth());
        objHeight = MathUtils.round(r.getRectangle().getHeight());
        cols = objWidth/16;
        rows = objHeight/16;

        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                float x = r.getRectangle().getX() + j*16;
                float y = r.getRectangle().getY() + i*16;
                renderAnimation(a,x,y);
            }
        }
    }

    private void renderAnimatedObject(MapObject o) {
        RectangleMapObject r = (RectangleMapObject) o;
        Animation a = objectAnimations.get(o.getProperties().get("index", String.class));
        renderAnimation(a,r.getRectangle().getX(), r.getRectangle().getY());
    }

    /**
     * Draws the given animation at the position given by the rectangles corner
     * @param anim
     * @param x
     * @param y
     */
    private void renderAnimation(Animation<TextureRegion> anim, float x, float y) {
        try {
            this.batch.draw(anim.getKeyFrame(elapsedTime), x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts sprites, so people in the background are drawn behind those in the front
     */
    private void sortSpritesByDepth() {
        Sort.instance().sort(sprites,new SpriteZComparator());
    }
}
