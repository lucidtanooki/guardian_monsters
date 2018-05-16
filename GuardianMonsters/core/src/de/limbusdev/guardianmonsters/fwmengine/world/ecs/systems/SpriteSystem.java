package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.ExtendedTiledMapRenderer;


/**
 * Created by georg on 22.11.15.
 */
public class SpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ExtendedTiledMapRenderer mapRenderer;

    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteSystem(ExtendedTiledMapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        // Get all entities with either Sprite-, Equipment- or CharacterSprite Components
        ImmutableArray<Entity> visibleEntities = engine.getEntitiesFor(Family.one(
                SpriteComponent.class,
                CharacterSpriteComponent.class
        ).get());
        for(Entity e : visibleEntities) {
            AnimatedPersonSprite es;
            if(Components.characterSprite.has(e)) {
                es = Components.characterSprite.get(e).sprite;
                mapRenderer.addEntitySprite(es);
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
