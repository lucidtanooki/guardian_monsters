package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Sort;
import com.sun.media.sound.AiffFileReader;

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.geometry.IdentifiableRectangle;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.geometry.IntRectangle;
import de.limbusdev.guardianmonsters.geometry.IntVector2;


/**
 * Created by georg on 21.11.15.
 */
public class OrthogonalTiledMapAndEntityRenderer extends OrthogonalTiledMapRenderer {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<EntitySprite> sprites;
    private Array<IdentifiableRectangle> animatedTiles;
    private ArrayMap<Integer,TextureRegion> animationTextures;
    private ArrayMap<Integer,TextureRegion> animatedObjectsTextures;
    private ArrayMap<String,Animation> objectAnimations;
    private Media media;
    private float elapsedTime;
    private Array<Vector2> weatherTiles;

    /* ........................................................................... CONSTRUCTOR .. */
    public OrthogonalTiledMapAndEntityRenderer(
            TiledMap map) {
        super(map, 1);
        this.sprites = new Array<EntitySprite>();
        this.animatedTiles = new Array<IdentifiableRectangle>();
        this.animationTextures = new ArrayMap<Integer,TextureRegion>();
        this.animatedObjectsTextures = new ArrayMap<Integer, TextureRegion>();
        this.objectAnimations = new ArrayMap<String,Animation>();
        weatherTiles = new Array<Vector2>();
        int mapWidth = map.getProperties().get("width", Integer.class)*16;
        int mapHeight = map.getProperties().get("height", Integer.class)*16;
        for(int i=-1; i<mapWidth/640+1; i++) {
            for(int j=-1; j<mapHeight/480+1; j++) {
                weatherTiles.add(new Vector2(i*640,j*480));
            }
        }
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

        // Sprite Z-Sorting
        sortSpritesByDepth();


        beginRender();
        int currentLayer = 0;
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                renderTileLayer((TiledMapTileLayer) layer);
                currentLayer++;
                if(layer.getName().equals("objects1")) {

                    if(map.getLayers().get("animatedObjects") != null) {
                        MapLayer animObjLayer = map.getLayers().get("animatedObjects");
                        renderAnimatedObjects(animObjLayer);
                    }

                    // Draw entity sprites if visible
                    for(EntitySprite es : sprites) {
                        if (es.visible) {
                            es.draw(this.batch);
                        }
                    }

                }
                if(layer.getName().equals("ground1")) {
                    renderAnimatedTiles();
                }

            } else {
                for (MapObject object : layer.getObjects())
                    renderObject(object);
            }
        }

        // Render Weather Effects
        drawWeather();


        endRender();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public void addEntitySprite(EntitySprite es) {
        sprites.add(es);
    }

    public void drawWeather() {
        for(Vector2 iv : weatherTiles) {
            iv.x += Gdx.graphics.getDeltaTime()*10.0f;
            if(iv.x >= (map.getProperties().get("width", Integer.class)*16f/640+1)*640) {
                iv.x=-640f;
            }
            batch.draw(media.getTexture(TextureAssets.weatherTexture1),iv.x,iv.y);
        }
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
                String id = mo.getProperties().get("index", String.class);
                Animation a = media.getObjectAnimation(id);
                objectAnimations.put(id,a);
            }
        } catch (Exception e) {
            System.err.println("No Animated Objects Layer available");
            e.printStackTrace();
        }
    }

    private void renderAnimatedTiles() {
            for(IdentifiableRectangle ir : animatedTiles) {
                this.batch.draw(animationTextures.get(ir.ID),
                        ir.x, ir.y);
            }
    }

    private void renderAnimatedObjects(MapLayer mapLayer) {
        for(MapObject o : mapLayer.getObjects()) {
            RectangleMapObject r = (RectangleMapObject) o;
            this.batch.draw(
                objectAnimations.get(o.getProperties().get("index", String.class)).getKeyFrame(elapsedTime),
                r.getRectangle().getX(),
                r.getRectangle().getY());
        }
    }

    /**
     * Sorts sprites, so people in the background are drawn behind thoses in the front
     */
    private void sortSpritesByDepth() {
        Sort.instance().sort(sprites,new SpriteZComparator());
    }
}
