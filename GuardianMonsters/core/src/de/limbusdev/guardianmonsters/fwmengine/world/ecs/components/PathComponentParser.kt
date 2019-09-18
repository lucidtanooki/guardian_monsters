package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object PathComponentParser  : IComponentParser<PathComponent>
{
    private data class Data(var enabled: Boolean = true, var dynamic: Boolean = false, var path: String = "SSTOP")

    override fun parseComponent(json: Json, mapObject: MapObject): PathComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey("PathComponent")) { return null }

        val jsonStringWithoutBrackets = mapObject.properties["PathComponent", PathComponent().defaultJson]

        val data = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

        val path = Array<SkyDirection>()
        if(data.path.isNotEmpty())
        {
            val pathString = data.path.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            pathString.forEach { path.add(SkyDirection.valueOf(it)) }
        }

        val pathComponent = PathComponent(path, data.dynamic)
        pathComponent.enabled = data.enabled

        return pathComponent
    }

}