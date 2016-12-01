package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OrthogonalTiledMapAndEntityRenderer;


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
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SpriteComponent.class,
                CharacterSpriteComponent.class
        ).get());
        for(Entity e : visibleEntities) {
            EntitySprite es = null;
            if(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.characterSprite.has(e)) es = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.characterSprite.get(e).sprite;
            if(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.sprite.has(e)) es = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.sprite.get(e).sprite;
            mapRenderer.addEntitySprite(es);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}