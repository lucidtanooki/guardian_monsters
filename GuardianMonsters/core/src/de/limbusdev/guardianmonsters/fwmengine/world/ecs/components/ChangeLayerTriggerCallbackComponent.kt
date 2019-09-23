package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

class ChangeLayerTriggerCallbackComponent
(
    val fromNorth : Int = -1,
    val fromEast  : Int = -1,
    val fromSouth : Int = 1,
    val fromWest  : Int = -1
)
    : TriggerCallbackComponent()
{
    override val defaultJson: String get() = "${super.defaultJson}, fromNorth: -1, fromSouth: 1, fromEast: -1, fromWest: 1"

    override fun onTriggerEntered(gameObject: LimbusGameObject?, fromDirection: Compass4?)
    {
        if(gameObject == null) { return }
        if(fromDirection == null) { return }
        gameObject.transform.layer += when(fromDirection)
        {
            Compass4.N -> fromNorth
            Compass4.E -> fromEast
            Compass4.S -> fromSouth
            Compass4.W -> fromWest
        }
    }
}