package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

/**
 * A wrapper class which makes it easier to retrieve {@link com.badlogic.ashley.core.Component}s
 * very fast without the need of creating new {@link ComponentMapper}s in every single
 * {@link com.badlogic.ashley.core.EntitySystem}.
 * Use it without creating instances of it.
 *
 * Created by georg on 23.11.15.
 */
public abstract class ComponentRetriever {
    /* ............................................................................ ATTRIBUTES .. */
    public static ComponentMapper<PositionComponent> posCompMap =
            ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<ColliderComponent> collCompMap =
            ComponentMapper.getFor(ColliderComponent.class);
    public static ComponentMapper<PathComponent> pathCompMap =
            ComponentMapper.getFor(PathComponent.class);
    public static ComponentMapper<InputComponent> inpCompMap =
            ComponentMapper.getFor(InputComponent.class);
    public static ComponentMapper<ConversationComponent> convCompMap =
            ComponentMapper.getFor(ConversationComponent.class);
    public static ComponentMapper<TitleComponent> titleCompMap =
            ComponentMapper.getFor(TitleComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */

    /**
     * Retrieve {@link PositionComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static PositionComponent getPositionComponent(Entity entity) throws
            NullPointerException {
        if(posCompMap.has(entity)) return posCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a PositionComponent.");
    }

    /**
     * Retrieve {@link ColliderComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static ColliderComponent getColliderComponent(Entity entity) throws
            NullPointerException {
        if(collCompMap.has(entity)) return collCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a ColliderComponent.");
    }

    /**
     * Retrieve {@link PathComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static PathComponent getPathComponent(Entity entity) throws
            NullPointerException {
        if(pathCompMap.has(entity)) return pathCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a PathComponent.");
    }

    /**
     * Retrieve {@link InputComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static InputComponent getInputComponent(Entity entity) throws
            NullPointerException {
        if(inpCompMap.has(entity)) return inpCompMap.get(entity);
        else throw new NullPointerException("Given entity does not contain a InputComponent.");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
