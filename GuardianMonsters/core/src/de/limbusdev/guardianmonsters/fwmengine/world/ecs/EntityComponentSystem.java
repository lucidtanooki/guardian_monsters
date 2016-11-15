package de.limbusdev.guardianmonsters.fwmengine.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.fwmengine.world.model.MapObjectInformation;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.BattleScreen;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.HUD;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.OutdoorGameWorldScreen;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Created by georg on 21.11.15.
 */
public class EntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private Media media;
    private EntityFactory entityFactory;
    private de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent heroPosition;
    public de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea gameArea;
    public SaveGameManager saveGameManager;
    public Entity hero;
    public HUD hud;
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Base Game Engine Component. The Entity-Component-System (ECS) updates every {@link Entity}
     * every single Update Cycle according to the present changes in the game world.
     * @param viewport      screen size
     * @param gameArea      active game level/map
     * @param fromSave      whether to init a new game or restore game state from save game
     * @param gameScreen    screen
     * @param sgm           the SaveGameManager
     */
    public EntityComponentSystem(Viewport viewport, de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea gameArea, boolean
            fromSave, OutdoorGameWorldScreen gameScreen, SaveGameManager sgm
    ) {

        media = Services.getMedia();
        this.gameArea = gameArea;
        this.engine = new Engine();
        this.entityFactory = new EntityFactory(engine, gameArea);
        setUpHero(fromSave);
        this.hud = new HUD(new BattleScreen(),sgm, hero, engine);
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
        this.heroPosition = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.getPositionComponent(hero);
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


    public void setUpEntitySystems(de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea gameArea, Viewport viewport, HUD hud) {
        // Sprite System
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.SpriteSystem spriteSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.SpriteSystem(gameArea.getMapRenderer());
        spriteSystem.addedToEngine(engine);
        engine.addSystem(spriteSystem);

        // Input System
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.InputSystem inputSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.InputSystem(viewport, hud);
        inputSystem.addedToEngine(engine);
        engine.addSystem(inputSystem);

        // Position Synchronization
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.PositionSynchroSystem positionSynchroSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.addSystem(positionSynchroSystem);

        // Character Sprite System
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.CharacterSpriteSystem characterSpriteSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.CharacterSpriteSystem();
        characterSpriteSystem.addedToEngine(engine);
        engine.addSystem(characterSpriteSystem);

        // Movement System
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.MovementSystem movementSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.MovementSystem(this, gameArea.getWarpPoints(), gameArea.getHealFields());
        movementSystem.addedToEngine(engine);
        engine.addSystem(movementSystem);

        // Camera System
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.CameraSystem cameraSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.CameraSystem((OrthographicCamera)viewport.getCamera(),
                gameArea.getTiledMap());
        cameraSystem.addedToEngine(engine);
        engine.addSystem(cameraSystem);

        // Path System
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.PathSystem pathSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.PathSystem(gameArea);
        pathSystem.addedToEngine(engine);
        engine.addSystem(pathSystem);

        // GameSaveManager
        this.saveGameManager = hud.saveGameManager;
        this.saveGameManager.addedToEngine(engine);
        engine.addSystem(this.saveGameManager);

        // Debugging
        de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.DebuggingSystem debuggingSystem = new de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.DebuggingSystem();
        debuggingSystem.addedToEngine(engine);
        engine.addSystem(debuggingSystem);
    }

    public void deleteGameAreasEntities() {
        for(Entity e : engine.getEntities())
            if(!(e instanceof de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity))
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
        if(GS.DEBUGGING_ON) engine.getSystem(de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.DebuggingSystem.class).render(shape);
    }

    /**
     * Change to another game area/map
     * @param mapID         map to load
     * @param startFieldID  start point on new map
     */
    public void changeGameArea(int mapID, int startFieldID) {
        Services.getScreenManager().pushScreen(
            new OutdoorGameWorldScreen(mapID, startFieldID, false));
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
