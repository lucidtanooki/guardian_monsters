package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PathComponent;
import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Updates the entities sprites, setting the correct @link Animation} frame,
 * {@link SkyDirection} and so on according to the given {@link Entity}'s {@link InputComponent}
 * Created by georg on 22.11.15.
 */
public class CharacterSpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

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
        // Calculation elapsedTime for animation
        elapsedTime += deltaTime;

        // Update every single CharacterSpriteComponent
        for (Entity entity : entities) {
            CharacterSpriteComponent sprite = Components.characterSprite.get(entity);
            SkyDirection direction = SkyDirection.S;
            boolean moving = false;

            // If entity has InputComponent
            if(Components.input.has(entity)) {
                direction = Components.getInputComponent(entity).skyDir;
                moving = Components.input.get(entity).moving;
            }


            // If entitiy has PathComponent
            if(Components.path.has(entity)) {
                PathComponent entPath = Components.path.get(entity);
                if(entPath.talking) direction = entPath.talkDir;
                else direction = entPath.path.get(entPath.currentDir);

                // Get from path whether to move or not
                if(!entPath.staticEntity) moving = entPath.moving ;
                else moving = entPath.staticEntity;
            }

            // Set animation according to input direction
            switch(direction) {
                case NSTOP:;
                case N: sprite.recentAnim = sprite.animationImgs.get("n");break;
                case ESTOP:;
                case E: sprite.recentAnim = sprite.animationImgs.get("e");break;
                case WSTOP:;
                case W: sprite.recentAnim = sprite.animationImgs.get("w");break;
                default: sprite.recentAnim = sprite.animationImgs.get("s");break;
            }

            // Set the first animation frame as recent image when standing still
            sprite.recentIdleImg = sprite.recentAnim.getKeyFrames()[0];

            // set correct texture according to state
            if(!moving
                || direction.equals(SkyDirection.NSTOP)
                || direction.equals(SkyDirection.SSTOP)
                || direction.equals(SkyDirection.ESTOP)
                || direction.equals(SkyDirection.WSTOP))

                sprite.sprite.setRegion(sprite.recentIdleImg);  // idle image
            else
                sprite.sprite.setRegion(sprite.recentAnim.getKeyFrame(elapsedTime, true));

        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
