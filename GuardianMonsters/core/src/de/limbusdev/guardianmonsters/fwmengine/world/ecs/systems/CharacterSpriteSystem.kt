package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent

/**
 * Updates the entities sprites, setting the correct [Animation] frame,
 * [SkyDirection] and so on according to the given [Entity]'s [InputComponent]
 *
 * @author Georg Eckert 2015-11-22
 */
class CharacterSpriteSystem : EntitySystem()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private var entities: ImmutableArray<Entity>? = null


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        entities = engine.getEntitiesFor(Family
                .all(CharacterSpriteComponent::class.java)
                .one(InputComponent::class.java, PathComponent::class.java)
                .get())
    }

    override fun update(deltaTime: Float)
    {
        // Update every single CharacterSpriteComponent
        for (entity in entities!!)
        {
            val sprite = Components.characterSprite.get(entity)
            var direction = SkyDirection.SSTOP
            var moving: Boolean

            // If entity has InputComponent
            if (Components.input.has(entity))
            {
                direction = Components.getInputComponent(entity).skyDir
                moving = Components.input.get(entity).moving
                if (!moving)
                {
                    direction = when(direction)
                    {
                        SkyDirection.N -> SkyDirection.NSTOP
                        SkyDirection.S -> SkyDirection.SSTOP
                        SkyDirection.E -> SkyDirection.ESTOP
                        SkyDirection.W -> SkyDirection.WSTOP
                        else           -> SkyDirection.SSTOP
                    }
                }
            }

            // If entity has PathComponent
            if (Components.path.has(entity))
            {
                val entPath = Components.path.get(entity)

                direction = when(entPath.talking)
                {
                    true  -> entPath.talkDir
                    false -> entPath.path.get(entPath.currentDir)
                }

                // Get from path whether to move or not
                moving = when(entPath.staticEntity)
                {
                    true  -> entPath.staticEntity
                    false -> entPath.moving
                }
            }

            // Set animation according to input direction
            sprite.sprite.changeState(direction)
        }
    }
}
