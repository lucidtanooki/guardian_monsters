package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour
import kotlin.reflect.KClass

object Behaviours
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
    }
}