package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.ecs.components.CameraComponent;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PathComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.SpriteComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.enums.TextureAtlasType;
import org.limbusdev.monsterworld.geometry.MapObjectInformation;
import org.limbusdev.monsterworld.geometry.MapPersonInformation;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.utils.GlobalSettings;
import org.limbusdev.monsterworld.utils.UnitConverter;

import java.util.ArrayList;

/**
 * Created by georg on 23.11.15.
 */
public class EntityFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private MediaManager media;
    private OutdoorGameArea area;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityFactory(Engine engine, MediaManager media, OutdoorGameArea area) {
        this.engine = engine;
        this.media = media;
        this.area = area;
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
        ColliderComponent collider = new ColliderComponent(position.x, position.y, position
                .width, position.height);
        area.addMovingCollider(collider.collider);
        hero.add(collider);
        engine.addEntity(hero);

        return hero;
    }

    public Entity createSign(MapObjectInformation mapInfo) {
        Entity sign = new Entity();
        sign.add(new ConversationComponent(mapInfo.content));
        sign.add(new TitleComponent(mapInfo.title));
        sign.add(new ColliderComponent(
                mapInfo.x, mapInfo.y ,GlobalSettings.TILE_SIZE, GlobalSettings.TILE_SIZE));
        engine.addEntity(sign);
        return sign;
    }

    public Entity createPerson(MapPersonInformation personInformation) {
        Array<SkyDirection> path = new Array<SkyDirection>();
        if(!(personInformation.path == null || personInformation.path.isEmpty())) {
            String[] pathStr = personInformation.path.split("\\s*,\\s*");
            for (String s : pathStr)
                path.add(SkyDirection.valueOf(s));
        }

        return createPerson(new PositionComponent(personInformation.startPosition.x,
                personInformation.startPosition.y, GlobalSettings.TILE_SIZE, GlobalSettings
                .TILE_SIZE), path, personInformation.moves, personInformation.conversation);
    }

    /**
     * Creates a walking person entity
     * @param startField
     * @return
     */
    public Entity createPerson(PositionComponent startField, Array<SkyDirection> path, boolean
            moves, String conv) {
        Entity person = new Entity();
        PathComponent pathComp = new PathComponent(path, moves);
        pathComp.moving = moves;
        person.add(pathComp);
        person.add(new CharacterSpriteComponent(media.getTextureAtlasType(TextureAtlasType.HERO)));
        PositionComponent position = new PositionComponent(
                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1));
        person.add(position);
        ColliderComponent collider = new ColliderComponent(position.x, position.y, position
                .width, position.height);
        area.addMovingCollider(collider.collider);
        person.add(collider);
        person.add(new ConversationComponent(conv));
        engine.addEntity(person);

        return person;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
