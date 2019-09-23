package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

/**
 * [LimbusGameObject]s with a SlidingComponent can be pushed around by the player.
 * To work, the same object  a [ColliderComponent], a [TileWiseMovementComponent], an
 * [InputComponent] and a [RigidBodyComponent].
 */
class SlidingComponent : LimbusBehaviour()
{
    override val defaultJson: String get() = ""

    private var tileWiseMovementComponent = TileWiseMovementComponent()
    private var inputComponent = InputComponent()

    override fun initialize()
    {
        super.initialize()

        tileWiseMovementComponent = gameObject?.get() ?: return
        inputComponent = gameObject?.get() ?: return

        tileWiseMovementComponent.onGridSlotChanged.add { stopPushing() }
    }

    fun push(direction: Compass4)
    {
        inputComponent.direction = direction.toSkyDirection()
    }

    private fun stopPushing()
    {
        inputComponent.direction = inputComponent.direction.stop()
    }
}