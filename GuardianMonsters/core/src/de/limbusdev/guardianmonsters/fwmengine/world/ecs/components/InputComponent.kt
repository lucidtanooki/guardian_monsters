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
class InputComponent(override val defaultJson: String = "") : LimbusBehaviour(), Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var skyDir      : SkyDirection
    var nextInput   : SkyDirection = SkyDirection.SSTOP
    var moving      : Boolean = false
    var startMoving : Boolean = false
    var talking     : Boolean = false
    var inBattle    : Boolean = false
    var touchDown   : Boolean = false
    var stop        : Boolean = true

    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        skyDir = nextInput
    }
}
