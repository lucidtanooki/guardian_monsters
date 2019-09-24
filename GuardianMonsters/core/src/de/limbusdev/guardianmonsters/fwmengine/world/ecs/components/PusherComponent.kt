package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

/**
 * [LimbusGameObject]s with a PusherComponent are enabled to move around other [LimbusGameObject]s
 * with a [SlidingComponent]. To work, the parent game object also needs a [TileWiseMovementComponent]
 * and an [InputComponent].
 */
class PusherComponent : LimbusBehaviour()
{
    private var tileWiseMovementComponent = TileWiseMovementComponent()
    private var inputComponent = InputComponent()

    override fun initialize()
    {
        super.initialize()

        inputComponent = gameObject?.get() ?: return
        tileWiseMovementComponent = gameObject?.get() ?: return
        tileWiseMovementComponent.onMovementImpossible.add { blocker -> push(blocker) }
    }

    private fun push(blockingObject: LimbusGameObject)
    {
        val blockingMovementComponent = blockingObject.get<TileWiseMovementComponent>() ?: return
        val blockingSlidingComponent = blockingObject.get<SlidingComponent>() ?: return

        if(blockingMovementComponent.moving) { return }

        blockingSlidingComponent.push(Compass4.translate(inputComponent.direction), gameObject)
        tileWiseMovementComponent.speed = blockingMovementComponent.speed
    }
}