package org.limbusdev.monsterworld.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.MonsterWorld;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.DynamicBodyComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.systems.CharacterSpriteSystem;
import org.limbusdev.monsterworld.ecs.systems.InputSystem;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.ecs.systems.PositionSynchroSystem;
import org.limbusdev.monsterworld.ecs.systems.SpriteSystem;
import org.limbusdev.monsterworld.enums.TextureAtlasType;
import org.limbusdev.monsterworld.managers.MediaManager;

/**
 * Created by georg on 21.11.15.
 */
public class EntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private World world;
    private MediaManager media;
    private OutdoorGameArea gameArea;

    private Entity mainHero;
    private DynamicBodyComponent heroBody;
    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            MonsterWorld game, World world, Viewport viewport, OutdoorGameArea gameArea
    ) {
        this.media = game.media;
        this.world = world;
        this.engine = new Engine();
        this.gameArea = gameArea;
        setUpHero();
        setUpEntitySystems(gameArea, viewport);
    }
    /* ............................................................................... METHODS .. */
    public void setUpHero() {
        this.mainHero = new Entity();
        mainHero.add(new CharacterSpriteComponent(media.getTextureAtlasType(TextureAtlasType.HERO)));
        this.heroBody = new DynamicBodyComponent(world, new Vector2(16,1));
        mainHero.add(heroBody);
        mainHero.add(new InputComponent());
        engine.addEntity(mainHero);
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
        return this.heroBody.dynamicBody.getPosition();
    }
}
