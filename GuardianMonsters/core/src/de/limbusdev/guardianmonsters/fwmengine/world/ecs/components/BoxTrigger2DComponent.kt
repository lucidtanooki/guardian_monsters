package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

class BoxTrigger2DComponent(var triggerID : Int = 0) : TriggerComponent()
{
    companion object
    {
        const val className = "BoxTrigger2DComponent"
        val defaultJson: String get() = "enabled: true, triggerID: 0"
    }

    private lateinit var collider : ColliderComponent

    init
    {
        triggerChannel.add(HeroComponent::class)
        triggerChannel.add(TileWiseMovementComponent::class)
    }

    override fun doTheyCollide(triggerCollider: ColliderComponent, otherCollider: ColliderComponent) : Boolean
    {
        return triggerCollider.asRectangle.overlaps(otherCollider.asRectangle)
    }

    private fun checkForEnteringColliders(position: IntVec2)
    {
        println("Check entering: ${CoreSL.world.hero.get<TileWiseMovementComponent>()?.currentMovement ?: SkyDirection.S}")

        println("OnGrid = ${transform.onGrid} position = $position")

        if(currentlyOverlappingGameObjects.contains(CoreSL.world.hero))
        {
            transform.onGrid == position
        }

        if(transform.onGrid == position)
        {
            currentlyOverlappingGameObjects.add(CoreSL.world.hero)
            val direction = CoreSL.world.hero.get<TileWiseMovementComponent>()?.currentMovement ?: SkyDirection.S
            onTriggerEntered.forEach { it.invoke(CoreSL.world.hero) }
        }
    }


    // --------------------------------------------------------------------------------------------- PARSER
    object Parser : IComponentParser<BoxTrigger2DComponent>
    {
        override fun createComponent() = BoxTrigger2DComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): BoxTrigger2DComponent?
        {
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            return  json.fromJson(BoxTrigger2DComponent::class.java, "{$jsonStringWithoutBrackets}")
        }
    }
}