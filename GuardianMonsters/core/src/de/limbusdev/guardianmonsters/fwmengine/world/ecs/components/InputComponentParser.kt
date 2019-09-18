package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json

object InputComponentParser : IComponentParser<InputComponent>
{
    override fun parseComponent(json: Json, mapObject: MapObject): InputComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("InputComponent")) { return null }

        val inputComponent = InputComponent()

        return InputComponent()
    }
}