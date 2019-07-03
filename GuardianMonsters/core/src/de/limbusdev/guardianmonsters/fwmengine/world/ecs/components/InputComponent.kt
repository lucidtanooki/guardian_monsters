package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3

import de.limbusdev.guardianmonsters.enums.SkyDirection


/**
 * InputComponent
 *
 * @author Georg Eckert 2015-11-22
 */
class InputComponent : Component
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    var skyDir      : SkyDirection
    var nextInput   : SkyDirection = SkyDirection.SSTOP
    var moving      : Boolean = false
    var touchPos    : Vector3 = Vector3(0f, 0f, 0f)
    var startMoving : Boolean = false
    var talking     : Boolean = false
    var inBattle    : Boolean = false
    var touchDown   : Boolean = false
    var firstTip    : Long = 0

    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        skyDir = nextInput
    }
}
