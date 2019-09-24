package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

abstract class RenderingLimbusBehaviour : LimbusBehaviour()
{
    abstract fun render()
}