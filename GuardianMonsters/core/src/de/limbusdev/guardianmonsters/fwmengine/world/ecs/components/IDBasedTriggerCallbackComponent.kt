package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.BoxTrigger2DComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TriggerCallbackComponent

abstract class IDBasedTriggerCallbackComponent(val triggerID : Int = 0) : TriggerCallbackComponent()
{
    companion object
    {
        const val className = "IDBasedTriggerCallbackComponent"
        val defaultJson = "${TriggerCallbackComponent.defaultJson}, triggerID: 0}"
    }

    override fun initialize()
    {
        super.initialize()

        val trigger = LimbusGameObject.objectByTiledID(triggerID) ?: return

        trigger.get<BoxTrigger2DComponent>()?.onTriggerEntered?.add { go -> onTriggerEntered(go) }
    }
}