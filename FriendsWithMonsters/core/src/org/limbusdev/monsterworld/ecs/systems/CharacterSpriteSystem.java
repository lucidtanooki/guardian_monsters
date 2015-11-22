package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.DynamicBodyComponent;

/**
 * Created by georg on 22.11.15.
 */
public class CharacterSpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<CharacterSpriteComponent> cm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteSystem() {}
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                CharacterSpriteComponent.class,
                DynamicBodyComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            CharacterSpriteComponent sprite = cm.get(entity);
            DynamicBodyComponent body = dm.get(entity);

            if(body.dynamicBody.getLinearVelocity().len() > 0.1)
                if(Math.abs(body.dynamicBody.getLinearVelocity().x)
                        > Math.abs(body.dynamicBody.getLinearVelocity().y)) {
                    // Character horizontally
                    if(body.dynamicBody.getLinearVelocity().x > 0)
                        sprite.recentAnim = sprite.animationImgs.get("e");
                    else
                        sprite.recentAnim = sprite.animationImgs.get("w");
                } else {
                    // Character vertically
                    if(body.dynamicBody.getLinearVelocity().y > 0)
                        sprite.recentAnim = sprite.animationImgs.get("n");
                    else
                        sprite.recentAnim = sprite.animationImgs.get("s");
                }

            sprite.recentIdleImg = sprite.recentAnim.getKeyFrames()[0];

            if(Math.abs(body.dynamicBody.getLinearVelocity().x) < 0.1 &&
                    Math.abs(body.dynamicBody.getLinearVelocity().y) < 0.1)
                sprite.sprite.setRegion(sprite.recentIdleImg);
            else
                sprite.sprite.setRegion(sprite.recentAnim.getKeyFrame(elapsedTime, true));

        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
