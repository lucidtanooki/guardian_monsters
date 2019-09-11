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
class ColliderComponent() : LimbusBehaviour(), Component
{
    constructor(enabled: Boolean, x: Int, y: Int, width: Int, height: Int) : this()
    {
        this.enabled = enabled
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    override val defaultJson =
            """
                    enabled: true,
                    x: 0,
                    y: 0,
                    width: 16,
                    height: 16
            """.trimMargin()

    var x = 0
    var y = 0
    var width = 16
    var height = 16

    val asRectangle get() = IntRect(x, y, width, height)
}
