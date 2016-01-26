package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.TimeUtils;

import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PathComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.geometry.IntRectangle;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Moves around entities with a {@link PathComponent} like persons, animals and so on
 * Created by georg on 30.11.15.
 */
public class PathSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private GameArea gameArea;
    /* ........................................................................... CONSTRUCTOR .. */
    public PathSystem(GameArea gameArea) {
        this.gameArea = gameArea;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent.class,
                ColliderComponent.class,
                PathComponent.class).exclude(
                InputComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent position = Components.getPositionComponent(entity);
            ColliderComponent collider = Components.getColliderComponent(entity);
            PathComponent path = Components.getPathComponent(entity);
            makeOneStep(position, path, collider);
            position.updateGridPosition();
        }
    }

    public void makeOneStep(PositionComponent position, PathComponent path, ColliderComponent
            collider) {
        if(path.startMoving && !path.staticEntity) {
            // Define direction of movement
            switch(path.path.get(path.currentDir)) {
                case N: position.nextX=position.x;position.nextY = position.y + GlobalSettings.TILE_SIZE;
                    break;
                case W: position.nextX=position.x - GlobalSettings.TILE_SIZE;position.nextY = position.y;
                    break;
                case E: position.nextX=position.x + GlobalSettings.TILE_SIZE;position.nextY = position.y;
                    break;
                case S: position.nextX=position.x;position.nextY = position.y - GlobalSettings.TILE_SIZE;
                    break;
                default: position.nextY=position.x;position.nextY=position.y;break;
            }

            /* Check whether movement is possible or blocked by a collider */
            IntVector2 nextPos = new IntVector2(0,0);
            for(IntRectangle r : gameArea.getMovingColliders()) {
                nextPos.x = position.nextX + GlobalSettings.TILE_SIZE / 2;
                nextPos.y = position.nextY + GlobalSettings.TILE_SIZE / 2;
                if (!collider.collider.equals(r) && r.contains(nextPos)) return;
            }

            collider.collider.x = position.nextX;
            collider.collider.y = position.nextY;
            position.lastPixelStep = TimeUtils.millis();
            path.moving = true;
            path.startMoving = false;
        }

        // If moving, check whether next pixel step should take place
        if(!path.staticEntity && path.moving && TimeUtils.timeSinceMillis(position.lastPixelStep) >
                GlobalSettings.ONE_STEP_DURATION_PERSON && !path.talking) {
            switch(path.path.get(path.currentDir)) {
                case N: position.y += 1;break;
                case W: position.x -= 1;break;
                case E: position.x += 1;break;
                case S: position.y -= 1;break;
                default:path.stop();break;      // if stopping, count up stopping time
            }
            position.lastPixelStep = TimeUtils.millis();

            switch (path.path.get(path.currentDir)) {
                case N: if(position.y == position.nextY) path.moving = false;break;
                case E: if(position.x == position.nextX) path.moving = false;break;
                case W: if(position.x == position.nextX) path.moving = false;break;
                case S: if(position.y == position.nextY) path.moving = false;break;
                default: if(path.stopCounter==0) path.moving = false;break;
            }

            // Go on if finger is still on screen
            if(!path.moving) {
                path.next();
                path.startMoving = true;
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
