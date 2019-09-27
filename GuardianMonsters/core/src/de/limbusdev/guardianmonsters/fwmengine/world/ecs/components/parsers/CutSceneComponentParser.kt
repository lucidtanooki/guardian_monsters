package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CutSceneComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.DisableGameObjectTriggerCallbackComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TriggerCallbackComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

object CutSceneComponentParser : IComponentParser<CutSceneComponent>
{
    override fun createComponent() = CutSceneComponent()

    override fun parseComponent(json: Json, mapObject: MapObject): CutSceneComponent?
    {
        // MapObject must contain proper component
        if(!mapObject.properties.containsKey(CutSceneComponent.className)) { return null }

        val jsonStringWithoutBrackets = mapObject.properties[CutSceneComponent.className, TriggerCallbackComponent.defaultJson]
        val cutScene = json.fromJson(CutSceneComponent::class.java, "{$jsonStringWithoutBrackets}")

        return cutScene
    }
}