package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object WarpTargetComponentParser : IComponentParser<WarpTargetComponent>
{
    override fun createComponent() = WarpTargetComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): WarpTargetComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("WarpTargetComponent")) { return null }

        val jsonStringWithoutBrackets = mapObject.properties["WarpTargetComponent", WarpTargetComponent.defaultJson]
        val warpTarget = json.fromJson(WarpTargetComponent::class.java, "{$jsonStringWithoutBrackets}")

        return warpTarget
    }
}