package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

/**
 * Disables the parent [LimbusGameObject], if all checkpoints are achieved.
 */
class CheckPointDependentDisableComponent(checkPointIDs: Set<Int> = setOf()) : CheckPointDependencyComponent(checkPointIDs)
{
    companion object
    {
        const val className = "CheckPointDependentDisableComponent"
        const val defaultJson = "checkPointIDs: [1,2]"
    }

    override fun doIfAllCheckPointsAreAchieved()
    {
        gameObject.disable()
    }

    object Parser : IComponentParser<CheckPointDependentDisableComponent>
    {
        private data class Data(var checkPointIDs: ArrayList<Int> = ArrayList())

        override fun createComponent() = CheckPointDependentDisableComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): CheckPointDependentDisableComponent?
        {
            // MapObject must contain proper component
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            val data = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

            val ids = mutableSetOf<Int>()
            data.checkPointIDs.forEach { ids.add(it) }

            return CheckPointDependentDisableComponent(ids)
        }
    }
}