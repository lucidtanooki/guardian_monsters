package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.utils.geometry.IntRect
import de.limbusdev.utils.geometry.IntVec2


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
        var height: Int = 16,
        var inheritsExtent: Boolean = true,
        var isTrigger: Boolean = false
)
    : LimbusBehaviour()
{
    companion object
    {
        const val className ="ColliderComponent"
        val defaultJson = """
                    enabled: true,
                    offsetX: 0,
                    offsetY: 0,
                    width: 16,
                    height: 16,
                    inheritsExtent: true,
                    isTrigger: false
            """.trimMargin()
    }

    init
    {
        this.enabled = enabled
    }

    val asRectangle get() = IntRect((transform.x) + offsetX, (transform.y) + offsetY, width, height)

    /** Returns whether the given position is blocked by this collider */
    fun blocks(position : IntVec2) : Boolean
    {
        if(isTrigger) { return false }

        return asRectangle.contains(position)
    }
}
