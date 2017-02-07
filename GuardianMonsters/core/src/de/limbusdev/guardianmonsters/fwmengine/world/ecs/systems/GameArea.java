package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import org.w3c.dom.css.Rect;

import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.geometry.IntRectangle;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapDescriptionInfo;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation;
import de.limbusdev.guardianmonsters.fwmengine.world.model.WarpPoint;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MonsterArea;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OrthogonalTiledMapAndEntityRenderer;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Contains logic and information about one game world area like a forest or a path. One
 * OutDoorGameArea per Tiled Map.
 *
 * Created by georg on 21.11.15.
 */
public class GameArea {
    //................................................................................... ATTRIBUTES
    private TiledMap tiledMap;
    private OrthogonalTiledMapAndEntityRenderer mapRenderer;
    private String bgMusic;

    private ArrayMap<Integer,Array<IntRectangle>> colliders;
    private ArrayMap<Integer,Array<IntRectangle>> movingColliders;
    private ArrayMap<Integer,Array<WarpPoint>> warpPoints;
    private ArrayMap<Integer,Array<Rectangle>> healFields;
    private ArrayMap<Integer,Array<MapPersonInformation>> mapPeople;
    private ArrayMap<Integer,Array<MapDescriptionInfo>> descriptions;
    private ArrayMap<Integer,Array<MonsterArea>> monsterAreas;
    public IntVector2 gridPosition;
    public PositionComponent startPosition;
    public int areaID;

    //.................................................................................. CONSTRUCTOR
    public GameArea(int areaID, int startPosID) {
        this.startPosition = new PositionComponent(0,0,0,0,0);

        initializeArrays();

        this.tiledMap = setUpTiledMap(areaID, startPosID);
        this.mapRenderer = new OrthogonalTiledMapAndEntityRenderer(tiledMap);
        this.areaID = areaID;

        this.gridPosition = new IntVector2(0,0);
    }
    //...................................................................................... METHODS

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

        for(Array<IntRectangle> a : colliders.values()) {
            for (IntRectangle r : a) {
                shape.rect(r.x, r.y, r.width, r.height);
            }
        }

