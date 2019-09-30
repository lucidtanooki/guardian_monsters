package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

/**
 * A CheckPointComponent is used to activate done checkpoints and enable dependent stuff.
 */
class CheckPointComponent(val checkPointID: Int = 0) : LimbusBehaviour()
{
    companion object
    {
        const val className ="CheckPointComponent"
        const val defaultJson = "checkPointID: 1"
    }

    object Parser : IComponentParser<CheckPointComponent>
    {
        override fun parseComponent(json: Json, mapObject: MapObject): CheckPointComponent?
        {
            // MapObject must contain proper component
            if (!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            return json.fromJson(CheckPointComponent::class.java, "{$jsonStringWithoutBrackets}")
        }

        override fun createComponent(): CheckPointComponent = CheckPointComponent()
    }
}