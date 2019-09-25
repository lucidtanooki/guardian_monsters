package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.parsers.*
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
    }
}