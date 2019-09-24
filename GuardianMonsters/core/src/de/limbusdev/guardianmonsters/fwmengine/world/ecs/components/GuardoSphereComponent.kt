package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere

/**
 * @author Georg Eckert 2019-07-03
 */
class GuardoSphereComponent : LimbusBehaviour()
{
    companion object
    {
        const val className ="GuardoSphereComponent"
    }

    var guardoSphere = GuardoSphere()
}
