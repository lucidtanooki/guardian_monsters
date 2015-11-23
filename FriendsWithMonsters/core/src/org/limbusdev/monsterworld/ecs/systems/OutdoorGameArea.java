package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.enums.MusicType;
import org.limbusdev.monsterworld.geometry.WarpPoint;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.rendering.OrthogonalTiledMapAndEntityRenderer;

/**
 * Contains logic and information about one game world area like a forest or a path. One
 * OutDoorGameArea per Tiled Map.
 *
 * Created by georg on 21.11.15.
 */
public class OutdoorGameArea {
    /* ............................................................................ ATTRIBUTES .. */
    private TiledMap tiledMap;
    private OrthogonalTiledMapAndEntityRenderer mapRenderer;
    private MediaManager media;
    private Music bgMusic;
    private Array<Rectangle> colliders;
    private Array<WarpPoint> warpPoints;
    public PositionComponent startPosition;

    /* ........................................................................... CONSTRUCTOR .. */
    public OutdoorGameArea(int areaID, MediaManager media, int startPosID) {
        this.media = media;
        this.startPosition = new PositionComponent(0,0,0,0);
        this.colliders = new Array<Rectangle>();
        this.warpPoints = new Array<WarpPoint>();
        setUpTiledMap(areaID, startPosID);
        this.mapRenderer = new OrthogonalTiledMapAndEntityRenderer(tiledMap, this);
    }
    /* ............................................................................... METHODS .. */

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void renderDebugging(ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);

        for(Rectangle r : this.colliders) {
            shape.rect(r.x, r.y, r.width, r.height);
        }

        shape.end();
    }

    public void setUpTiledMap(int areaID, int startFieldID) {

        tiledMap = new TmxMapLoader().load("tilemaps/" + areaID + ".tmx");
        // create static bodies from colliders
        Rectangle r;
        for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            colliders.add(r);
        }


        for(MapObject mo : tiledMap.getLayers().get("sensors").getObjects()) {
            if(mo.getName().equals("warpField"))
                warpPoints.add(new WarpPoint(
                        Integer.parseInt(mo.getProperties().get("targetWarpPointID", String.class)),
                        ((RectangleMapObject) mo).getRectangle(),
                        Integer.parseInt(mo.getProperties().get("targetID", String.class)))
                );
            if(mo.getName().equals("startField")) {
                if(Integer.parseInt(mo.getProperties().get("fieldID", String.class))
                        == startFieldID) {
                    Rectangle field = ((RectangleMapObject)mo).getRectangle();
                    startPosition.x = MathUtils.round(field.x);
                    startPosition.y = MathUtils.round(field.y);
                }
            }
        }


        // Set background music
        String musicType = tiledMap.getProperties().get("musicType", String.class);
        if(musicType.equals("town"))
            bgMusic = media.getBGMusic(
                    MusicType.TOWN,
                    Integer.parseInt(tiledMap.getProperties().get("musicIndex", String.class))-1);
        bgMusic.setLooping(true);

    }
    /* ..................................................................... GETTERS & SETTERS .. */

    public OrthogonalTiledMapAndEntityRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void dispose() {
        bgMusic.dispose();
    }

    public void playMusic() {
        this.bgMusic.play();
    }

    public void stopMusic() {
        this.bgMusic.stop();
    }

    public Array<Rectangle> getColliders() {
        return colliders;
    }

    public Array<WarpPoint> getWarpPoints() {
        return warpPoints;
    }
}
