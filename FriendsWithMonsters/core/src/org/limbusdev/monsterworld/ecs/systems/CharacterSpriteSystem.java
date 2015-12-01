package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.ComponentRetreiver;
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
            if(ComponentRetreiver.inpCompMap.has(entity)) {
                direction = ComponentRetreiver.getInputComponent(entity).skyDir;
                moving = ComponentRetreiver.inpCompMap.get(entity).moving;
            }
            if(ComponentRetreiver.pathCompMap.has(entity)) {
                direction = ComponentRetreiver.getPathComponent(entity).path.get
                        (ComponentRetreiver.getPathComponent(entity).currentDir);
                moving = ComponentRetreiver.pathCompMap.get(entity).moving;
            }

            switch(direction) {
                case N: sprite.recentAnim = sprite.animationImgs.get("n");break;
                case E: sprite.recentAnim = sprite.animationImgs.get("e");break;
                case W: sprite.recentAnim = sprite.animationImgs.get("w");break;
                default: sprite.recentAnim = sprite.animationImgs.get("s");break;
            }

            sprite.recentIdleImg = sprite.recentAnim.getKeyFrames()[0];

            if(!moving) sprite.sprite.setRegion(sprite.recentIdleImg);
            else sprite.sprite.setRegion(sprite.recentAnim.getKeyFrame(elapsedTime, true));

        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
