package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.TimeUtils
import de.limbusdev.guardianmonsters.Constant

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logError
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

    private lateinit var inputComponent : InputComponent
    private lateinit var tileWiseMovementComponent : TileWiseMovementComponent

    // --------------------------------------------------------------------------------------------- PROPERTIES
    var moving      = true
    var currentDir  = -1
    var talking     = false
    var talkDir     = SkyDirection.S
    private var stoppedSince : Long = 0


    // --------------------------------------------------------------------------------------------- METHODS
    override fun update60fps()
    {
        super.update60fps()

        if(!moving)
        {
            stoppedSince++
            if(stoppedSince > 60)
            {
                stoppedSince = 0
                moving = true
                newTileReachedCallback(IntVec2())
            }
        }
    }

    override fun initialize()
    {
        super.initialize()

        initializeInputComponent()
        initializeTileWiseMovement()
    }

    private fun initializeInputComponent()
    {
        if(gameObject == null) { logError(TAG) { "PathComponent needs a parent LimbusGameObject" } }
        if(gameObject?.get<InputComponent>() == null) { logError(TAG) { "PathComponent needs an InputComponent" } }

        inputComponent = gameObject?.get()!!
    }

    private fun initializeTileWiseMovement()
    {
        if(gameObject == null) { logError(TAG) { "PathComponent needs a parent LimbusGameObject" } }
        if(gameObject?.get<TileWiseMovementComponent>() == null) { logError(TAG) { "PathComponent needs an TileWiseMovementComponent" } }

        tileWiseMovementComponent = gameObject?.get()!!
        tileWiseMovementComponent.speed = Constant.WALKING_SPEED_AI
        tileWiseMovementComponent.onGridSlotChanged.add { slot -> newTileReachedCallback(slot) }

        newTileReachedCallback(IntVec2())
    }

    private fun newTileReachedCallback(newSlot: IntVec2)
    {
        if(!moving) { return }
        next()
        inputComponent.direction = path[currentDir]
        if(inputComponent.direction.isStop())
        {
            moving = false
        }
    }

    /**
     * Moves on to the next direction in the contained path. If it reaches the end of the path it
     * will start from the beginning.
     */
    fun next()
    {
        currentDir = (currentDir+1) % path.size
    }
}
