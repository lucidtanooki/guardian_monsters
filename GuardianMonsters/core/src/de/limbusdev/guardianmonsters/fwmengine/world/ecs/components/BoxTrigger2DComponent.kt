package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

class BoxTrigger2DComponent(var triggerID : Int = 0) : TriggerComponent()
{
    companion object
    {
        const val className = "BoxTrigger2DComponent"
        val defaultJson: String get() = "enabled: true, triggerID: 0"
    }

    private lateinit var collider : ColliderComponent

    override fun doTheyCollide(triggerCollider: ColliderComponent, otherCollider: ColliderComponent) : Boolean
    {
        return triggerCollider.asRectangle.overlaps(otherCollider.asRectangle)
    }


    // --------------------------------------------------------------------------------------------- PARSER
    object Parser : ITriggerParser<BoxTrigger2DComponent>()
    {
        private class Data(channel: String = "", val triggerID: Int = 0) : TriggerData(channel)

        override fun createComponent() = BoxTrigger2DComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): BoxTrigger2DComponent?
        {
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            val triggerData = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

            val trigger = BoxTrigger2DComponent(triggerData.triggerID)
            trigger.triggerChannel.addAll(parseCollisionChannel(triggerData.channel))

            return trigger
        }
    }
}