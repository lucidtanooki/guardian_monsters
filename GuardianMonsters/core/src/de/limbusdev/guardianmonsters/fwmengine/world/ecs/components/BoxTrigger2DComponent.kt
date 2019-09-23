package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

class BoxTrigger2DComponent(val triggerID : Int) : LimbusBehaviour()
{
    override val defaultJson: String get() = "enabled: true, triggerID: 0"

    val onTriggerEntered = mutableListOf<((LimbusGameObject?, Compass4?) -> Unit)>()
}