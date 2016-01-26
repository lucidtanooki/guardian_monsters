package org.limbusdev.monsterworld.utils;

import com.badlogic.ashley.core.Family;

import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;

/**
 * Created by georg on 26.01.16.
 */
public class EntityFamilies {
    /* ............................................................................ ATTRIBUTES .. */
    public static Family signs = Family.all(
            TitleComponent.class,
            ConversationComponent.class,
            PositionComponent.class).get();
    public static Family living = Family
            .all(ConversationComponent.class, ColliderComponent.class, PositionComponent.class)
            .exclude(TitleComponent.class).get();
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
