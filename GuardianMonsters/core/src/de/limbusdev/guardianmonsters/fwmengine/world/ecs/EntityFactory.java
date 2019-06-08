package de.limbusdev.guardianmonsters.fwmengine.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CameraComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ConversationComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.GuardoSphereComponent;
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
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapDescriptionInfo;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.items.IItemService;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;
import de.limbusdev.guardianmonsters.utils.UnitConverter;
import de.limbusdev.utils.geometry.IntVec2;


/**
 * Created by georg on 23.11.15.
 */
public class EntityFactory
{
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
    public Entity createHero(PositionComponent startField, boolean restoreSave)
    {
        Entity hero = new HeroEntity();

        // Add Sprite
        CharacterSpriteComponent csc = new CharacterSpriteComponent(new AnimatedPersonSprite("hero"));
        hero.add(csc);

        // Input
        hero.add(new InputComponent());
        PositionComponent position = new PositionComponent(
            startField.getX(),
            startField.getY(),
            UnitConverter.tilesToPixels(1),
            UnitConverter.tilesToPixels(1),
            startField.layer);

        // Position
        position.onGrid = new IntVec2(
                position.getX() / Constant.TILE_SIZE,
                position.getY() / Constant.TILE_SIZE);
        hero.add(position);

        // Camera Component
        hero.add(new CameraComponent());

        // Collider
        ColliderComponent collider = new ColliderComponent(position.getX(), position.getY(), position.getWidth(), position.getHeight());
        area.addMovingCollider(collider.collider, startField.layer);
        hero.add(collider);

        // Game State
        GameState gameState = SaveGameManager.getCurrentGameState();
        hero.add(new SaveGameComponent(gameState));
        if(restoreSave)
        {
            position.setX(gameState.gridx * Constant.TILE_SIZE);
            position.setY(gameState.gridy * Constant.TILE_SIZE);
            position.onGrid = new IntVec2(gameState.gridx, gameState.gridy);
        }

        // Add Team
        AGuardianFactory factory = GuardiansServiceLocator.INSTANCE.getGuardianFactory();
        TeamComponent team = new TeamComponent();
        team.team = gameState.team;
        hero.add(team);


        // Add GuardoSphere
        GuardoSphereComponent sphereComponent = new GuardoSphereComponent();
        if(restoreSave) {
            sphereComponent.guardoSphere = gameState.guardoSphere;
        }
        hero.add(sphereComponent);


        IItemService items = GuardiansServiceLocator.INSTANCE.getItems();

        // Inventory
        Inventory inventory = new Inventory();
        inventory.putIntoInventory(items.getItem("bread"));
        inventory.putIntoInventory(items.getItem("bread"));
        inventory.putIntoInventory(items.getItem("bread"));
        inventory.putIntoInventory(items.getItem("bread"));
        inventory.putIntoInventory(items.getItem("potion-blue"));
        inventory.putIntoInventory(items.getItem("potion-blue"));
        inventory.putIntoInventory(items.getItem("potion-blue"));
        inventory.putIntoInventory(items.getItem("angel-tear"));
        inventory.putIntoInventory(items.getItem("sword-wood"));
        inventory.putIntoInventory(items.getItem("claws-wood"));

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
                mapInfo.x, mapInfo.y, Constant.TILE_SIZE, Constant.TILE_SIZE));
        sign.add(new PositionComponent(mapInfo.x, mapInfo.y,
                Constant.TILE_SIZE, Constant.TILE_SIZE,layer));
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
        return createPerson(new PositionComponent(personInformation.startPosition.getX(),
                personInformation.startPosition.getY(), Constant.TILE_SIZE, Constant
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
            startField.getX(),
            startField.getY(),
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1),
                startField.layer);
        person.add(position);

        // Collider
        ColliderComponent collider = new ColliderComponent(position.getX(), position.getY(), position.getWidth(), position.getHeight());
        area.addMovingCollider(collider.collider, startField.layer);
        person.add(collider);

        // Conversation
        person.add(new ConversationComponent(conv,name));
        engine.addEntity(person);

        return person;
    }
    /* ..................................................................... GETTERS & SETTERS .. */


}