        shape.end();
    }

    public TiledMap setUpTiledMap(int areaID, int startFieldID) {

        TiledMap tiledMap = new TmxMapLoader().load("tilemaps/" + areaID + ".tmx");

        for(MapLayer layer : tiledMap.getLayers()) {
            if(layer.getName().contains("people")) {
                createPeople(layer);
            }

            if(layer.getName().contains("colliderWalls")) {
                createColliders(layer);
            }

            if(layer.getName().contains("descriptions")) {
                createDescriptions(layer);
            }

            if(layer.getName().contains("triggers")) {
                createTriggers(layer, startFieldID);
            }
        }

        createBorderColliders(tiledMap);


        // Set background music
        String musicType = tiledMap.getProperties().get("musicType", String.class);
        int musicIndex = Integer.parseInt(tiledMap.getProperties().get("musicIndex", String.class))-1;
        if(musicType.equals("town")) {
            bgMusic = AudioAssets.get().getBgMusicTown(musicIndex);
        }

        return tiledMap;

    }


    // ......................................................................... MAP OBJECT CREATION

    private void createTriggers(MapLayer layer, int startFieldID) {
        int layerIndex = layer.getName().toCharArray()[layer.getName().length()-1];
        Array<Rectangle> healingTriggers = new Array<Rectangle>();
        healFields.put(layerIndex,healingTriggers);
        Array<WarpPoint> warpTriggers = new Array<WarpPoint>();
        warpPoints.put(layerIndex, warpTriggers);
        Array<MonsterArea> battleTriggers = new Array<MonsterArea>();
        monsterAreas.put(layerIndex, battleTriggers);

        for(MapObject mo : layer.getObjects()) {
            if (mo.getName().equals("healing")) {
                healingTriggers.add(new Rectangle(
                    ((RectangleMapObject) mo).getRectangle().x,
                    ((RectangleMapObject) mo).getRectangle().y,
                    GS.COL, GS.ROW));
            }
            if (mo.getName().equals("warpField"))
                warpTriggers.add(new WarpPoint(
                    Integer.parseInt(mo.getProperties().get("targetWarpPointID", String.class)),
                    ((RectangleMapObject) mo).getRectangle(),
                    Integer.parseInt(mo.getProperties().get("targetID", String.class)))
                );
            if (mo.getName().equals("startField")) {
                if (Integer.parseInt(mo.getProperties().get("fieldID", String.class))
                    == startFieldID) {
                    Rectangle field = ((RectangleMapObject) mo).getRectangle();
                    startPosition.x = MathUtils.round(field.x);
                    startPosition.y = MathUtils.round(field.y);
                    startPosition.layer = layerIndex;
                }
            }

            if (mo.getName().equals("monsterArea")) {
                Rectangle r = ((RectangleMapObject) mo).getRectangle();
                IntRectangle mr = new IntRectangle(r);
                Array<Float> ap = new Array<Float>();
                ap.add(Float.parseFloat(mo.getProperties().get("probability", String.class)));
                ap.add(Float.parseFloat(mo.getProperties().get("probability2", String.class)));
                ap.add(Float.parseFloat(mo.getProperties().get("probability3", String.class)));
                battleTriggers.add(new MonsterArea(
                    mr.x, mr.y, mr.width, mr.height,
                    mo.getProperties().get("monsters", String.class),
                    ap
                ));
            }
        }
    }

    private void createDescriptions(MapLayer layer) {
        Array<MapDescriptionInfo> descriptionLayer = new Array<MapDescriptionInfo>();
        int layerIndex = layer.getName().toCharArray()[layer.getName().length()-1];
        descriptions.put(layerIndex,descriptionLayer);

        // get information about signs on map
        for(MapObject mo : layer.getObjects()) {
            if(mo.getName().equals("sign")) {
                descriptionLayer.add(new MapDescriptionInfo(mo));
            }
        }
    }

    /**
     * Only for Layers containing "people" in their name
     * @param layer
     */
    private void createPeople(MapLayer layer) {
        Array<MapPersonInformation> peopleLayer = new Array<MapPersonInformation>();
        int layerIndex = layer.getName().toCharArray()[layer.getName().length()-1];
        mapPeople.put(layerIndex, peopleLayer);

        // get information about people on map
        for(MapObject mo : layer.getObjects()) {
            peopleLayer.add(new MapPersonInformation(mo));
        }
    }

    /**
     * Only for layers containing "colliderWalls" in their name
     * Creates rectangle objects for every collider in the given layer
     * @param layer
     */
    private void createColliders(MapLayer layer) {
        Array<IntRectangle> colliderLayer = new Array<IntRectangle>();
        int layerIndex = layer.getName().toCharArray()[layer.getName().length()-1];
        colliders.put(layerIndex, colliderLayer);

        Rectangle r;
        for(MapObject mo : layer.getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            colliderLayer.add(new IntRectangle(
                MathUtils.round(r.x), MathUtils.round(r.y), MathUtils.round(r.width), MathUtils.round(r.height)));
        }
    }

    /**
     * Creates a wall of colliders right around the active map so character can't just walk out
     * @param tiledMap
     */
    public void createBorderColliders(TiledMap tiledMap) {

        int mapWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class);

        for(int i : colliders.keys()) {
            Array<IntRectangle> layerColliders = colliders.get(i);
            layerColliders.add(new IntRectangle(
                -1*GS.TILE_SIZE, -1*GS.TILE_SIZE, (mapWidth+2)*GS.TILE_SIZE, GS.TILE_SIZE
            ));
            layerColliders.add(new IntRectangle(
                -1*GS.TILE_SIZE, mapHeight*GS.TILE_SIZE, (mapWidth+2)*GS.TILE_SIZE, GS.TILE_SIZE
            ));
            layerColliders.add(new IntRectangle(
                -1*GS.TILE_SIZE, 0, GS.TILE_SIZE, mapHeight*GS.TILE_SIZE
            ));
            layerColliders.add(new IntRectangle(
                mapWidth*GS.TILE_SIZE, 0, GS.TILE_SIZE, mapHeight*GS.TILE_SIZE
            ));
        }

    }

    /* ..................................................................... GETTERS & SETTERS .. */

    public OrthogonalTiledMapAndEntityRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void dispose() {

    }

    public void playMusic() {
        Services.getAudio().playLoopMusic(bgMusic);
    }

    public void stopMusic() {
        Services.getAudio().stopMusic(bgMusic);
    }

    public ArrayMap<Integer, Array<IntRectangle>> getColliders() {
        return colliders;
    }

    public ArrayMap<Integer, Array<WarpPoint>> getWarpPoints() {
        return warpPoints;
    }

    public ArrayMap<Integer, Array<Rectangle>> getHealFields() {
        return healFields;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void addMovingCollider(IntRectangle collider, int layer) {
        if(!movingColliders.containsKey(layer)) {
            movingColliders.put(layer, new Array<IntRectangle>());
        }
        movingColliders.get(layer).add(collider);
    }

    public void removeMovingCollider(IntRectangle collider, int layer) {
        if(movingColliders.containsKey(layer)) {
            movingColliders.get(layer).removeValue(collider, false);
        }
    }

    public ArrayMap<Integer, Array<IntRectangle>> getMovingColliders() {
        return movingColliders;
    }

    public ArrayMap<Integer, Array<MapPersonInformation>> getMapPeople() {
        return mapPeople;
    }

    public ArrayMap<Integer,Array<MapDescriptionInfo>> getDescriptions() {
        return descriptions;
    }

    public ArrayMap<Integer, Array<MonsterArea>> getMonsterAreas() {
        return monsterAreas;
    }

    private void initializeArrays() {
        this.colliders = new ArrayMap<Integer, Array<IntRectangle>>();
        this.movingColliders = new ArrayMap<Integer, Array<IntRectangle>>();
        this.monsterAreas = new ArrayMap<Integer, Array<MonsterArea>>();
        this.warpPoints = new ArrayMap<Integer, Array<WarpPoint>>();
        this.mapPeople = new ArrayMap<Integer, Array<MapPersonInformation>>();
        this.descriptions = new ArrayMap<Integer, Array<MapDescriptionInfo>>();
        this.healFields = new ArrayMap<Integer, Array<Rectangle>>();
    }
}
