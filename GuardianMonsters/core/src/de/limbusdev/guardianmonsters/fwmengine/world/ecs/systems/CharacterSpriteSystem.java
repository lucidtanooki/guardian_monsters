package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent;

/**
 * Updates the entities sprites, setting the correct @link Animation} frame,
 * {@link SkyDirection} and so on according to the given {@link Entity}'s {@link InputComponent}
 * Created by georg on 22.11.15.
 */
public class CharacterSpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteSystem() {}
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                CharacterSpriteComponent.class).one(
                InputComponent.class,
                PathComponent.class).get());
    }

    public void update(float deltaTime) {

        // Update every single CharacterSpriteComponent
        for (Entity entity : entities) {
            CharacterSpriteComponent sprite = Components.characterSprite.get(entity);
            SkyDirection direction = SkyDirection.SSTOP;
            boolean moving;

            // If entity has InputComponent
            if(Components.input.has(entity)) {
                direction = Components.getInputComponent(entity).skyDir;
                moving = Components.input.get(entity).moving;
                if(!moving) {
                    switch(direction) {
                        case N: direction = SkyDirection.NSTOP;break;
                        case S: direction = SkyDirection.SSTOP;break;
                        case E: direction = SkyDirection.ESTOP;break;
                        case W: direction = SkyDirection.WSTOP;break;
                    }
                }
            }


            // If entity has PathComponent
            if(Components.path.has(entity)) {
                PathComponent entPath = Components.path.get(entity);
                if(entPath.talking) direction = entPath.talkDir;
                else direction = entPath.path.get(entPath.currentDir);

                // Get from path whether to move or not
                if(!entPath.staticEntity) moving = entPath.moving ;
                else moving = entPath.staticEntity;
            }

            // Set animation according to input direction
            sprite.sprite.changeState(direction);

        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
