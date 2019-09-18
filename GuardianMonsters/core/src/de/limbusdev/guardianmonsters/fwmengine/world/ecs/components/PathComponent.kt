package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.TimeUtils
import de.limbusdev.guardianmonsters.Constant

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.utils.geometry.IntVec2
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

    override val defaultJson: String get() = "enabled: true, path: SSTOP"

    private var inputComponent : InputComponent? = null
    private var tileWiseMovementComponent : TileWiseMovementComponent? = null

    // --------------------------------------------------------------------------------------------- PROPERTIES
    var startMoving = true
    var moving      = false
    var currentDir  = 0
    var stopCounter = 0
    var talking     = false
    var talkDir     = SkyDirection.S
    var stoppedSince : Long = 0


    // --------------------------------------------------------------------------------------------- METHODS
    override fun update(deltaTime: Float)
    {
        super.update(deltaTime)

        if(inputComponent == null)
        {
            initializeInputComponent()
        }

        if(tileWiseMovementComponent == null && inputComponent != null)
        {
            initializeTileWiseMovement()
        }

        if(!moving && TimeUtils.timeSinceMillis(stoppedSince) > 1000)
        {
            moving = true
            newTileReachedCallback(IntVec2())
        }
        if(!moving && TimeUtils.timeSinceMillis(stoppedSince) <= 1000)
        {
            inputComponent?.firstTip = TimeUtils.millis()
        }
    }

    private fun initializeInputComponent()
    {
        inputComponent = gameObject?.get()
        inputComponent?.startMoving = false
        inputComponent?.touchDown = false
        inputComponent?.skyDir = SkyDirection.S
        inputComponent?.firstTip = TimeUtils.millis()
    }

    private fun initializeTileWiseMovement()
    {
        tileWiseMovementComponent = gameObject?.get()
        tileWiseMovementComponent?.speed = Constant.ONE_STEP_DURATION_PERSON
        tileWiseMovementComponent?.onGridSlotChanged?.add { slot -> newTileReachedCallback(slot) }
        inputComponent?.startMoving = true
        inputComponent?.touchDown = true
        newTileReachedCallback(IntVec2())
    }

    private fun newTileReachedCallback(newSlot: IntVec2)
    {
        next()
        if(path[currentDir] == path[currentDir].stop())
        {
            inputComponent?.touchDown = true
            inputComponent?.firstTip = TimeUtils.millis()
            inputComponent?.skyDir = path[currentDir].nostop()
            stoppedSince = TimeUtils.millis()
            moving = false
        }
        else
        {
            inputComponent?.startMoving = true
            moving = true
            inputComponent?.touchDown = true
        }
        inputComponent?.nextInput = path[currentDir].nostop()
    }

    /**
     * Moves on to the next direction in the contained path. If it reaches the end of the path it
     * will start from the beginning.
     */
    operator fun next()
    {
        currentDir++
        if (currentDir >= path.size) { currentDir = 0 }
    }
}
