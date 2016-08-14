package de.limbusdev.guardianmonsters.ecs.systems;

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

import de.limbusdev.guardianmonsters.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.enums.MusicType;
import de.limbusdev.guardianmonsters.geometry.IntRectangle;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.geometry.MapObjectInformation;
import de.limbusdev.guardianmonsters.geometry.MapPersonInformation;
import de.limbusdev.guardianmonsters.geometry.WarpPoint;
import de.limbusdev.guardianmonsters.managers.MediaManager;
import de.limbusdev.guardianmonsters.model.MonsterArea;
import de.limbusdev.guardianmonsters.rendering.OrthogonalTiledMapAndEntityRenderer;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Contains logic and information about one game world area like a forest or a path. One
 * OutDoorGameArea per Tiled Map.
 *
 * Created by georg on 21.11.15.
 */
public class GameArea {
    /* ............................................................................ ATTRIBUTES .. */
    private TiledMap tiledMap;
    private OrthogonalTiledMapAndEntityRenderer mapRenderer;
    private MediaManager media;
    private Music bgMusic;
    private Array<IntRectangle> colliders;
    private Array<IntRectangle> movingColliders;
    private Array<WarpPoint> warpPoints;
    private Array<Rectangle> healFields;
    private Array<MapPersonInformation> mapPeople;
    private Array<MapObjectInformation> mapSigns;
    private Array<MonsterArea> monsterAreas;
    public IntVector2 gridPosition;
    public PositionComponent startPosition;
    public int areaID;

    /* ........................................................................... CONSTRUCTOR .. */
    public GameArea(int areaID, MediaManager media, int startPosID) {
        this.media = media;
        this.startPosition = new PositionComponent(0,0,0,0);
        this.colliders = new Array<IntRectangle>();
        this.movingColliders = new Array<IntRectangle>();
        this.monsterAreas = new Array<MonsterArea>();
        this.warpPoints = new Array<WarpPoint>();
        this.mapPeople = new Array<MapPersonInformation>();
        this.mapSigns = new Array<MapObjectInformation>();
        this.healFields = new Array<Rectangle>();
        setUpTiledMap(areaID, startPosID);
        this.mapRenderer = new OrthogonalTiledMapAndEntityRenderer(tiledMap, media);
        this.areaID = areaID;
        this.mapRenderer.setUpAnimations(tiledMap.getLayers().get("animations"));
        this.mapRenderer.setUpAnimatedObjects(tiledMap.getLayers().get("animatedObjects"));
        this.gridPosition = new IntVector2(0,0);
    }
    /* ............................................................................... METHODS .. */

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void update(float delta) {
        // TODO
    }

    public void renderDebugging(ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);

        for(IntRectangle r : this.colliders) {
            shape.rect(r.x, r.y, r.width, r.height);
        }

        shape.end();
    }

    public void setUpTiledMap(int areaID, int startFieldID) {

        tiledMap = new TmxMapLoader().load("tilemaps/" + areaID + ".tmx");
        // create static bodies from colliders
        Rectangle r;
        for(MapObject mo : tiledMap.getLayers().get("colliderWalls1").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            colliders.add(new IntRectangle(MathUtils.round(r.x), MathUtils.round(r.y), MathUtils
                    .round(r.width), MathUtils.round(r.height)));
        }

        // get information about people on map
        for(MapObject mo : tiledMap.getLayers().get("livingEntities").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            mapPeople.add(new MapPersonInformation(
                    mo.getProperties().get("path", String.class),
                    new IntVector2(MathUtils.round(r.x), MathUtils.round(r.y)),
                    Boolean.valueOf(mo.getProperties().get("static", String.class)),
                    mo.getProperties().get("text", String.class),
                    Boolean.valueOf(mo.getProperties().get("male", String.class)),
                    Integer.valueOf(mo.getProperties().get("spriteIndex", String.class))));
        }

        // get information about signs on map
        for(MapObject mo : tiledMap.getLayers().get("objects").getObjects()) {
            if(mo.getName().equals("sign")) {
                mapSigns.add(new MapObjectInformation(
                        mo.getProperties().get("title", String.class),
                        mo.getProperties().get("text", String.class),
                        MathUtils.round(mo.getProperties().get("x", Float.class)),
                        MathUtils.round(mo.getProperties().get("y", Float.class))));
            }
        }

        // get information about sensors
        for(MapObject mo : tiledMap.getLayers().get("sensors").getObjects()) {
            if(mo.getName().equals("healing")) {
                healFields.add(new Rectangle(
                        ((RectangleMapObject) mo).getRectangle().x,
                        ((RectangleMapObject) mo).getRectangle().y,
                        GS.COL,GS.ROW));
            }
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

            if(mo.getName().equals("monsterArea")) {
                r = ((RectangleMapObject) mo).getRectangle();
                IntRectangle mr = new IntRectangle(r);
                Array<Float> ap = new Array<Float>();
                ap.add(Float.parseFloat(mo.getProperties().get("probability", String.class)));
                ap.add(Float.parseFloat(mo.getProperties().get("probability2", String.class)));
                ap.add(Float.parseFloat(mo.getProperties().get("probability3", String.class)));
                monsterAreas.add(new MonsterArea(
                        mr.x, mr.y, mr.width, mr.height,
                        mo.getProperties().get("monsters", String.class),
                        ap
                ));
            }
        }

        createBorderColliders(tiledMap);


        // Set background music
        String musicType = tiledMap.getProperties().get("musicType", String.class);
        if(musicType.equals("town"))
            bgMusic = media.getBGMusic(
                    MusicType.TOWN,
                    Integer.parseInt(tiledMap.getProperties().get("musicIndex", String.class))-1);
        bgMusic.setLooping(true);

    }

    /**
     * Creates a wall of colliders right around the active map so character can't just walk out
     * @param tiledMap
     */
    public void createBorderColliders(TiledMap tiledMap) {
        // Create Colliders around the level
        int mapWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class);
        for(int i=0; i<mapWidth+2; i++) {
            for(int j=0; j<2; j++)
                colliders.add(new IntRectangle(
                                (-1 + i)* GS.TILE_SIZE,
                                (-1 + j*(mapHeight+1))* GS.TILE_SIZE,
                                GS.TILE_SIZE,
                                GS.TILE_SIZE)
                );
        }

        for(int i=0; i<mapHeight; i++) {
            for(int j=0; j<2; j++) {
                colliders.add(
                        new IntRectangle(
                                (-1 + j*(mapWidth+1))* GS.TILE_SIZE,
                                i* GS.TILE_SIZE,
                                GS.TILE_SIZE,
                                GS.TILE_SIZE
                        )
                );
            }
        }
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

    public Array<IntRectangle> getColliders() {
        return colliders;
    }

    public Array<WarpPoint> getWarpPoints() {
        return warpPoints;
    }

    public Array<Rectangle> getHealFields() {
        return healFields;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void addMovingCollider(IntRectangle collider) {
        this.movingColliders.add(collider);
    }

    public void removeMovingCollider(IntRectangle collider) {
        this.movingColliders.removeValue(collider, false);
    }

    public Array<IntRectangle> getMovingColliders() {
        return movingColliders;
    }

    public Array<MapPersonInformation> getMapPeople() {
        return mapPeople;
    }

    public Array<MapObjectInformation> getMapSigns() {
        return mapSigns;
    }

    public Array<MonsterArea> getMonsterAreas() {
        return monsterAreas;
    }
}
