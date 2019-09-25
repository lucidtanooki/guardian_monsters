package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

/**
 * A CheckPointComponent is used to activate done checkpoints and enable dependent stuff.
 */
class CheckPointComponent(val checkPointID: Int = 0) : LimbusBehaviour()
{

}