package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;

import org.limbusdev.monsterworld.ecs.components.CameraComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.geometry.IntRectangle;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 25.11.15.
 */
public class CameraSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private OrthographicCamera camera;
    private TiledMap tiledMap;
    private IntRectangle mapOutline;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<CameraComponent> cm
            = ComponentMapper.getFor(CameraComponent.class);
    private ComponentMapper<PositionComponent> pm
            = ComponentMapper.getFor(PositionComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Default constructor that will initialise an EntitySystem with priority 0.
     */
    public CameraSystem(OrthographicCamera camera, TiledMap tiledMap) {
        this.camera = camera;
        this.tiledMap = tiledMap;
        this.mapOutline = new IntRectangle(
                0,
                0,
                tiledMap.getProperties().get("width", Integer.class)* GlobalSettings.TILE_SIZE,
                tiledMap.getProperties().get("height", Integer.class)* GlobalSettings.TILE_SIZE
        );
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent.class,
                CameraComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent position = pm.get(entity);

            if (mapOutline.width >= camera.viewportWidth && mapOutline.height >= camera
                    .viewportHeight) {
                camera.position.x = MathUtils.clamp(position.x, 0 + camera.viewportWidth/2,
                        mapOutline.width - camera.viewportWidth/2);
                camera.position.y = MathUtils.clamp(position.y, 0 + camera.viewportHeight/2,
                        mapOutline.height - camera.viewportHeight/2);
            } else {
                camera.position.set(position.x, position.y, 0);
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
