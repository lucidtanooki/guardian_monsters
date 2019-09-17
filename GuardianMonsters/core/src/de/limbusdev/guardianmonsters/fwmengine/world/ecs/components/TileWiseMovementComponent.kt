package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.utils.TimeUtils
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreServiceLocator
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug
import kotlin.properties.Delegates

class TileWiseMovementComponent() : LimbusBehaviour()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "TileWiseMovementComponent" }

    override val defaultJson: String get() = ""

    private val newFrameEveryXPixels = 6
    private var stepsSinceLastFrameUpdate = 0

    private var gridSlot : IntVec2 by Delegates.observable(IntVec2())
    {
        _, _, newSlot -> run {

        logDebug(TAG) { "Hero at $newSlot" }
        onGridSlotChanged.forEach { it.invoke(newSlot) }
    }}

    // Register callback functions for changing gridSlot here: Callback(newGridSlot)
    val onGridSlotChanged = mutableListOf<((IntVec2) -> Unit)>()


    // --------------------------------------------------------------------------------------------- METHODS
    override fun update(deltaTime: Float)
    {
        super.update(deltaTime)

        val transform = gameObject?.transform ?: return
        val inputComponent = gameObject?.get<InputComponent>() ?: return

        // For every new tile-step, initialize the new movement
        initializeMovement(transform, inputComponent)

        // after initializing, perform all the pixel-steps
        applyMovement(transform, inputComponent)
    }

    private fun applyMovement(transform: Transform, inputComponent: InputComponent) : Boolean
    {
        // Early Exits
        if (!inputComponent.moving)
        {
            CoreServiceLocator.world.hero.get<CharacterSpriteComponent>()?.sprite?.resetAnimation()
            return false
        }
        if (TimeUtils.timeSinceMillis(transform.lastPixelStep) < Constant.ONE_STEPDURATION_MS) { return false }

        // If entity is already moving, and last incremental step has completed (long enough ago)
        val spriteComponent = gameObject?.get<CharacterSpriteComponent>() ?: return false

        when (inputComponent.skyDir)
        {
            SkyDirection.N -> transform.y += 1
            SkyDirection.W -> transform.x -= 1
            SkyDirection.E -> transform.x += 1
            else -> transform.y -= 1
        }
        transform.lastPixelStep = TimeUtils.millis()

        if (stepsSinceLastFrameUpdate >= newFrameEveryXPixels)
        {
            stepsSinceLastFrameUpdate = 0
            spriteComponent.sprite.toNextFrame()
        }
        stepsSinceLastFrameUpdate++

        // Check if movement is complete
        val movementComplete = when (inputComponent.skyDir)
        {
            SkyDirection.N, SkyDirection.S -> transform.y == transform.nextY
            SkyDirection.W, SkyDirection.E -> transform.x == transform.nextX
            else -> false
        }

        if (movementComplete)
        {
            gridSlot = transform.onGrid

            inputComponent.moving = false
        }

        // Movement completed
        if (!inputComponent.moving)
        {
            // Continue movement when button is pressed
            if (inputComponent.touchDown)
            {
                inputComponent.startMoving = true
                inputComponent.skyDir = inputComponent.nextInput
            }
        }

        return true
    }


    /**
     * Turns character according to the touched direction and initializes movement, if touch is
     * longer than 100 ms and nothing blocks the way.
     */
    private fun initializeMovement(transform: Transform, inputComponent: InputComponent) : Boolean
    {
        // Initialize Movement
        if(!inputComponent.startMoving || !inputComponent.touchDown) { return false }

        // Turn Character to the chosen direction
        CoreServiceLocator.world.hero.get<CharacterSpriteComponent>()?.sprite?.changeState(inputComponent.skyDir)

        if(TimeUtils.timeSinceMillis(inputComponent.firstTip) > 100)
        {
            // Start movement in that direction
            val nextPosition = calculateNextPosition()
            val isBlocked = isNextPositionBlocked(nextPosition)
            if(isBlocked) { return false }

            transform.lastPixelStep = TimeUtils.millis()    // remember time of this iteration

            inputComponent.moving = true        // entity is moving right now
            inputComponent.startMoving = false  // because entity now started moving

            return true
        }

        return false
    }

    /** Returns whether the given slot is blocked by a collider. */
    private fun isNextPositionBlocked(nextPosition: IntVec2) : Boolean
    {
        val colliderComponent = gameObject?.get<ColliderComponent>() ?: return true
        val transform = gameObject?.transform ?: return true

        // Check whether movement is possible or blocked by a collider
        val nextPos = IntVec2(0, 0)

        for (collider in CoreServiceLocator.world.getAllWith("ColliderComponent", transform.layer))
        {
            val staticCollider = collider.get<ColliderComponent>()

            if(staticCollider != null)
            {
                nextPos.x = nextPosition.x + Constant.TILE_SIZE/2
                nextPos.y = nextPosition.y + Constant.TILE_SIZE/2

                if (staticCollider.asRectangle.contains(nextPos) && !staticCollider.isTrigger) { return true }
            }
        }

        return false
    }

    private fun calculateNextPosition() : IntVec2
    {
        val inputComponent = gameObject?.get<InputComponent>() ?: return IntVec2()
        val transform = gameObject?.transform ?: return IntVec2()

        // Define potential next position according to the input direction
        when (inputComponent.skyDir)
        {
            SkyDirection.N ->
            {
                transform.nextX = transform.x
                transform.nextY = transform.y + Constant.TILE_SIZE
            }
            SkyDirection.W ->
            {
                transform.nextX = transform.x - Constant.TILE_SIZE
                transform.nextY = transform.y
            }
            SkyDirection.E ->
            {
                transform.nextX = transform.x + Constant.TILE_SIZE
                transform.nextY = transform.y
            }
            else ->
            {
                transform.nextX = transform.x
                transform.nextY = transform.y - Constant.TILE_SIZE
            }
        }

        return IntVec2(transform.nextX, transform.nextY)
    }
}