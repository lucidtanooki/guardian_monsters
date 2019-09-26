package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object PathComponentParser : IComponentParser<PathComponent>
{
    private data class Data(var enabled: Boolean = true, var path: String = "SSTOP")

    override fun createComponent() = PathComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): PathComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(PathComponent.className)) { return null }

        val jsonStringWithoutBrackets = mapObject.properties[PathComponent.className, PathComponent.defaultJson]

        val data = json.fromJson(PathComponentParser.Data::class.java, "{$jsonStringWithoutBrackets}")

        val pathComponent = parse(data.path)
        pathComponent.enabled = data.enabled

        return pathComponent
    }

    fun parse(pathString: String) : PathComponent
    {
        val path = Array<SkyDirection>()
        if(pathString.isNotEmpty())
        {
            val p = pathString.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            p.forEach { path.add(SkyDirection.valueOf(it)) }
        }
        else
        {
            path.add(SkyDirection.SSTOP)
        }

        return PathComponent(path, path.size == 1)
    }
}