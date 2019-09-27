package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CutSceneComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.DisableGameObjectTriggerCallbackComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TriggerCallbackComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get
import ktx.collections.gdxArrayOf

object CutSceneComponentParser : IComponentParser<CutSceneComponent>
{
    private data class Data(var triggerID: Int = 0, var people: ArrayList<String> = ArrayList(), var scenes: ArrayList<String> = ArrayList())
    private data class Action(var person: String, var action: String, var value: String)

    override fun createComponent() = CutSceneComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): CutSceneComponent?
    {
        val json = Json()
        json.setElementType(Data::class.java, "people", String::class.java)

        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(CutSceneComponent.className)) { return null }

        val jsonStringWithoutBrackets = mapObject.properties[CutSceneComponent.className, CutSceneComponent.defaultJson]
        val data = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

        val actions = mutableListOf<Action>()
        for(scene in data.scenes)
        {
            val person = scene.substringBefore(",")
            val action = scene.substringAfter(",").substringBefore("(")
            val value = scene.substringAfter(",").substringAfter("(").substringBefore(")")
            actions.add(Action(person, action, value))
        }

        val cutScene = CutSceneComponent()
        return cutScene
    }
}