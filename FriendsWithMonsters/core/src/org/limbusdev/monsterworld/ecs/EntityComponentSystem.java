package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.ComponentRetriever;
import org.limbusdev.monsterworld.ecs.components.MonsterComponents.MonsterStatusComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.ecs.entities.MonsterEntity;
import org.limbusdev.monsterworld.ecs.systems.CameraSystem;
import org.limbusdev.monsterworld.ecs.systems.CharacterSpriteSystem;
import org.limbusdev.monsterworld.ecs.systems.DebuggingSystem;
import org.limbusdev.monsterworld.ecs.systems.InputSystem;
import org.limbusdev.monsterworld.ecs.systems.MovementSystem;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.ecs.systems.PathSystem;
import org.limbusdev.monsterworld.ecs.systems.PositionSynchroSystem;
import org.limbusdev.monsterworld.ecs.systems.SpriteSystem;
import org.limbusdev.monsterworld.geometry.MapObjectInformation;
import org.limbusdev.monsterworld.geometry.MapPersonInformation;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.managers.SaveGameManager;
import org.limbusdev.monsterworld.screens.BattleScreen;
import org.limbusdev.monsterworld.screens.HUD;
import org.limbusdev.monsterworld.screens.OutdoorGameWorldScreen;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 21.11.15.
 */
public class EntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private MediaManager media;
    private EntityFactory entityFactory;
    private PositionComponent heroPosition;
    private MonsterWorld game;
    private OutdoorGameArea gameArea;
    public SaveGameManager saveGameManager;
    public Entity hero;
    public HUD hud;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            MonsterWorld game, Viewport viewport, OutdoorGameArea gameArea, boolean
            fromSave, OutdoorGameWorldScreen gameScreen, SaveGameManager sgm
    ) {
        this.media = game.media;
        this.game = game;
        this.gameArea = gameArea;
        this.engine = new Engine();
        this.entityFactory = new EntityFactory(engine, media, gameArea);
        setUpHero(fromSave);
        this.hud = new HUD(new BattleScreen(game.media, gameScreen, game),game,sgm, hero);
        setUpPeople();
        setUpSigns();
        setUpEntitySystems(gameArea, viewport, hud);
        setUpPartnerMonster();
    }
    /* ............................................................................... METHODS .. */
    public void setUpHero(boolean fromSave) {
        Entity hero = entityFactory.createHero(gameArea.startPosition, fromSave);
        this.heroPosition = ComponentRetriever.getPositionComponent(hero);
        this.hero = hero;
    }

    public void setUpPartnerMonster() {
        Entity partnerMonster = new MonsterEntity();
        partnerMonster.add(new MonsterStatusComponent());
        this.engine.addEntity(partnerMonster);
        // TODO add monster in BattleHUD
    }

    public void setUpPeople() {
        for(MapPersonInformation mpi : gameArea.getMapPeople())
            this.entityFactory.createPerson(mpi);
    }

    public void setUpSigns() {
        for(MapObjectInformation moi : gameArea.getMapSigns())
            this.entityFactory.createSign(moi);
    }

    public void setUpEntitySystems(OutdoorGameArea gameArea, Viewport viewport, HUD hud) {
        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(gameArea.getMapRenderer());
        spriteSystem.addedToEngine(engine);
        engine.addSystem(spriteSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport, gameArea, hud, hero);
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
        MovementSystem movementSystem = new MovementSystem(this, gameArea.getWarpPoints());
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

    public void update(float delta) {
        engine.update(delta);
    }

    public void render(Batch batch, ShapeRenderer shape) {
        if(GlobalSettings.DEBUGGING_ON) engine.getSystem(DebuggingSystem.class).render(shape);
    }

    public void changeGameArea(int mapID, int startFieldID) {
        game.setScreen(new OutdoorGameWorldScreen(game, mapID, startFieldID, false));
    }

    public void draw() {
        hud.stage.getViewport().apply();
        hud.draw();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return engine.getSystem(InputSystem.class);
    }

    public Vector2 getHeroPosition() {
        return new Vector2(heroPosition.x, heroPosition.y);
    }
}
