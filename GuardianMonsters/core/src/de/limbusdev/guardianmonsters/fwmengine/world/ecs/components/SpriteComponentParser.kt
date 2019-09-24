package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.utils.Json

object SpriteComponentParser : IComponentParser<SpriteComponent>
{
    override fun createComponent() = SpriteComponent(TextureRegion())

    override fun parseComponent(json: Json, mapObject: MapObject): SpriteComponent?
    {
        if(!mapObject.properties.containsKey(SpriteComponent.className)) { return null }
        if(mapObject !is TextureMapObject) { return null }

        return SpriteComponent(mapObject.textureRegion)
    }
}