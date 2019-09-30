package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

/**
 * Enables a component of the same game object if all check points are achieved.
 */
class CheckPointDependentEnableBehaviourComponent<T : LimbusBehaviour>(checkPointIDs: Set<Int>) : CheckPointDependencyComponent(checkPointIDs)
{
    override fun doIfAllCheckPointsAreAchieved()
    {
        val component = gameObject.get<LimbusBehaviour>() as T?
        component?.enabled = true
    }
}