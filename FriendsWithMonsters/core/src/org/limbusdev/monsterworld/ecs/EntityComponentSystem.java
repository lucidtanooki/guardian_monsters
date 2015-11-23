package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.ComponentRetreiver;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.ecs.systems.CharacterSpriteSystem;
import org.limbusdev.monsterworld.ecs.systems.DebuggingSystem;
import org.limbusdev.monsterworld.ecs.systems.InputSystem;
import org.limbusdev.monsterworld.ecs.systems.MovementSystem;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.ecs.systems.PositionSynchroSystem;
import org.limbusdev.monsterworld.ecs.systems.SpriteSystem;
import org.limbusdev.monsterworld.managers.MediaManager;
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
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            MonsterWorld game, Viewport viewport, OutdoorGameArea gameArea
    ) {
        this.media = game.media;
        this.game = game;
        this.gameArea = gameArea;
        this.engine = new Engine();
        this.entityFactory = new EntityFactory(engine, media);
        setUpHero();
        setUpEntitySystems(gameArea, viewport);
    }
    /* ............................................................................... METHODS .. */
    public void setUpHero() {
        Entity hero = entityFactory.createHero(gameArea.startPosition);
        this.heroPosition = ComponentRetreiver.getPositionComponent(hero);
    }

    public void setUpEntitySystems(OutdoorGameArea gameArea, Viewport viewport) {
        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(gameArea.getMapRenderer());
        spriteSystem.addedToEngine(engine);
        engine.addSystem(spriteSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport, gameArea);
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
        game.setScreen(new OutdoorGameWorldScreen(game, mapID, startFieldID));
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return engine.getSystem(InputSystem.class);
    }

    public Vector2 getHeroPosition() {
        return new Vector2(heroPosition.x, heroPosition.y);
    }
}
