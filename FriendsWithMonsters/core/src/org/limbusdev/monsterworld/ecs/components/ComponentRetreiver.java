package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

/**
 * Created by georg on 23.11.15.
 */
public class ComponentRetreiver {
    /* ............................................................................ ATTRIBUTES .. */
    public static ComponentMapper<PositionComponent> pm =
            ComponentMapper.getFor(PositionComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static PositionComponent getPositionComponent(Entity entity) throws
            NullPointerException {
        if(pm.has(entity)) return pm.get(entity);
        else throw new NullPointerException("Given entity does not contain a PositionComponent.");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
