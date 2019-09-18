package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json

object TileWiseMovementComponentParser : IComponentParser<TileWiseMovementComponent>
{
    override fun parseComponent(json: Json, mapObject: MapObject): TileWiseMovementComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("TileWiseMovementComponent")) { return null }

        return TileWiseMovementComponent()
    }
}