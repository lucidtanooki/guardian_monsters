package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object ConversationComponentParser : IComponentParser<ConversationComponent>
{
    override fun createComponent() = ConversationComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): ConversationComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("ConversationComponent")) { return null }

        val jsonStringWithoutBrackets = mapObject.properties["ConversationComponent", ConversationComponent.defaultJson]
        val conversation = json.fromJson(ConversationComponent::class.java, "{$jsonStringWithoutBrackets}")

        return conversation
    }
}