package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

class ChangeLayerTriggerCallbackComponent
(
        private val fromNorth : Int = -1,
        private val fromEast  : Int = -1,
        private val fromSouth : Int = 1,
        private val fromWest  : Int = -1,
        private val triggerID : Int = 0
)
    : TriggerCallbackComponent()
{
    companion object
    {
        const val className = "ChangeLayerTriggerCallbackComponent"
        val defaultJson: String get() = "${TriggerCallbackComponent.defaultJson}, fromNorth: -1, fromSouth: 1, fromEast: -1, fromWest: 1"
    }

    override fun initialize()
    {
        super.initialize()

        for(trigger in CoreSL.world.getAllWith(BoxTrigger2DComponent::class))
        {
            val triggerComponent = trigger.get<BoxTrigger2DComponent>()
            if(triggerComponent != null && triggerComponent.triggerID == triggerID)
            {
                triggerComponent.onTriggerEntered.add { go -> onTriggerEntered(go) }
            }
        }
    }

    override fun onTriggerEntered(enteringGameObject: LimbusGameObject?)
    {
        if(enteringGameObject == null) { return }
        val fromDirection = Compass4.translate(enteringGameObject.get<TileWiseMovementComponent>()?.currentMovement?.invert() ?: SkyDirection.S)

        enteringGameObject.transform.layer += when(fromDirection)
        {
            Compass4.N -> fromNorth
            Compass4.E -> fromEast
            Compass4.S -> fromSouth
            Compass4.W -> fromWest
        }
    }

    object Parser : IComponentParser<ChangeLayerTriggerCallbackComponent>
    {
        override fun createComponent() = ChangeLayerTriggerCallbackComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): ChangeLayerTriggerCallbackComponent?
        {
            // MapObject must contain proper component
            if (!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            return json.fromJson(ChangeLayerTriggerCallbackComponent::class.java, "{$jsonStringWithoutBrackets}")
        }
    }
}