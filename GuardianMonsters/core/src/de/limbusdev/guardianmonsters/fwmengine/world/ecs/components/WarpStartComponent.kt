package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

class WarpStartComponent() : LimbusBehaviour()
{
    companion object
    {
        const val defaultJson = "targetMapID: 0, warpTargetID: 0"
    }

    var targetMapID = 0
    var warpTargetID = 0
}