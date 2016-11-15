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
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent> collision =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent.class);
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent> path =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent.class);
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent> input =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent.class);
    public static ComponentMapper<ConversationComponent> conversation =
            ComponentMapper.getFor(ConversationComponent.class);
    public static ComponentMapper<TitleComponent> title =
            ComponentMapper.getFor(TitleComponent.class);
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SaveGameComponent> saveGame =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SaveGameComponent.class);
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent> team =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent.class);
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent> characterSprite =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent.class);
    public static ComponentMapper<de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SpriteComponent> sprite =
            ComponentMapper.getFor(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SpriteComponent.class);
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
     * Retrieve {@link de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent getColliderComponent(Entity entity) throws
            NullPointerException {
        if(collision.has(entity)) return collision.get(entity);
        else throw new NullPointerException("Given entity does not contain a ColliderComponent.");
    }

    /**
     * Retrieve {@link de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent getPathComponent(Entity entity) throws
            NullPointerException {
        if(path.has(entity)) return path.get(entity);
        else throw new NullPointerException("Given entity does not contain a PathComponent.");
    }

    /**
     * Retrieve {@link de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent} from an {@link Entity} if it has one.
     * @param entity    which the wanted component belongs to
     * @return          the wanted component
     * @throws NullPointerException if the given entity does not contain such a component
     */
    public static de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent getInputComponent(Entity entity) throws
            NullPointerException {
        if(input.has(entity)) return input.get(entity);
        else throw new NullPointerException("Given entity does not contain a InputComponent.");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
