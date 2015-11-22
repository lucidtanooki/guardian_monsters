package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
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
    private ComponentMapper<SpriteComponent> sm;
    private ComponentMapper<CharacterSpriteComponent> csm;
    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteSystem(OrthogonalTiledMapAndEntityRenderer mapRenderer) {
        this.sm = ComponentMapper.getFor(SpriteComponent.class);
        this.csm = ComponentMapper.getFor(CharacterSpriteComponent.class);
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
            if(sm.has(e)) es = sm.get(e).sprite;
            if(csm.has(e)) es = csm.get(e).sprite;
            mapRenderer.addEntitySprite(es);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
