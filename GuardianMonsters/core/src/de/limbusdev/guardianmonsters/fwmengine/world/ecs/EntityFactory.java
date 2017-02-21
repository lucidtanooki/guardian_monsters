package de.limbusdev.guardianmonsters.fwmengine.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CameraComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ConversationComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.HeroComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InventoryComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SaveGameComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TitleComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea;
import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapDescriptionInfo;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleFactory;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.ItemInfo;
import de.limbusdev.guardianmonsters.utils.GS;
import de.limbusdev.guardianmonsters.utils.GameState;
import de.limbusdev.guardianmonsters.utils.UnitConverter;


/**
 * Created by georg on 23.11.15.
 */
public class EntityFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private GameArea area;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityFactory(Engine engine, GameArea area) {
        this.engine = engine;
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
        CharacterSpriteComponent csc = new CharacterSpriteComponent(new AnimatedPersonSprite("hero"));
        hero.add(csc);

        // Input
        hero.add(new InputComponent());
        PositionComponent position = new PositionComponent(
            startField.x,
            startField.y,
            UnitConverter.tilesToPixels(1),
            UnitConverter.tilesToPixels(1),
            startField.layer);

        // Position
        position.onGrid = new IntVec2(
                position.x/ GS.TILE_SIZE,
                position.y/ GS.TILE_SIZE);
        hero.add(position);

        // Camera Component
        hero.add(new CameraComponent());

        // Collider
        ColliderComponent collider = new ColliderComponent(position.x, position.y, position
                .width, position.height);
        area.addMovingCollider(collider.collider, startField.layer);
        hero.add(collider);

        // Game State
        GameState gameState = SaveGameManager.loadSaveGame();
        hero.add(new SaveGameComponent(gameState));
        if(restoreSave) {
            position.x = gameState.x;
            position.y = gameState.y;
            position.onGrid = new IntVec2(gameState.gridx, gameState.gridy);
        }

        // Add Team
        TeamComponent team = new TeamComponent();
        team.monsters.put(0,BattleFactory.getInstance().createMonster(3));
        team.monsters.put(1,BattleFactory.getInstance().createMonster(14));
        team.monsters.put(2,BattleFactory.getInstance().createMonster(5));
        if(restoreSave) {
            team.monsters = gameState.team;
        }
        hero.add(team);

        // Inventory
        Inventory inventory = new Inventory();
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("bread"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("potion-blue"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("angel-tear"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-wood"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("claws-wood"));

        InventoryComponent inventoryComp = new InventoryComponent(inventory);
        hero.add(inventoryComp);

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
    public Entity createSign(MapDescriptionInfo mapInfo, int layer) {
        Entity sign = new Entity();
        sign.add(new ConversationComponent(mapInfo.content));
        sign.add(new TitleComponent(mapInfo.title));
        sign.add(new ColliderComponent(
                mapInfo.x, mapInfo.y, GS.TILE_SIZE, GS.TILE_SIZE));
        sign.add(new PositionComponent(mapInfo.x, mapInfo.y,
                GS.TILE_SIZE, GS.TILE_SIZE,layer));
        engine.addEntity(sign);
        return sign;
    }

    public Entity createPerson(MapPersonInformation personInformation, int layer) {

        // Set up path component
        Array<SkyDirection> path = new Array<>();
        if(!(personInformation.path == null || personInformation.path.isEmpty())) {
            String[] pathStr = personInformation.path.split("\\s*,\\s*");
            for (String s : pathStr)
                path.add(SkyDirection.valueOf(s));
        }

        // Use second Constructor
        return createPerson(new PositionComponent(personInformation.startPosition.x,
                personInformation.startPosition.y, GS.TILE_SIZE, GS
                .TILE_SIZE, layer), path, personInformation.moves, personInformation.conversation,
                personInformation.name,
                personInformation.male, personInformation.spriteIndex
        );
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
        person.add(new CharacterSpriteComponent(new AnimatedPersonSprite(male,spriteIndex)));

        // Position
        PositionComponent position = new PositionComponent(
                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1),
                startField.layer);
        person.add(position);

        // Collider
        ColliderComponent collider = new ColliderComponent(position.x, position.y, position
                .width, position.height);
        area.addMovingCollider(collider.collider, startField.layer);
        person.add(collider);

        // Conversation
        person.add(new ConversationComponent(conv,name));
        engine.addEntity(person);

        return person;
    }
    /* ..................................................................... GETTERS & SETTERS .. */


}
