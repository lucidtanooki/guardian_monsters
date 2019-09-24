package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

/**
 * For every new component a ComponentParser must be implemented, to tell the Engine how to
 * generate that component from a MapObject's data (Json).
 *
 * All component parsers must be registered with World.
 */
interface IComponentParser<T : LimbusBehaviour>
{
    /** Takes the map object and returns a proper component object, or null if something is wrong. */
    fun parseComponent(json: Json, mapObject: MapObject) : T?

    fun createComponent() : T
}