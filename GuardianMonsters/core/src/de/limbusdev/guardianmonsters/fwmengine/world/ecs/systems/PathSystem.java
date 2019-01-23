package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.TimeUtils;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.utils.geometry.IntRect;
import de.limbusdev.utils.geometry.IntVec2;


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
                case N: position.nextX= position.getX();position.nextY = position.getY() + Constant.TILE_SIZE;
                    break;
                case W: position.nextX= position.getX() - Constant.TILE_SIZE;position.nextY = position.getY();
                    break;
                case E: position.nextX= position.getX() + Constant.TILE_SIZE;position.nextY = position.getY();
                    break;
                case S: position.nextX= position.getX();position.nextY = position.getY() - Constant.TILE_SIZE;
                    break;
                default: position.nextY= position.getX();position.nextY= position.getY();break;
            }

            /* Check whether movement is possible or blocked by a collider */
            IntVec2 nextPos = new IntVec2(0,0);
            for(IntRect r : gameArea.getMovingColliders().get(position.layer)) {
                nextPos.setX(position.nextX + Constant.TILE_SIZE / 2);
                nextPos.setY(position.nextY + Constant.TILE_SIZE / 2);
                if (!collider.collider.equals(r) && r.contains(nextPos)) return;
            }

            collider.collider.setX(position.nextX);
            collider.collider.setY(position.nextY);
            position.lastPixelStep = TimeUtils.millis();
            path.moving = true;
            path.startMoving = false;
        }

        // If moving, check whether next pixel step should take place
        if(!path.staticEntity && path.moving && TimeUtils.timeSinceMillis(position.lastPixelStep) >
                Constant.ONE_STEP_DURATION_PERSON && !path.talking) {
            switch(path.path.get(path.currentDir)) {
                case N: position.setY(position.getY() + 1);break;
                case W: position.setX(position.getX() - 1);break;
                case E: position.setX(position.getX() + 1);break;
                case S: position.setY(position.getY() - 1);break;
                default:path.stop();break;      // if stopping, count up stopping time
            }
            position.lastPixelStep = TimeUtils.millis();

            switch (path.path.get(path.currentDir)) {
                case N: if(position.getY() == position.nextY) path.moving = false;break;
                case E: if(position.getX() == position.nextX) path.moving = false;break;
                case W: if(position.getX() == position.nextX) path.moving = false;break;
                case S: if(position.getY() == position.nextY) path.moving = false;break;
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
