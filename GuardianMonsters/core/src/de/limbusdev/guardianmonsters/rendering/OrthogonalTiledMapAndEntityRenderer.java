package de.limbusdev.guardianmonsters.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.geometry.IdentifiableRectangle;
import de.limbusdev.guardianmonsters.graphics.EntitySprite;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;


/**
 * Created by georg on 21.11.15.
 */
public class OrthogonalTiledMapAndEntityRenderer extends OrthogonalTiledMapRenderer {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<EntitySprite> sprites;
    private Array<IdentifiableRectangle> animatedTiles;
    private ArrayMap<Integer,TextureRegion> animationTextures;
    private Array<IdentifiableRectangle> animatedObjectsTiles;
    private ArrayMap<Integer,TextureRegion> animatedObjectsTextures;
    private Media media;
    private float elapsedTime;

    /* ........................................................................... CONSTRUCTOR .. */
    public OrthogonalTiledMapAndEntityRenderer(
            TiledMap map) {
        super(map, 1);
        this.sprites = new Array<EntitySprite>();
        this.animatedTiles = new Array<IdentifiableRectangle>();
        this.animationTextures = new ArrayMap<Integer,TextureRegion>();
        this.animatedObjectsTiles = new Array<IdentifiableRectangle>();
        this.animatedObjectsTextures = new ArrayMap<Integer, TextureRegion>();
        this.media = Services.getMedia();
        this.elapsedTime = 0;
    }
    /* ............................................................................... METHODS .. */
    @Override
    public void render() {
        elapsedTime += Gdx.graphics.getDeltaTime();
        for(Integer i : animationTextures.keys())
            animationTextures.put(i, media.getTileAnimation(i).getKeyFrame(elapsedTime));
        for(Integer i : animatedObjectsTextures.keys())
            animatedObjectsTextures.put(i, media.getTileAnimation(i).getKeyFrame(elapsedTime));


        beginRender();
        int currentLayer = 0;
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                renderTileLayer((TiledMapTileLayer) layer);
                currentLayer++;
                if(layer.getName().equals("objects1")) {
                    renderAnimatedObjects();

                    // Draw entity sprites if visible
                    for(EntitySprite es : sprites)
                        if(es.visible)
                            es.draw(this.batch);

                }
                if(layer.getName().equals("ground1")) {
                    renderAnimatedTiles();
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

    public void setUpAnimations(MapLayer mapLayer) {
        try {
            for (MapObject mo : mapLayer.getObjects()) {
                RectangleMapObject r = (RectangleMapObject) mo;
                IdentifiableRectangle ir = new IdentifiableRectangle(
                        MathUtils.round(r.getRectangle().x),
                        MathUtils.round(r.getRectangle().y),
                        Integer.parseInt(mo.getProperties().get("index", String.class)));
                animatedTiles.add(ir);
                animationTextures.put(ir.ID, media.getTileAnimation(ir.ID).getKeyFrames()[0]);
            }
        } catch (Exception e) {
            System.err.println("No Animations Layer available");
        }
    }

    public void setUpAnimatedObjects(MapLayer mapLayer) {
        try {
            for (MapObject mo : mapLayer.getObjects()) {
                RectangleMapObject r = (RectangleMapObject) mo;
                IdentifiableRectangle ir = new IdentifiableRectangle(
                        MathUtils.round(r.getRectangle().x),
                        MathUtils.round(r.getRectangle().y),
                        Integer.parseInt(mo.getProperties().get("index", String.class)));
                animatedObjectsTiles.add(ir);
                animatedObjectsTextures.put(ir.ID, media.getTileAnimation(ir.ID).getKeyFrames()[0]);
            }
        } catch (Exception e) {
            System.err.println("No Animated Objects Layer available");
        }
    }

    private void renderAnimatedTiles() {
            for(IdentifiableRectangle ir : animatedTiles) {
                this.batch.draw(animationTextures.get(ir.ID),
                        ir.x, ir.y);
            }
    }

    private void renderAnimatedObjects() {
        for(IdentifiableRectangle ir : animatedObjectsTiles) {
            this.batch.draw(animatedObjectsTextures.get(ir.ID),
                    ir.x, ir.y);
        }
    }
}
