package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.EntitySprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.ExtendedTiledMapRenderer


/**
 * Special [Component] which holds an [EntitySprite] for a visible actor. This component also holds
 * [Animation]s which are used by the [CharacterSpriteSystem] to animate and update an entity's
 * sprite.
 *
 * @author Georg Eckert 2015-11-22
 */
class CharacterSpriteComponent
(
        var sprite: AnimatedPersonSprite = AnimatedPersonSprite(male = true, index = 0)
)
    : LimbusBehaviour(), Component
{
    override val defaultJson: String = "enabled: true, male: true, index: 0"

    override fun update(deltaTime: Float)
    {
        super.update(deltaTime)

        var direction = SkyDirection.SSTOP

        // If parent GameObject has InputComponent
        val inputComponent = gameObject?.get<InputComponent>()
        if(inputComponent != null)
        {
            direction = when(inputComponent.moving)
            {
                true -> inputComponent.skyDir
                else -> inputComponent.skyDir.stop()
            }
        }

        // If parent GameObject hast PathComponent
        val pathComponent = gameObject?.get<PathComponent>()
        if(pathComponent != null)
        {
            direction = when(pathComponent.talking)
            {
                true -> pathComponent.talkDir
                false -> pathComponent.path[pathComponent.currentDir]
            }

            val moving = when(pathComponent.staticEntity)
            {
                true -> false
                false -> pathComponent.moving
            }
        }

        //sprite.changeState(direction)
    }

    /**
     * Call this, when the next animation frame should be displayed.
     */
    fun updateAnimationFrame() = sprite.toNextFrame()
}