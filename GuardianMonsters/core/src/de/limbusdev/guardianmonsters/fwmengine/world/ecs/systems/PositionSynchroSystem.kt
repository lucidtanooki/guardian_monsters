package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TransformComponent
import de.limbusdev.guardianmonsters.utils.getComponent


/**
 * Synchronizes Sprites of Entities with their Position
 *
 * @author Georg Eckert 2015-11-22
 */
class PositionSynchroSystem : EntitySystem()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private lateinit var entities: ImmutableArray<Entity>


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        entities = engine.getEntitiesFor(Family.all(
                TransformComponent::class.java,
                CharacterSpriteComponent::class.java
        ).get())
    }

    override fun update(deltaTime: Float)
    {
        for (entity in entities)
        {
            val position = Components.position.get(entity)

            // Synchronize CharacterSprite with TransformComponent
            val sprite = entity.getComponent<CharacterSpriteComponent>()
            sprite?.sprite?.setPosition(position.xf, position.yf)
        }
    }
}
