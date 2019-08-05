package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.GdxBehaviour

import de.limbusdev.utils.geometry.IntRect


/**
 * Simple [Component] to hold an [IntRect] to represent a moving [Entity]'s collider.
 *
 * @author Georg Eckert 2015-11-15
 */
class ColliderComponent(data: Data) : GdxBehaviour(), Component
{
    data class Data(var enabled: Boolean = true, var x: Int = 0, var y: Int = 0, var width: Int = 16, var height: Int = 16)

    // --------------------------------------------------------------------------------------------- PROPERTIES
    var collider: IntRect

    init
    {
        collider = IntRect(data.x, data.y, data.width, data.height)
    }
}
