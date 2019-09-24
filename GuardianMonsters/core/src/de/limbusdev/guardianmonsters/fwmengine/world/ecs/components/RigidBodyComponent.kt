package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

class RigidBodyComponent() : LimbusBehaviour()
{
    companion object
    {
        const val defaultJson = "enabled: true, applyPhysics: false"
    }
}