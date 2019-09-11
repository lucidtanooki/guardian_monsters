package de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities

import com.badlogic.ashley.core.Family

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ConversationComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TransformComponent
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
            TransformComponent::class.java).get()!!

    val living = Family
            .all(ConversationComponent::class.java, ColliderComponent::class.java, TransformComponent::class.java)
            .exclude(TitleComponent::class.java).get()!!
}
