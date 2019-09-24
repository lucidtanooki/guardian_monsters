package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

class BoxTrigger2DComponent(var triggerID : Int = 0) : LimbusBehaviour()
{
    companion object
    {
        val defaultJson: String get() = "enabled: true, triggerID: 0"
    }

    val onTriggerEntered = mutableListOf<((LimbusGameObject?, Compass4?) -> Unit)>()
    val currentlyOverlappingGameObjects = mutableListOf<LimbusGameObject>()

    override fun initialize()
    {
        super.initialize()

        println("init")

        val heroMovement = CoreSL.world.hero.get<TileWiseMovementComponent>()
        heroMovement?.onGridSlotChanged?.add { slot -> checkForEnteringColliders(slot) }
    }

    private fun checkForEnteringColliders(position: IntVec2)
    {
        println("Check entering: ${CoreSL.world.hero.get<TileWiseMovementComponent>()?.currentMovement ?: SkyDirection.S}")

        if(gameObject == null) { return }

        println("OnGrid = ${gameObject?.transform?.onGrid} position = $position")

        if(gameObject?.transform?.onGrid == position)
        {
            println("add")
        }

        if(gameObject?.transform?.onGrid != position)
        {
            println("remove")
        }


        if(currentlyOverlappingGameObjects.contains(CoreSL.world.hero))
        {
            gameObject?.transform?.onGrid == position
        }

        if(gameObject?.transform?.onGrid == position)
        {
            currentlyOverlappingGameObjects.add(CoreSL.world.hero)
            val direction = CoreSL.world.hero.get<TileWiseMovementComponent>()?.currentMovement ?: SkyDirection.S
            onTriggerEntered.forEach { it.invoke(CoreSL.world.hero, Compass4.translate(direction.invert())) }
        }
    }

    object Parser : IComponentParser<BoxTrigger2DComponent>
    {
        override fun createComponent() = BoxTrigger2DComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): BoxTrigger2DComponent?
        {
            if(!mapObject.properties.containsKey("BoxTrigger2DComponent")) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[BoxTrigger2DComponent::class.java.simpleName, defaultJson]
            val trigger = json.fromJson(BoxTrigger2DComponent::class.java, "{$jsonStringWithoutBrackets}")

            return trigger
        }

    }
}