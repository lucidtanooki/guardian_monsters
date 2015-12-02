package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.ComponentRetriever;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PathComponent;
import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Created by georg on 22.11.15.
 */
public class CharacterSpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<CharacterSpriteComponent> cm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
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
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            CharacterSpriteComponent sprite = cm.get(entity);
            SkyDirection direction = SkyDirection.S;
            boolean moving = false;
            if(ComponentRetriever.inpCompMap.has(entity)) {
                direction = ComponentRetriever.getInputComponent(entity).skyDir;
                moving = ComponentRetriever.inpCompMap.get(entity).moving;
            }
            if(ComponentRetriever.pathCompMap.has(entity)) {
                direction = ComponentRetriever.getPathComponent(entity).path.get
                        (ComponentRetriever.getPathComponent(entity).currentDir);
                if(!ComponentRetriever.pathCompMap.get(entity).staticEntity)
                    moving = ComponentRetriever.pathCompMap.get(entity).moving ;
                else
                    moving = ComponentRetriever.pathCompMap.get(entity).staticEntity;
            }

            switch(direction) {
                case NSTOP:;
                case N: sprite.recentAnim = sprite.animationImgs.get("n");break;
                case ESTOP:;
                case E: sprite.recentAnim = sprite.animationImgs.get("e");break;
                case WSTOP:;
                case W: sprite.recentAnim = sprite.animationImgs.get("w");break;
                default: sprite.recentAnim = sprite.animationImgs.get("s");break;
            }

            sprite.recentIdleImg = sprite.recentAnim.getKeyFrames()[0];

            // set correct texture according to state
            if(!moving
                || direction.equals(SkyDirection.NSTOP)
                || direction.equals(SkyDirection.SSTOP)
                    || direction.equals(SkyDirection.ESTOP)
                    || direction.equals(SkyDirection.WSTOP))
                sprite.sprite.setRegion(sprite.recentIdleImg);
            else sprite.sprite.setRegion(sprite.recentAnim.getKeyFrame(elapsedTime, true));

        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
