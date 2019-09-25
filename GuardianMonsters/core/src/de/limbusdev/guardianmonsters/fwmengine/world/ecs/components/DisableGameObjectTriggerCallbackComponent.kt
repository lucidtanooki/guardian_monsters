package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

class DisableGameObjectTriggerCallbackComponent(private val triggerID : Int = 0) : TriggerCallbackComponent()
{
    companion object
    {
        const val className = "DisableGameObjectTriggerCallbackComponent"
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
        gameObject.disable()
    }

    object Parser : IComponentParser<DisableGameObjectTriggerCallbackComponent>
    {
        override fun createComponent() = DisableGameObjectTriggerCallbackComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): DisableGameObjectTriggerCallbackComponent?
        {
            // MapObject must contain proper component
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            val disabler = json.fromJson(DisableGameObjectTriggerCallbackComponent::class.java, "{$jsonStringWithoutBrackets}")

            return disabler
        }
    }
}