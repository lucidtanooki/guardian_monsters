package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser

/**
 * [LimbusGameObject]s with a SlidingComponent can be pushed around by other [LimbusGameObject]s,
 * if they have a [PusherComponent].
 * To work, the same object  a [ColliderComponent], a [TileWiseMovementComponent], an
 * [InputComponent] and a [RigidBodyComponent].
 */
class SlidingComponent : LimbusBehaviour()
{
    private lateinit var tileWiseMovementComponent : TileWiseMovementComponent
    private lateinit var inputComponent : InputComponent

    private var pusher : LimbusGameObject? = null

    override fun initialize()
    {
        super.initialize()

        tileWiseMovementComponent = gameObject.getOrCreate()
        inputComponent = gameObject.getOrCreate()

        tileWiseMovementComponent.speed = 4
        tileWiseMovementComponent.onGridSlotChanged.add { stopPushing() }
        tileWiseMovementComponent.onMovementImpossible.add { stopPushing() }
    }

    fun push(direction: Compass4, pusher: LimbusGameObject?)
    {
        this.pusher = pusher
        pusher?.get<TileWiseMovementComponent>()?.speed = tileWiseMovementComponent.speed
        inputComponent.direction = direction.toSkyDirection()
    }

    private fun stopPushing()
    {
        inputComponent.direction = inputComponent.direction.stop()

        val pushersMovement = pusher?.get<TileWiseMovementComponent>() ?: return
        pushersMovement.speed = pushersMovement.defaultSpeed
    }


    // --------------------------------------------------------------------------------------------- Component Parser
    object Parser : IComponentParser<SlidingComponent>
    {
        override fun createComponent() = SlidingComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): SlidingComponent?
        {
            return if(mapObject.properties.containsKey("SlidingComponent")) SlidingComponent() else null
        }
    }
}