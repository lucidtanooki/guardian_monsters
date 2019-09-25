package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

/**
 * Disables the parent [LimbusGameObject], if all checkpoints are achieved.
 */
class CheckPointDependentDisableComponent(checkPointIDs: Set<Int>) : CheckPointDependencyComponent(checkPointIDs)
{
    override fun doIfAllCheckPointsAreAchieved()
    {
        gameObject.disable()
    }
}