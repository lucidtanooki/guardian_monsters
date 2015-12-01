package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

/**
 * Created by georg on 23.11.15.
 */
public class ComponentRetreiver {
    /* ............................................................................ ATTRIBUTES .. */
    public static ComponentMapper<PositionComponent> posCompMap =
            ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<ColliderComponent> collCompMap =
            ComponentMapper.getFor(ColliderComponent.class);
    public static ComponentMapper<PathComponent> pathCompMap =
            ComponentMapper.getFor(PathComponent.class);
    public static ComponentMapper<InputComponent> inpCompMap =
            ComponentMapper.getFor(InputComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static PositionComponent getPositionComponent(Entity entity) throws
            NullPointerException {
        if(posCompMap.has(entity)) return posCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a PositionComponent.");
    }

    public static ColliderComponent getColliderComponent(Entity entity) throws
            NullPointerException {
        if(collCompMap.has(entity)) return collCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a ColliderComponent.");
    }

    public static PathComponent getPathComponent(Entity entity) throws
            NullPointerException {
        if(pathCompMap.has(entity)) return pathCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a PathComponent.");
    }

    public static InputComponent getInputComponent(Entity entity) throws
            NullPointerException {
        if(inpCompMap.has(entity)) return inpCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a InputComponent.");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
