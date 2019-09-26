package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.utils.Json
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.IComponentParser
import de.limbusdev.guardianmonsters.fwmengine.world.ui.get

class StepOnButtonComponent
(
        var needsPermanentPressure : Boolean = false
)
    : LimbusBehaviour()
{
    companion object
    {
        const val className = "StepOnButtonComponent"
        const val defaultJson = "enabled: true, needsPermanentPressure: false"
    }

    private lateinit var collider   : ColliderComponent
    private lateinit var sprite     : SpriteComponent
    private lateinit var trigger    : BoxTrigger2DComponent

    private var defaultRegionX = 0

    override fun initialize()
    {
        super.initialize()

        collider = gameObject.getOrCreate()
        sprite = gameObject.getOrCreate()
        trigger = gameObject.getOrCreate()
        trigger.onTriggerEntered.add { buttonDown() }
        trigger.onTriggerLeft.add { buttonUp() }

        collider.isTrigger = true

        defaultRegionX = sprite.sprite.regionX
    }

    private fun buttonUp()
    {
        if(!needsPermanentPressure) { return }
        sprite.sprite.regionX = defaultRegionX
        sprite.sprite.regionWidth = 16
    }

    private fun buttonDown()
    {
        sprite.sprite.regionX = defaultRegionX-16
        sprite.sprite.regionWidth = 16
    }



    object Parser : IComponentParser<StepOnButtonComponent>
    {
        override fun createComponent() = StepOnButtonComponent()

        override fun parseComponent(json: Json, mapObject: MapObject): StepOnButtonComponent?
        {
            // MapObject must contain proper component
            if(!mapObject.properties.containsKey(className)) { return null }

            val jsonStringWithoutBrackets = mapObject.properties[className, defaultJson]
            val stepOnButton = json.fromJson(StepOnButtonComponent::class.java, "{$jsonStringWithoutBrackets}")

            return stepOnButton
        }
    }
}