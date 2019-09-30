package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

/**
 * A CheckPointDependencyComponent is used to check, if the player has already achieved all needed
 * checkpoints. This could mean that a barrier disappears, if the player has done checkpoints 1 and 2.
 */
abstract class CheckPointDependencyComponent(val checkPointIDs: Set<Int>) : LimbusBehaviour()
{
    override fun initialize()
    {
        super.initialize()

        val saveGame = CoreSL.world.hero.get<SaveGameComponent>()
        if(saveGame != null)
        {
            if(checkPointIDs.all { it -> saveGame.isCheckPointEnabled(it) })
            {
                doIfAllCheckPointsAreAchieved()
            }
        }
    }

    abstract fun doIfAllCheckPointsAreAchieved()
}