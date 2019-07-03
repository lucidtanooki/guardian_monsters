package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SpriteComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.ExtendedTiledMapRenderer
import de.limbusdev.guardianmonsters.utils.getComponent


/**
 * SpriteSystem
 *
 * @author Georg Eckert 2015-11-22
 */
class SpriteSystem(private val mapRenderer: ExtendedTiledMapRenderer) : EntitySystem()
{
    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        // Get all entities with either Sprite-, Equipment- or CharacterSprite Components
        val visibleEntities = engine.getEntitiesFor(Family.one(
                SpriteComponent::class.java,
                CharacterSpriteComponent::class.java
        ).get())

        for (entity in visibleEntities)
        {
            val spriteComponent = entity.getComponent<CharacterSpriteComponent>()
            if(spriteComponent != null)
            {
                mapRenderer.addEntitySprite(spriteComponent.sprite)
            }
        }
    }
}
