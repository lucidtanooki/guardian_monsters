package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;


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
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent.class,
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent position = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.position.get(entity);

            // Synchronize CharacterSprite with PositionComponent
            if(entity.getComponent(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent.class) != null) {
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent sprite = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.characterSprite.get(entity);
                sprite.sprite.setPosition(position.x, position.y);
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
