package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

class WarpStartComponent() : LimbusBehaviour()
{
    override val defaultJson: String get() = "targetMapID: 0, warpTargetID: 0"

    var targetMapID = 0
    var warpTargetID = 0
}