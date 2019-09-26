package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.CharacterSpriteComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.PathComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

/** Convenience component for easy person creation in tiled. */
class PersonComponent
(
        var male: Boolean = true,
        var sprite: Int = 0,
        var name: String = "",
        var path: String = "SSTOP"
)
    : LimbusBehaviour()
{
    companion object
    {
        const val className = "PersonComponent"
        const val defaultJson = "male: true, sprite: 1, name: <MAP>_<ID>, path: SSTOP"
    }

    override fun initialize()
    {
        super.initialize()

        gameObject.add(InputComponent())
        gameObject.add(CharacterSpriteComponentParser.parse(male, sprite))
        gameObject.add(ColliderComponent())
        gameObject.add(TileWiseMovementComponent())
        gameObject.add(PathComponentParser.parse(path))

        val conversation = ConversationComponent()
        conversation.name = "person_name_$name"
        conversation.text = "person_$name"
        gameObject.add(conversation)

        gameObject.add(CutSceneComponent())
    }

    object Parser : IComponentParser<PersonComponent>
    {
        override fun createComponent() = PersonComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): PersonComponent?
        {
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringNoBrackets = mapObject.properties[className, defaultJson]

            return json.fromJson(PersonComponent::class.java, "{$jsonStringNoBrackets}")
        }

    }
}