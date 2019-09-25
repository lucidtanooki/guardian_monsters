package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TileWiseMovementComponent

object TileWiseMovementComponentParser : IComponentParser<TileWiseMovementComponent>
{
    override fun createComponent() = TileWiseMovementComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): TileWiseMovementComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(TileWiseMovementComponent.className)) { return null }

        return TileWiseMovementComponent()
    }
}