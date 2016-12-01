package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.TimeUtils;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.geometry.IntRectangle;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Moves around entities with a {@link de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent} like persons, animals and so on
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
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent.class,
                ColliderComponent.class,
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent.class).exclude(
                InputComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent position = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.getPositionComponent(entity);
            ColliderComponent collider = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.getColliderComponent(entity);
            de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent path = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.getPathComponent(entity);
            makeOneStep(position, path, collider);
            position.updateGridPosition();
        }
    }

    public void makeOneStep(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent position, de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent path, ColliderComponent
            collider) {
        if(path.startMoving && !path.staticEntity) {
            // Define direction of movement
            switch(path.path.get(path.currentDir)) {
                case N: position.nextX=position.x;position.nextY = position.y + GS.TILE_SIZE;
                    break;
                case W: position.nextX=position.x - GS.TILE_SIZE;position.nextY = position.y;
                    break;
                case E: position.nextX=position.x + GS.TILE_SIZE;position.nextY = position.y;
                    break;
                case S: position.nextX=position.x;position.nextY = position.y - GS.TILE_SIZE;
                    break;
                default: position.nextY=position.x;position.nextY=position.y;break;
            }

            /* Check whether movement is possible or blocked by a collider */
            IntVector2 nextPos = new IntVector2(0,0);
            for(IntRectangle r : gameArea.getMovingColliders()) {
                nextPos.x = position.nextX + GS.TILE_SIZE / 2;
                nextPos.y = position.nextY + GS.TILE_SIZE / 2;
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
                GS.ONE_STEP_DURATION_PERSON && !path.talking) {
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