package de.limbusdev.guardianmonsters.fwmengine.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CameraComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ConversationComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SaveGameComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TitleComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea;
import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.enums.TextureAtlasType;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapObjectInformation;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleFactory;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameState;
import de.limbusdev.guardianmonsters.utils.UnitConverter;


/**
 * Created by georg on 23.11.15.
 */
public class EntityFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private Media media;
    private GameArea area;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityFactory(Engine engine, GameArea area) {
        this.engine = engine;
        this.area = area;
        media = Services.getMedia();
    }
    /* ............................................................................... METHODS .. */

    /**
     * Creates a hero {@link Entity} and adds it to the {@link Engine}.
     * @return
     */
    public Entity createHero(de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent startField, boolean restoreSave) {
        Entity hero = new HeroEntity();

        // Add Sprite
        CharacterSpriteComponent csc = new CharacterSpriteComponent(
                media.getTextureAtlasType(TextureAtlasType.HERO));
        hero.add(csc);

        // Input
        hero.add(new InputComponent());
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent position = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent(
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
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent team = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent();
        team.monsters.put(0,BattleFactory.getInstance().createMonster(1));
        team.monsters.put(1,BattleFactory.getInstance().createMonster(14));
        team.monsters.put(2,BattleFactory.getInstance().createMonster(5));
        if(restoreSave) {
            team.monsters = gameState.team;
        }
        hero.add(team);

        // Mark as Hero
        hero.add(new de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.HeroComponent());

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
                personInformation.name,
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
            moves, String conv, String name, boolean male, int spriteIndex) {

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
        person.add(new ConversationComponent(conv,name));
        engine.addEntity(person);

        return person;
    }
    /* ..................................................................... GETTERS & SETTERS .. */


}
