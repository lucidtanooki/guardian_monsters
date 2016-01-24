package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.SpriteComponent;
import org.limbusdev.monsterworld.graphics.EntitySprite;
import org.limbusdev.monsterworld.rendering.OrthogonalTiledMapAndEntityRenderer;

/**
 * Created by georg on 22.11.15.
 */
public class SpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private OrthogonalTiledMapAndEntityRenderer mapRenderer;
    private ImmutableArray<Entity> visibleEntities;
    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteSystem(OrthogonalTiledMapAndEntityRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        // Get all entities with either Sprite-, Weapon- or CharacterSprite Components
        visibleEntities = engine.getEntitiesFor(Family.one(
                SpriteComponent.class,
                CharacterSpriteComponent.class
        ).get());
        for(Entity e : visibleEntities) {
            EntitySprite es = null;
            if(Components.characterSprite.has(e)) es = Components.characterSprite.get(e).sprite;
            if(Components.sprite.has(e)) es = Components.sprite.get(e).sprite;
            mapRenderer.addEntitySprite(es);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
