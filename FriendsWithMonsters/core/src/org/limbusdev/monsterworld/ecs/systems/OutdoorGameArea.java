package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import org.limbusdev.monsterworld.rendering.OrthogonalTiledMapAndEntityRenderer;
import org.limbusdev.monsterworld.utils.GlobalSettings;

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

    /* ........................................................................... CONSTRUCTOR .. */
    public OutdoorGameArea(World world, int areaID) {
        setUpTiledMap(world, areaID);
        this.mapRenderer = new OrthogonalTiledMapAndEntityRenderer(
                tiledMap, GlobalSettings.PIXELS_PER_METER, this);
    }
    /* ............................................................................... METHODS .. */

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void setUpTiledMap(World world, int areaID) {


        tiledMap = new TmxMapLoader().load("tilemaps/" + areaID + ".tmx");
        // create static bodies from colliders
        Rectangle r;
        for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            BodyDef groundBodyDef  = new BodyDef();
            groundBodyDef.type     = BodyDef.BodyType.StaticBody;
            groundBodyDef.position.set(new Vector2(r.x / 16f + r.width / 32f, r.y / 16f + r.height
                    / 32f));
            Body groundBody        = world.createBody(groundBodyDef);

            PolygonShape groundBox = new PolygonShape();
            groundBox.setAsBox(r.width/32f, r.height/32f);
            groundBody.createFixture(groundBox, 0.0f);
            groundBox.dispose();
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */

    public OrthogonalTiledMapAndEntityRenderer getMapRenderer() {
        return mapRenderer;
    }
}
