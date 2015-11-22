package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.DynamicBodyComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;

/**
 * Created by georg on 22.11.15.
 */
public class PositionSynchroSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<CharacterSpriteComponent> sm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);

    /* ........................................................................... CONSTRUCTOR .. */
    public PositionSynchroSystem() {};
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class).one(
                CharacterSpriteComponent.class,
                InputComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);

            // Synchronize CharacterSprite with DynamicBody
            if(entity.getComponent(CharacterSpriteComponent.class) != null) {
                CharacterSpriteComponent sprite = sm.get(entity);
                sprite.sprite.setPosition(
                        body.dynamicBody.getPosition().x - sprite.sprite.getWidth()/2,
                        body.dynamicBody.getPosition().y - body.fixture.getShape().getRadius()
                );
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
