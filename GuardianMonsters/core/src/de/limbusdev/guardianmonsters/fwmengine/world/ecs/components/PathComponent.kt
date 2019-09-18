package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array

import com.badlogic.ashley.core.Entity

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import ktx.collections.gdxArrayOf


/**
 * Defines a path for AI to walk on. It also contains to boolean attributes to indicate whether an
 * [Entity] is about to move or already moving and the current direction.
 *
 * @author Georg Eckert 2015-11-30
 *
 *
 * Creates a component holding a path consisting of [SkyDirection]s for an [Entity] to follow.
 * Try to close the path into a circle so it can be followed again and again. There is no
 * collision detection for AI with level colliders.
 *
 * USAGE:
 *
 *  SkyDirection[] dirs = {
 *      SkyDirection.N,
 *      SkyDirection.N,
 *      SkyDirection.ESTOP,
 *      SkyDirection.WSTOP,
 *      SkyDirection.S,
 *      SkyDirection.S}
 *
 * PathComponent path = new PathComponent((new Array<SkyDirection>()).addAll(dirs));
 */
class PathComponent
(
        var path: Array<SkyDirection> = gdxArrayOf(SkyDirection.SSTOP),
        var staticEntity: Boolean = true
)
    : LimbusBehaviour(), Component
{
    companion object { const val TAG = "PathComponent" }

    override val defaultJson: String get() = "enabled: true, path: SSTOP, dynamic: false"

    // --------------------------------------------------------------------------------------------- PROPERTIES
    var startMoving = true
    var moving      = false
    var currentDir  = 0
    var stopCounter = 0
    var talking     = false
    var talkDir     = SkyDirection.S


    // --------------------------------------------------------------------------------------------- METHODS
    /**
     * Moves on to the next direction in the contained path. If it reaches the end of the path it
     * will start from the beginning.
     */
    operator fun next()
    {
        currentDir++
        if (currentDir >= path.size) { currentDir = 0 }
    }

    /**
     * Counts the duration of being in the stop status. When it reaches 32 the counter gets reset
     * @return  whether the path is still in stop mode
     */
    fun stop(): Boolean
    {
        stopCounter++

        return when(stopCounter)
        {
            32   -> { stopCounter = 0; false }
            else -> true
        }
    }
}
