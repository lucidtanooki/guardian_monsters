package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

class WarpTargetComponent() : LimbusBehaviour()
{
    companion object
    {
        const val defaultJson = "warpTargetID: 0"
    }

    var warpTargetID = 0
}