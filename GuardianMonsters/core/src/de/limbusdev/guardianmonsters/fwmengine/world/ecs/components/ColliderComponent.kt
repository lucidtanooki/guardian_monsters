package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

import de.limbusdev.utils.geometry.IntRect


/**
 * Simple [Component] to hold an [IntRect] to represent a moving [Entity]'s collider.
 *
 * @author Georg Eckert 2015-11-15
 */
class ColliderComponent(x: Int, y: Int, width: Int, height: Int) : Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var collider = IntRect(x,y,width,height)
}
