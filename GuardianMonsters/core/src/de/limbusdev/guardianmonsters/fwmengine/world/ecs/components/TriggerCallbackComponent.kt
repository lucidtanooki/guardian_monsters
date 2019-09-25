package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

/**
 * [LimbusGameObject]s with a TriggerCallbackComponent can respond to triggers firing events.
 * For example: a locked door with a TriggerCallbackComponent could open up on activating a switch,
 * if that switch is a trigger and the door's trigger callback component has subscribed to that
 * switches trigger events.
 */
abstract class TriggerCallbackComponent : LimbusBehaviour()
{
    companion object
    {
        val defaultJson: String get() = "enabled: true, triggerID: 0"
    }

    abstract fun onTriggerEntered(enteringGameObject: LimbusGameObject?);
}