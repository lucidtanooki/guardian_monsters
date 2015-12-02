package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.ecs.EntityComponentSystem;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.ComponentRetriever;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.geometry.WarpPoint;

/**
 * Created by georg on 23.11.15.
 */
public class MovementSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private Array<WarpPoint> warpPoints;
    private EntityComponentSystem ecs;
    /* ........................................................................... CONSTRUCTOR .. */
    public MovementSystem(EntityComponentSystem ecs, Array<WarpPoint> warpPoints) {
        this.ecs = ecs;
        this.warpPoints = warpPoints;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent.class).one(
                CharacterSpriteComponent.class,
                InputComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for(Entity e : entities) {
            if(e instanceof HeroEntity) {
                PositionComponent position = ComponentRetriever.getPositionComponent(e);
                Rectangle heroArea = new Rectangle(position.x, position.y, position.width,
                        position.height);
                for(WarpPoint w : warpPoints) {
                    if(heroArea.contains(w.x, w.y)) {
                        // TODO change game area
                        System.out.println("Changing to Map " + w.targetID);
                        ecs.changeGameArea(w.targetID, w.targetWarpPointID);
                    }
                }
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
