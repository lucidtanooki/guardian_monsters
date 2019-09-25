package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent

object InputComponentParser : IComponentParser<InputComponent>
{
    override fun createComponent() = InputComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): InputComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(InputComponent.className)) { return null }

        return InputComponent()
    }
}