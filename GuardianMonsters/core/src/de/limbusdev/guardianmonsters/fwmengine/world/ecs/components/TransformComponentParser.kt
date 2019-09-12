package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object TransformComponentParser : IComponentParser<TransformComponent>
{
    override fun parseComponent(json: Json, mapObject: MapObject): TransformComponent?
    {
        // RectangleMapObjects have implicit TransformComponent
        if(mapObject !is RectangleMapObject) { return null }

        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("TransformComponent")) { return null }

        // Must only contain useful values for enabled and layer in Tiled, since every Tiled
        // RectangleMapObject has x, y, width and height
        val jsonStringWithoutBrackets = mapObject.properties["TransformComponent", TransformComponent().defaultJson]
        val transform = json.fromJson(TransformComponent::class.java, "{$jsonStringWithoutBrackets}")
        // Necessary since libGDX inverts the Y axis of tiled map objects automatically.
        // Here we count Y up from the bottom. Tiled does it the other way round.
        transform.x = MathUtils.round(mapObject.rectangle.x)
        transform.y = MathUtils.round(mapObject.rectangle.y)

        return transform
    }
}