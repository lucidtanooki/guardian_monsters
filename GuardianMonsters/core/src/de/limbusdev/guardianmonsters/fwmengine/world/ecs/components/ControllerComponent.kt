package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

class ControllerComponent() : LimbusBehaviour()
{
    override val defaultJson: String get() = "enabled: true"
}