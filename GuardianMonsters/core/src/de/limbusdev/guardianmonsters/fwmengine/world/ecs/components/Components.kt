package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.*
import java.lang.Exception
import kotlin.reflect.KClass

object Components
{
    val parsers = mutableMapOf<KClass<out LimbusBehaviour>, IComponentParser<out LimbusBehaviour>>()

    init
    {
        parsers[ColliderComponent::class] = ColliderComponentParser
        parsers[ConversationComponent::class] = ConversationComponentParser
        parsers[CharacterSpriteComponent::class] = CharacterSpriteComponentParser
        parsers[WarpStartComponent::class] = WarpStartComponentParser
        parsers[WarpTargetComponent::class] = WarpTargetComponentParser
        parsers[PathComponent::class] = PathComponentParser
        parsers[InputComponent::class] = InputComponentParser
        parsers[TileWiseMovementComponent::class] = TileWiseMovementComponentParser
        parsers[SpriteComponent::class] = SpriteComponentParser
        parsers[SlidingComponent::class] = SlidingComponent.Parser
        parsers[BoxTrigger2DComponent::class] = BoxTrigger2DComponent.Parser
        parsers[ChangeLayerTriggerCallbackComponent::class] = ChangeLayerTriggerCallbackComponent.Parser
        parsers[RandomBattleAreaComponent::class] = RandomBattleAreaComponent.Parser
        parsers[StepOnButtonComponent::class] = StepOnButtonComponent.Parser
        parsers[DisableGameObjectTriggerCallbackComponent::class] = DisableGameObjectTriggerCallbackComponent.Parser
        parsers[PersonComponent::class] = PersonComponent.Parser
    }


    private const val defaultComponentPackage = "de.limbusdev.guardianmonsters.fwmengine.world.ecs.components."

    fun componentNameToClass(componentName: String) : KClass<out LimbusBehaviour>?
    {
        if (!componentName.contains("Component", false)) { return null }

        var componentType: KClass<out LimbusBehaviour>? = null

        try
        {
            // Components that are part of the engine can be used with simple names
            // Custom components must use their full name, like: com.me.CustomComponent
            val componentClassBasePath = when (componentName.contains(".")) {
                true -> ""
                false -> defaultComponentPackage
            }
            val kClass = Class.forName(componentClassBasePath + componentName).kotlin

            componentType = kClass as KClass<out LimbusBehaviour>
        }
        catch (e: Exception)
        {
            println("Cast not successful for $componentName.")
        }

        return componentType
    }
}