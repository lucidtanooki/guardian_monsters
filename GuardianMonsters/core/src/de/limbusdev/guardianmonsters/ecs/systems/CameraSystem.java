package de.limbusdev.guardianmonsters.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.ecs.components.CameraComponent;
import de.limbusdev.guardianmonsters.ecs.components.Components;
import de.limbusdev.guardianmonsters.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.geometry.IntRectangle;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * The camera system updates the cameras position according to the hero's position by keeping it
 * inside the lmaps bounds, unless a map is smaller than the {@link com.badlogic.gdx.graphics
 * .Camera}
 * field of view
 * Created by georg on 25.11.15.
 */
public class CameraSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private OrthographicCamera camera;
    private IntRectangle mapOutline;    // Bounds of map to be rendered

    private ImmutableArray<Entity> entities;
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Default constructor that will initialise an EntitySystem with priority 0.
     */
    public CameraSystem(OrthographicCamera camera, TiledMap tiledMap) {
        this.camera = camera;

        // Get the maps bounds
        this.mapOutline = new IntRectangle(
                0,
                0,
                tiledMap.getProperties().get("width", Integer.class)* GS.TILE_SIZE,
                tiledMap.getProperties().get("height", Integer.class)* GS.TILE_SIZE
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

        // Move all cameras of entities with a {@link CameraComponent} (hero only)
        for (Entity entity : entities) {
            PositionComponent position = Components.getPositionComponent(entity);

            if (mapOutline.width >= camera.viewportWidth &&
                    mapOutline.height >= camera.viewportHeight) {
                // If map is bigger than camera field
                camera.position.x = MathUtils.clamp(
                        position.x,
                        0 + camera.viewportWidth/2,
                        mapOutline.width - camera.viewportWidth/2);

                camera.position.y = MathUtils.clamp(
                        position.y,
                        0 + camera.viewportHeight/2,
                        mapOutline.height - camera.viewportHeight/2);
            } else {
                // If camera field is bigger than map dimension
                camera.position.set(position.x, position.y, 0);
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
