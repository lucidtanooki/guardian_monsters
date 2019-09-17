package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

class WarpTargetComponent() : LimbusBehaviour()
{
    override val defaultJson: String get() = "warpTargetID: 0"

    var warpTargetID = 0
}