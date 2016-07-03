package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.ecs.components.CameraComponent;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.HeroComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PathComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.SaveGameComponent;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.ecs.systems.GameArea;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.enums.TextureAtlasType;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.geometry.MapObjectInformation;
import org.limbusdev.monsterworld.geometry.MapPersonInformation;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.managers.SaveGameManager;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.utils.GameState;
import org.limbusdev.monsterworld.utils.GS;
import org.limbusdev.monsterworld.utils.UnitConverter;


/**
 * Created by georg on 23.11.15.
 */
public class EntityFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private MediaManager media;
    private GameArea area;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityFactory(Engine engine, MediaManager media, GameArea area) {
        this.engine = engine;
        this.media = media;
        this.area = area;
    }
    /* ............................................................................... METHODS .. */

    /**
     * Creates a hero {@link Entity} and adds it to the {@link Engine}.
     * @return
     */
    public Entity createHero(PositionComponent startField, boolean restoreSave) {
        Entity hero = new HeroEntity();

        // Add Sprite
        CharacterSpriteComponent csc = new CharacterSpriteComponent(
                media.getTextureAtlasType(TextureAtlasType.HERO));
        hero.add(csc);

        // Input
        hero.add(new InputComponent());
        PositionComponent position = new PositionComponent(
                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1));

        // Position
        position.onGrid = new IntVector2(
                position.x/ GS.TILE_SIZE,
                position.y/ GS.TILE_SIZE);
        hero.add(position);

        // Camera Component
        hero.add(new CameraComponent());

        // Collider
        ColliderComponent collider = new ColliderComponent(position.x, position.y, position
                .width, position.height);
        area.addMovingCollider(collider.collider);
        hero.add(collider);

        // Game State
        GameState gameState = SaveGameManager.loadSaveGame();
        hero.add(new SaveGameComponent(gameState));
        if(restoreSave) {
            position.x = gameState.x;
            position.y = gameState.y;
            position.onGrid = new IntVector2(gameState.gridx, gameState.gridy);
        }

        // Add Team
        TeamComponent team = new TeamComponent();
        team.monsters.add(BattleFactory.getInstance().createMonster(1));
        team.monsters.add(BattleFactory.getInstance().createMonster(14));
        team.monsters.add(BattleFactory.getInstance().createMonster(5));
        if(restoreSave) {
            team.monsters = gameState.team;
        }
        hero.add(team);

        // Mark as Hero
        hero.add(new HeroComponent());

        engine.addEntity(hero);

        return hero;
    }

    /**
     * Creates an entity with text content for objects which are readable by the player (signs,
     * book shelves, and so on)
     * @param mapInfo
     * @return
     */
    public Entity createSign(MapObjectInformation mapInfo) {
        Entity sign = new Entity();
        sign.add(new ConversationComponent(mapInfo.content));
        sign.add(new TitleComponent(mapInfo.title));
        sign.add(new ColliderComponent(
                mapInfo.x, mapInfo.y, GS.TILE_SIZE, GS.TILE_SIZE));
        sign.add(new PositionComponent(mapInfo.x, mapInfo.y,
                GS.TILE_SIZE, GS.TILE_SIZE));
        engine.addEntity(sign);
        return sign;
    }

    public Entity createPerson(MapPersonInformation personInformation) {

        // Set up path component
        Array<SkyDirection> path = new Array<SkyDirection>();
        if(!(personInformation.path == null || personInformation.path.isEmpty())) {
            String[] pathStr = personInformation.path.split("\\s*,\\s*");
            for (String s : pathStr)
                path.add(SkyDirection.valueOf(s));
        }

        // Use second Constructor
        return createPerson(new PositionComponent(personInformation.startPosition.x,
                personInformation.startPosition.y, GS.TILE_SIZE, GS
                .TILE_SIZE), path, personInformation.moves, personInformation.conversation,
                personInformation.male, personInformation.spriteIndex);
    }

    /**
     * Creates a walking person entity
     * @param startField
     * @param path
     * @param moves
     * @param conv
     * @return
     */
    private Entity createPerson(PositionComponent startField, Array<SkyDirection> path, boolean
            moves, String conv, boolean male, int spriteIndex) {

        Entity person = new Entity();

        // Path
        PathComponent pathComp = new PathComponent(path, moves);
        pathComp.moving = moves;
        person.add(pathComp);

        // Sprite
        person.add(new CharacterSpriteComponent(media.getPersonTextureAtlas(male,spriteIndex)));

        // Position
        PositionComponent position = new PositionComponent(
                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1));
        person.add(position);

        // Collider
        ColliderComponent collider = new ColliderComponent(position.x, position.y, position
                .width, position.height);
        area.addMovingCollider(collider.collider);
        person.add(collider);

        // Conversation
        person.add(new ConversationComponent(conv));
        engine.addEntity(person);

        return person;
    }
    /* ..................................................................... GETTERS & SETTERS .. */


}
