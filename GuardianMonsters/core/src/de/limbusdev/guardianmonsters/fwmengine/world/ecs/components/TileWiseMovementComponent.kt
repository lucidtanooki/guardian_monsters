package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.utils.TimeUtils
import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World
import de.limbusdev.utils.geometry.IntVec2

class TileWiseMovementComponent() : LimbusBehaviour()
{
    override val defaultJson: String get() = ""

    override fun update(deltaTime: Float)
    {
        super.update(deltaTime)

        val transform = gameObject?.transform ?: return
        val inputComponent = gameObject?.get<InputComponent>() ?: return

        initializeMovement(transform, inputComponent)
        applyMovement(transform, inputComponent)


    }

    private fun applyMovement(transform: Transform, inputComponent: InputComponent) : Boolean
    {
        // If entity is already moving, and last incremental step has completed (long enough ago)
        if
        (
                inputComponent.moving &&
                TimeUtils.timeSinceMillis(transform.lastPixelStep) > Constant.ONE_STEPDURATION_MS
        ) {
            // TODO move from MovementSystem here
            return true
        }

        return false
    }

    private fun initializeMovement(transform: Transform, inputComponent: InputComponent) : Boolean
    {
        // Initialize Movement
        if
                (
                inputComponent.startMoving &&
                TimeUtils.timeSinceMillis(inputComponent.firstTip) > 100 &&
                inputComponent.touchDown
        ){
            val nextPosition = calculateNextPosition()
            val isBlocked = isNextPositionBlocked(nextPosition)
            if(isBlocked) { return false }

            transform.lastPixelStep = TimeUtils.millis()    // remember time of this iteration

            inputComponent.moving = true        // entity is moving right now
            inputComponent.startMoving = false  // because entity now started moving

            World.hero.get<CharacterSpriteComponent>()?.sprite?.changeState(inputComponent.skyDir)

            return true
        }

        return false
    }

    private fun isNextPositionBlocked(nextPosition: IntVec2) : Boolean
    {
        val colliderComponent = gameObject?.get<ColliderComponent>() ?: return true
        val transform = gameObject?.transform ?: return true

        // Check whether movement is possible or blocked by a collider
        val nextPos = IntVec2(0, 0)

        for (r in World.getAllWith("ColliderComponent", transform.layer))
        {
            val staticCollider = r.get<ColliderComponent>()

            if(staticCollider != null)
            {
                nextPos.x = nextPosition.x + Constant.TILE_SIZE/2
                nextPos.y = nextPosition.y + Constant.TILE_SIZE/2

                if (staticCollider.asRectangle.contains(nextPos)) { return true }
            }
        }

        return false

        // TODO collision with people
        /*for (r in ecs.gameArea.dynamicColliders.get(transform.layer))
        {
            nextPos.x = transform.nextX + Constant.TILE_SIZE/2
            nextPos.y = transform.nextY + Constant.TILE_SIZE/2

            if (collider.asRectangle != r.asRectangle && r.asRectangle.contains(nextPos)) { return }
        }*/
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