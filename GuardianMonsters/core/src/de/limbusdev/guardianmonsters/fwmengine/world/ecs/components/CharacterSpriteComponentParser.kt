package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object CharacterSpriteComponentParser : IComponentParser<CharacterSpriteComponent>
{
    private data class Data(var enabled: Boolean = true, var male: Boolean = true, var index: Int = 0)

    override fun parseComponent(json: Json, mapObject: MapObject): CharacterSpriteComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("CharacterSpriteComponent")) { return null }

        val jsonStringWithoutBrackets = mapObject.properties["CharacterSpriteComponent", CharacterSpriteComponent().defaultJson]

        val data = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

        val animatedSprite = CharacterSpriteComponent(AnimatedPersonSprite(data.male, data.index))
        animatedSprite.enabled = data.enabled

        return animatedSprite
    }
}