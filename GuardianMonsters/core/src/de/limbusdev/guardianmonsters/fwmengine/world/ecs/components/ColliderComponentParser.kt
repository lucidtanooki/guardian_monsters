package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object ColliderComponentParser : IComponentParser<ColliderComponent>
{
    override fun parseComponent(json: Json, mapObject: MapObject): ColliderComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("ColliderComponent")) { return null }

        val jsonStringWithoutBrackets = mapObject.properties["ColliderComponent", ColliderComponent().defaultJson]
        val collider = json.fromJson(ColliderComponent::class.java, "{$jsonStringWithoutBrackets}")

        // If MapObject is RectangularMapObject and inheritsExtent is true, use transform values
        if(mapObject is RectangleMapObject && collider.inheritsExtent)
        {
            collider.width = MathUtils.round(mapObject.rectangle.width)
            collider.height = MathUtils.round(mapObject.rectangle.height)
        }

        return collider
    }
}