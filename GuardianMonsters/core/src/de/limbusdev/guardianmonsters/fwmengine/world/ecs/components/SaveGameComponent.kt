package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.model.gamestate.GameState


/**
 * SaveGameComponent
 *
 * @author Georg Eckert 2015-12-03
 */
object SaveGameComponent : LimbusBehaviour()
{
    var gameState: GameState? = null
    private val checkPoints = mutableSetOf<Int>()

    fun enableCheckPoint(checkPointID: Int)
    {
        checkPoints.add(checkPointID)
    }

    /** Returns true, if the player has already gathered the check point in question. */
    fun isCheckPointEnabled(checkPointID: Int) : Boolean
    {
        return checkPoints.contains(checkPointID)
    }
}