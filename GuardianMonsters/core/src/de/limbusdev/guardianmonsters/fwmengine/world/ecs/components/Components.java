package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

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
public abstract class Components {
    /* ............................................................................ ATTRIBUTES .. */
    public static ComponentMapper<PositionComponent> position =
            ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<ColliderComponent> collision =
            ComponentMapper.getFor(ColliderComponent.class);
    public static ComponentMapper<PathComponent> path =
            ComponentMapper.getFor(PathComponent.class);
    public static ComponentMapper<InputComponent> input =
            ComponentMapper.getFor(InputComponent.class);
    public static ComponentMapper<ConversationComponent> conversation =
            ComponentMapper.getFor(ConversationComponent.class);
    public static ComponentMapper<TitleComponent> title =
            ComponentMapper.getFor(TitleComponent.class);
    public static ComponentMapper<SaveGameComponent> saveGame =
            ComponentMapper.getFor(SaveGameComponent.class);
    public static ComponentMapper<TeamComponent> team =
            ComponentMapper.getFor(TeamComponent.class);
    public static ComponentMapper<CharacterSpriteComponent> characterSprite =
            ComponentMapper.getFor(CharacterSpriteComponent.class);
    public static ComponentMapper<SpriteComponent> sprite =
            ComponentMapper.getFor(SpriteComponent.class);
    public static ComponentMapper<InventoryComponent> inventory =
        ComponentMapper.getFor(InventoryComponent.class);
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
        if(position.has(entity)) return position.get(entity);
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
        if(collision.has(entity)) return collision.get(entity);
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
        if(path.has(entity)) return path.get(entity);
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
        if(input.has(entity)) return input.get(entity);
        else throw new NullPointerException("Given entity does not contain a InputComponent.");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
