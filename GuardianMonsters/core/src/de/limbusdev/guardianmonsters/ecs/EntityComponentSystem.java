package de.limbusdev.guardianmonsters.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.GuardianMonsters;
import de.limbusdev.guardianmonsters.ecs.components.Components;
import de.limbusdev.guardianmonsters.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.ecs.entities.HeroEntity;
import de.limbusdev.guardianmonsters.ecs.systems.CameraSystem;
import de.limbusdev.guardianmonsters.ecs.systems.CharacterSpriteSystem;
import de.limbusdev.guardianmonsters.ecs.systems.DebuggingSystem;
import de.limbusdev.guardianmonsters.ecs.systems.GameArea;
import de.limbusdev.guardianmonsters.ecs.systems.InputSystem;
import de.limbusdev.guardianmonsters.ecs.systems.MovementSystem;
import de.limbusdev.guardianmonsters.ecs.systems.PathSystem;
import de.limbusdev.guardianmonsters.ecs.systems.PositionSynchroSystem;
import de.limbusdev.guardianmonsters.ecs.systems.SpriteSystem;
import de.limbusdev.guardianmonsters.geometry.MapObjectInformation;
import de.limbusdev.guardianmonsters.geometry.MapPersonInformation;
import de.limbusdev.guardianmonsters.managers.MediaManager;
import de.limbusdev.guardianmonsters.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.screens.BattleScreen;
import de.limbusdev.guardianmonsters.screens.HUD;
import de.limbusdev.guardianmonsters.screens.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Created by georg on 21.11.15.
 */
public class EntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private MediaManager media;
    private EntityFactory entityFactory;
    private PositionComponent heroPosition;
    private GuardianMonsters game;
    public GameArea gameArea;
    public SaveGameManager saveGameManager;
    public Entity hero;
    public HUD hud;
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Base Game Engine Component. The Entity-Component-System (ECS) updates every {@link Entity}
     * every single Update Cycle according to the present changes in the game world.
     * @param game          game instance
     * @param viewport      screen size
     * @param gameArea      active game level/map
     * @param fromSave      whether to init a new game or restore game state from save game
     * @param gameScreen    screen
     * @param sgm           the SaveGameManager
     */
    public EntityComponentSystem(
            GuardianMonsters game, Viewport viewport, GameArea gameArea, boolean
            fromSave, OutdoorGameWorldScreen gameScreen, SaveGameManager sgm
    ) {

        this.media = game.media;
        this.game = game;
        this.gameArea = gameArea;
        this.engine = new Engine();
        this.entityFactory = new EntityFactory(engine, media, gameArea);
        setUpHero(fromSave);
        this.hud = new HUD(new BattleScreen(game.media, gameScreen, game),game,sgm, hero, media,
                engine);
        setUpPeople();
        setUpSigns();
        setUpEntitySystems(gameArea, viewport, hud);
    }

    /* ............................................................................... METHODS .. */

    /**
     * Creates the hero instance
     * @param fromSave  whether to create hero or reconstruct from game save
     */
    public void setUpHero(boolean fromSave) {
        Entity hero = entityFactory.createHero(gameArea.startPosition, fromSave);
        this.heroPosition = Components.getPositionComponent(hero);
        this.hero = hero;
    }

    /**
     * Bring people on active map to live
     */
    public void setUpPeople() {
        for(MapPersonInformation mpi : gameArea.getMapPeople())
            this.entityFactory.createPerson(mpi);
    }

    /**
     * Set up objects with description on the map
     */
    public void setUpSigns() {
        for(MapObjectInformation moi : gameArea.getMapSigns())
            this.entityFactory.createSign(moi);
    }


    public void setUpEntitySystems(GameArea gameArea, Viewport viewport, HUD hud) {
        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(gameArea.getMapRenderer());
        spriteSystem.addedToEngine(engine);
        engine.addSystem(spriteSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport, hud);
        inputSystem.addedToEngine(engine);
        engine.addSystem(inputSystem);

        // Position Synchronization
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.addSystem(positionSynchroSystem);

        // Character Sprite System
        CharacterSpriteSystem characterSpriteSystem = new CharacterSpriteSystem();
        characterSpriteSystem.addedToEngine(engine);
        engine.addSystem(characterSpriteSystem);

        // Movement System
        MovementSystem movementSystem = new MovementSystem(this, gameArea.getWarpPoints(), gameArea.getHealFields());
        movementSystem.addedToEngine(engine);
        engine.addSystem(movementSystem);

        // Camera System
        CameraSystem cameraSystem = new CameraSystem((OrthographicCamera)viewport.getCamera(),
                gameArea.getTiledMap());
        cameraSystem.addedToEngine(engine);
        engine.addSystem(cameraSystem);

        // Path System
        PathSystem pathSystem = new PathSystem(gameArea);
        pathSystem.addedToEngine(engine);
        engine.addSystem(pathSystem);

        // GameSaveManager
        this.saveGameManager = hud.saveGameManager;
        this.saveGameManager.addedToEngine(engine);
        engine.addSystem(this.saveGameManager);

        // Debugging
        DebuggingSystem debuggingSystem = new DebuggingSystem();
        debuggingSystem.addedToEngine(engine);
        engine.addSystem(debuggingSystem);
    }

    public void deleteGameAreasEntities() {
        for(Entity e : engine.getEntities())
            if(!(e instanceof HeroEntity))
                engine.removeEntity(e);
    }

    /**
     * Update game world every single game render iteration
     * @param delta time since last update
     */
    public void update(float delta) {
        engine.update(delta);
        hud.update(delta);
        gameArea.update(delta);
    }

    /**
     * Render ECS stuff like debugger and so on
     * @param batch
     * @param shape
     */
    public void render(Batch batch, ShapeRenderer shape) {
        if(GS.DEBUGGING_ON) engine.getSystem(DebuggingSystem.class).render(shape);
    }

    /**
     * Change to another game area/map
     * @param mapID         map to load
     * @param startFieldID  start point on new map
     */
    public void changeGameArea(int mapID, int startFieldID) {
        game.pushScreen(new OutdoorGameWorldScreen(game, mapID, startFieldID, false));
    }

    /**
     * Render Heads Up Display
     */
    public void draw() {
        hud.stage.getViewport().apply();
        hud.draw();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return hud;
    }

}