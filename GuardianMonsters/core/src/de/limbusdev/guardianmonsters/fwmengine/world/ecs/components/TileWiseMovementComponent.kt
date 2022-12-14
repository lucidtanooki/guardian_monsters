package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.utils.geometry.IntVec2
import kotlin.properties.Delegates

class TileWiseMovementComponent : LimbusBehaviour()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object
    {
        const val className = "TileWiseMovementComponent"
        val TAG : String get() = className
    }

    private lateinit var inputComponent : InputComponent
    private var characterSpriteComponent : CharacterSpriteComponent? = null
    private var colliderComponent : ColliderComponent? = null

    private var nextX = 0
    private var nextY = 0

    var speed : Int = Constant.WALKING_SPEED_PLAYER
        set(value)
        {
            field = value
            framesPerStep = when
            {
                speed > 9 -> 1
                speed <= 0 -> 9
                else -> 10 - speed
            }
        }
    var defaultSpeed = speed; private set

    private val newFrameEveryXPixels = 6
    private var stepsSinceLastFrameUpdate = 0
    private var framesPerStep : Int = 1


    var currentMovement = SkyDirection.SSTOP
        private set

    var moving = false
        private set

    private var framesSinceLastPixelStep = 0

    private var gridSlot : IntVec2 by Delegates.observable(IntVec2())
    {
        _, _, newSlot -> run {
        onGridSlotChanged.forEach { it.invoke(newSlot) }
    }}

    // Register callback functions for changing gridSlot here: Callback(newGridSlot)
    val onGridSlotChanged = mutableListOf<((IntVec2) -> Unit)>()

    val onMovementImpossible = mutableListOf<((LimbusGameObject) -> Unit)>()


    // --------------------------------------------------------------------------------------------- METHODS
    override fun initialize()
    {
        super.initialize()

        // Get obligatory component
        inputComponent = gameObject.getOrCreate()

        // Get optional components
        characterSpriteComponent = gameObject.get()
        colliderComponent = gameObject.get()

        defaultSpeed = speed
    }

    override fun update60fps()
    {
        super.update60fps()

        if(!moving)
        {
            // For every new tile-step, initialize the new movement
            initializeMovement()
        }
        else
        {
            // after initializing, perform all the pixel-steps
            applyMovement()
        }
    }


    /**
     * Turns character according to the touched direction and initializes movement, if touch is
     * longer than 100 ms and nothing blocks the way.
     */
    private fun initializeMovement() : Boolean
    {

            characterSpriteComponent?.sprite?.let { safeSprite ->

                if (inputComponent.talking)
                {
                    // Initialize Movement - don't go further if stopped
                    safeSprite.changeState(inputComponent.talkDirection)
                    return false
                }
                else
                {
                    // Turn Character to the chosen direction
                    safeSprite.changeState(inputComponent.direction)
                }
            }

            if (inputComponent.direction.isStop()) { return false }

            // Check if next tile is empty
            val nextPosition = calculateAndSetNextPosition(inputComponent.direction)
            val isBlocked = isNextPositionBlocked(nextPosition)
            if (isBlocked) { return false }

            // Start Movement
            framesSinceLastPixelStep = 0
            currentMovement = inputComponent.direction  // set movement direction
            moving = true                               // entity is moving right now

            // Move Collider to next position
            colliderComponent?.let { collider ->

                collider.offsetX = currentMovement.x * Constant.TILE_SIZE
                collider.offsetY = currentMovement.y * Constant.TILE_SIZE
            }

            return true
    }

    private fun applyMovement() : Boolean
    {
        // Move to next pixel only when step duration has passed
        framesSinceLastPixelStep++
        if (framesSinceLastPixelStep < framesPerStep) { return false }

        // Set transform to new position
        transform.x += currentMovement.x
        transform.y += currentMovement.y

        colliderComponent?.let {

            it.offsetX -= currentMovement.x
            it.offsetY -= currentMovement.y
        }

        // Update animation, every X pixel steps
        if (stepsSinceLastFrameUpdate >= newFrameEveryXPixels)
        {
            stepsSinceLastFrameUpdate = 0
            characterSpriteComponent?.sprite?.toNextFrame()
        }
        stepsSinceLastFrameUpdate++

        // Check if movement is complete (next tile reached)
        val movementComplete = when (currentMovement)
        {
            SkyDirection.N, SkyDirection.S -> transform.y == nextY
            SkyDirection.W, SkyDirection.E -> transform.x == nextX
            else -> false
        }

            // Update grid slot after moving to new tile
        if (movementComplete)
        {
            gridSlot = transform.onGrid
            moving = false

            if (inputComponent.talking)
            {
                characterSpriteComponent?.sprite?.changeState(inputComponent.talkDirection.nostop())
            }

            // Reset Speed, if it was changed by anything
            speed = defaultSpeed
        }

        framesSinceLastPixelStep = 0

        return true
    }

    /** Returns whether the given slot is blocked by a collider. */
    private fun isNextPositionBlocked(nextPosition: IntVec2) : Boolean
    {
        if(colliderComponent == null) { return false }
        if(colliderComponent?.isTrigger == true) { return false }

            // Check whether movement is possible or blocked by a collider
        for (otherGameObject in CoreSL.world.getAllWith(ColliderComponent::class, transform.layer))
        {
            if (otherGameObject != gameObject && otherGameObject.enabled)
            {
                val otherCollider = otherGameObject.get<ColliderComponent>()

                val nextPos = nextPosition.offset(Constant.TILE_SIZE / 2)
                if (otherCollider?.blocks(nextPos) == true)
                {
                    // Movement Impossible, tell subscribers.
                    onMovementImpossible.forEach { it.invoke(otherGameObject) }
                    return true
                }
            }
        }

        return false
    }

    private fun calculateAndSetNextPosition(direction: SkyDirection) : IntVec2
    {
        // Define potential next position according to the input direction
        when (direction)
        {
            SkyDirection.N ->
            {
                nextX = transform.x
                nextY = transform.y + Constant.TILE_SIZE
            }
            SkyDirection.W ->
            {
                nextX = transform.x - Constant.TILE_SIZE
                nextY = transform.y
            }
            SkyDirection.E ->
            {
                nextX = transform.x + Constant.TILE_SIZE
                nextY = transform.y
            }
            else ->
            {
                nextX = transform.x
                nextY = transform.y - Constant.TILE_SIZE
            }
        }

        return IntVec2(nextX, nextY)
    }
}