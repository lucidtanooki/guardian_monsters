package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object WarpStartComponentParser : IComponentParser<WarpStartComponent>
{
    override fun createComponent() = WarpStartComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): WarpStartComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(WarpStartComponent.className)) { return null }

        val jsonStringWithoutBrackets = mapObject.properties[WarpStartComponent.className, WarpStartComponent.defaultJson]
        return json.fromJson(WarpStartComponent::class.java, "{$jsonStringWithoutBrackets}")
    }
}