package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import org.limbusdev.monsterworld.ecs.components.CameraComponent;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.enums.TextureAtlasType;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.utils.UnitConverter;

/**
 * Created by georg on 23.11.15.
 */
public class EntityFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private MediaManager media;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityFactory(Engine engine, MediaManager media) {
        this.engine = engine;
        this.media = media;
    }
    /* ............................................................................... METHODS .. */

    /**
     * Creates a hero {@link Entity} and adds it to the {@link Engine}.
     * @return
     */
    public Entity createHero(PositionComponent startField) {
        Entity hero = new HeroEntity();
        hero.add(new CharacterSpriteComponent(media.getTextureAtlasType(TextureAtlasType.HERO)));
        hero.add(new InputComponent());
        PositionComponent position = new PositionComponent(
                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1));
        hero.add(position);
        hero.add(new CameraComponent());
        engine.addEntity(hero);

        return hero;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
