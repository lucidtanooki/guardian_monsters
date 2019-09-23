package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import de.limbusdev.utils.logInfo

class ChangeLayerTriggerCallbackComponent
(
    val fromNorth : Int = -1,
    val fromEast  : Int = -1,
    val fromSouth : Int = 1,
    val fromWest  : Int = -1,
    val triggerID : Int = 0
)
    : TriggerCallbackComponent()
{
    override val defaultJson: String get() = "${super.defaultJson}, fromNorth: -1, fromSouth: 1, fromEast: -1, fromWest: 1"

    override fun initialize()
    {
        super.initialize()

        for(trigger in CoreSL.world.getAllWith("BoxTrigger2DComponent"))
        {
            val triggerComponent = trigger.get<BoxTrigger2DComponent>()
            if(triggerComponent != null && triggerComponent.triggerID == triggerID)
            {
                triggerComponent.onTriggerEntered.add { go, dir -> onTriggerEntered(go, dir) }
            }
        }
    }

    override fun onTriggerEntered(gameObject: LimbusGameObject?, fromDirection: Compass4?)
    {
        logInfo { "Trigger entered" }
        if(gameObject == null) { return }
        if(fromDirection == null) { return }
        println("oldLayer" + gameObject.transform.layer)
        gameObject.transform.layer += when(fromDirection)
        {
            Compass4.N -> fromNorth
            Compass4.E -> fromEast
            Compass4.S -> fromSouth
            Compass4.W -> fromWest
        }
        println("newLayer" + gameObject.transform.layer)
    }

    object Parser : IComponentParser<ChangeLayerTriggerCallbackComponent>
    {
        override fun parseComponent(json: Json, mapObject: MapObject): ChangeLayerTriggerCallbackComponent?
        {
            // MapObject must contain proper component
            if (!mapObject.properties.containsKey("ChangeLayerTriggerCallbackComponent")) { return null }

            val jsonStringWithoutBrackets = mapObject.properties["ChangeLayerTriggerCallbackComponent", ChangeLayerTriggerCallbackComponent().defaultJson]
            val callback = json.fromJson(ChangeLayerTriggerCallbackComponent::class.java, "{$jsonStringWithoutBrackets}")

            return callback
        }
    }
}