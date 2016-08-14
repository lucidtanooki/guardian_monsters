package de.limbusdev.guardianmonsters.utils;

import com.badlogic.ashley.core.Family;

import de.limbusdev.guardianmonsters.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.ecs.components.ConversationComponent;
import de.limbusdev.guardianmonsters.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.ecs.components.TitleComponent;


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
