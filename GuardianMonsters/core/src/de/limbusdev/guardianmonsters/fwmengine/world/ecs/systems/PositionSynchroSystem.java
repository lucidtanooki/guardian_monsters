package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;


/**
 * Synchronizes Sprites of Entities with their Position
 * Created by georg on 22.11.15.
 */
public class PositionSynchroSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    /* ........................................................................... CONSTRUCTOR .. */
    public PositionSynchroSystem() {};
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent.class,
                CharacterSpriteComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent position = Components.position.get(entity);

            // Synchronize CharacterSprite with PositionComponent
            if(entity.getComponent(CharacterSpriteComponent.class) != null) {
                CharacterSpriteComponent sprite = Components.characterSprite.get(entity);
                sprite.sprite.setPosition(position.x, position.y);
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
