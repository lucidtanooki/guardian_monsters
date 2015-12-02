package org.limbusdev.monsterworld.ecs.entities;

import com.badlogic.ashley.core.Entity;

/**
 * There is nothing special about this class. Actually it's just the same like {@link Entity},
 * but with entity instanceof HeroEntity it is pretty simple to find out whether an entity is
 * the one of your hero.
 *
 * Created by georg on 23.11.15.
 */
public class HeroEntity extends Entity {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    public HeroEntity() {
        super();
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
