package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour


/**
 * InputComponent
 *
 * @author Georg Eckert 2015-11-22
 */
class InputComponent : LimbusBehaviour(), Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var talking     : Boolean = false
    var inBattle    : Boolean = false

    var direction = SkyDirection.SSTOP
    var talkDirection = SkyDirection.SSTOP
}
