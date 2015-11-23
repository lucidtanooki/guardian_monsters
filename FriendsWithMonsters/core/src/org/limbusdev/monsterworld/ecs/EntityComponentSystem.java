package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.systems.CharacterSpriteSystem;
import org.limbusdev.monsterworld.ecs.systems.InputSystem;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.ecs.systems.PositionSynchroSystem;
import org.limbusdev.monsterworld.ecs.systems.SpriteSystem;
import org.limbusdev.monsterworld.enums.TextureAtlasType;
import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.utils.UnitConverter;

/**
 * Created by georg on 21.11.15.
 */
public class EntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private MediaManager media;
    private OutdoorGameArea gameArea;

    private Entity hero;
    private PositionComponent heroPosition;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            MonsterWorld game, Viewport viewport, OutdoorGameArea gameArea
    ) {
        this.media = game.media;
        this.engine = new Engine();
        this.gameArea = gameArea;
        setUpHero();
        setUpEntitySystems(gameArea, viewport);
    }
    /* ............................................................................... METHODS .. */
    public void setUpHero() {
        this.hero = new Entity();
        hero.add(new CharacterSpriteComponent(media.getTextureAtlasType(TextureAtlasType.HERO)));
        hero.add(new InputComponent());
        heroPosition = new PositionComponent(
                UnitConverter.metersToPixels(16),
                UnitConverter.metersToPixels(1),
                UnitConverter.metersToPixels(1),
                UnitConverter.metersToPixels(1));
        hero.add(heroPosition);
        engine.addEntity(hero);
    }

    public void setUpEntitySystems(OutdoorGameArea gameArea, Viewport viewport) {
        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(gameArea.getMapRenderer());
        spriteSystem.addedToEngine(engine);
        engine.addSystem(spriteSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport);
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
    }

    public void update(float delta) {
        engine.update(delta);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public InputProcessor getInputProcessor() {
        return engine.getSystem(InputSystem.class);
    }

    public Vector2 getHeroPosition() {
        return new Vector2(heroPosition.x, heroPosition.y);
    }
}
