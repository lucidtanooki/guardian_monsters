package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component

import de.limbusdev.utils.geometry.IntVec2


/**
 * Simple [Component] to store the camera position. Only [com.badlogic.ashley.core.Entity]s which
 * should be followed by the camera should get one.
 *
 * @author Georg Eckert 2015-11-22
 */
class CameraComponent : Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var position = IntVec2(0,0)
}
