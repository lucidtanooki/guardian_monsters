package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.utils.geometry.IntRect


/**
 * Simple [Component] to hold an [IntRect] to represent a moving [Entity]'s collider.
 *
 * @author Georg Eckert 2015-11-15
 */
class ColliderComponent
(
        enabled: Boolean = true,
        var offsetX: Int = 0,
        var offsetY: Int = 0,
        var width: Int = 16,
        var height: Int = 16
)
    : LimbusBehaviour(), Component
{
    init
    {
        this.enabled = enabled
    }

    override val defaultJson =
            """
                    enabled: true,
                    offsetX: 0,
                    offsetY: 0,
                    width: 16,
                    height: 16
            """.trimMargin()


    val asRectangle get() = IntRect(offsetX, offsetY, width, height)
}
