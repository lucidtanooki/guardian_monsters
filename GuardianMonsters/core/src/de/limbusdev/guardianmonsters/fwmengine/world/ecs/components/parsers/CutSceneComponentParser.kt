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
    private data class Data(var triggerID: Int = 0, var people: ArrayList<String> = ArrayList(), var actions: ArrayList<String> = ArrayList())
    data class Action(var person: String, var action: String, var value: String)
    data class Person(var ID: String, var male: Boolean, var index: Int, var offX: Int, var offY : Int)

    override fun createComponent() = CutSceneComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): CutSceneComponent?
    {
        val json = Json()

        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(CutSceneComponent.className)) { return null }

        val jsonStringWithoutBrackets = mapObject.properties[CutSceneComponent.className, CutSceneComponent.defaultJson]
        val data = json.fromJson(Data::class.java, "{$jsonStringWithoutBrackets}")

        val people = mutableListOf<Person>()
        for(person in data.people)
        {
            var p = person.trim()              // removes start and end white space
            p = p.replace("\\s".toRegex(), "") // removes all white space
            p = p.replace("\\n*", "") // removes all line breaks
            val details = p.split(",")
            people.add(Person(details[0], details[1].toBoolean(), details[2].toInt(), details[3].toInt(), details[4].toInt()))
        }

        val actions = mutableListOf<Action>()
        for(scene in data.actions)
        {
            val person = scene.substringBefore(",")
            val action = scene.substringAfter(",").substringBefore("(")
            val value = scene.substringAfter(",").substringAfter("(").substringBefore(")")
            actions.add(Action(person, action, value))
        }

        val cutScene = CutSceneComponent(data.triggerID, people, actions)
        return cutScene
    }
}