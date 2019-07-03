package de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities

import com.badlogic.ashley.core.Family

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ConversationComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TitleComponent


/**
 * EntityFamilies
 *
 * @author Georg Eckert 2016-01-26
 */
object EntityFamilies
{
    val signs = Family.all(
            TitleComponent::class.java,
            ConversationComponent::class.java,
            PositionComponent::class.java).get()!!

    val living = Family
            .all(ConversationComponent::class.java, ColliderComponent::class.java, PositionComponent::class.java)
            .exclude(TitleComponent::class.java).get()!!
}
