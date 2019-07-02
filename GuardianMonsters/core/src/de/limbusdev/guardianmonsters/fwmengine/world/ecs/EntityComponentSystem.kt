package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.battle.BattleScreen
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.CameraSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.CharacterSpriteSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.DebuggingSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.InputSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.MovementSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.PathSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.PositionSynchroSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.SpriteSystem
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapDescriptionInfo
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation
import de.limbusdev.guardianmonsters.fwmengine.world.ui.HUD
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen
import de.limbusdev.guardianmonsters.services.Services


/**
 * EntityComponentSystem
 *
 * @author Georg Eckert 2015-11-21
 */

/**
 * Base Game Engine Component. The Entity-Component-System (ECS) updates every [Entity]
 * every single Update Cycle according to the present changes in the game world.
 * @param viewport      screen size
 * @param gameArea      active game level/map
 * @param fromSave      whether to initialize a new game or restore game state from save game
 * @param gameScreen    screen
 * @param sgm           the SaveGameManager
 */
class EntityComponentSystem
(
        viewport: Viewport,
        var gameArea: GameArea,
        fromSave: Boolean,
        gameScreen: WorldScreen,
        private val saveGameManager: SaveGameManager
) {
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val engine          = Engine()
    private val entityFactory   = EntityFactory(engine, gameArea)

    var hero    : Entity
    var hud     : HUD

    val inputProcessor: InputProcessor get() = hud


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        hero = setupHero(fromSave)
        val inventory = Components.inventory.get(hero).inventory
        hud = HUD(BattleScreen(inventory), saveGameManager, hero, engine, gameArea)
        setUpPeople()
        setUpSigns()
        setUpEntitySystems(gameArea, viewport, hud)
    }


    // --------------------------------------------------------------------------------------------- METHODS
    /**
     * Creates the hero instance
     * @param fromSave  whether to create hero or reconstruct from game save
     */
    private fun setupHero(fromSave: Boolean) : Entity
    {
        val hero = entityFactory.createHero(gameArea.startPosition, fromSave)
        val heroPosition = Components.getPositionComponent(hero)
        return hero
    }

    /** Bring people on active map to life */
    private fun setUpPeople()
    {
        for (key in gameArea.mapPeople.keys())
        {
            for (mpi in gameArea.mapPeople.get(key))
            {
                entityFactory.createPerson(mpi, key)
            }
        }
    }

    /** Set up objects with description on the map */
    private fun setUpSigns()
    {
        for (key in gameArea.descriptions.keys())
        {
            for (mdi in gameArea.descriptions.get(key))
            {
                entityFactory.createSign(mdi, key)
            }
        }
    }


    fun setUpEntitySystems(gameArea: GameArea, viewport: Viewport, hud: HUD)
    {
        // Sprite System
        val spriteSystem = SpriteSystem(gameArea.mapRenderer)
        spriteSystem.addedToEngine(engine)
        engine.addSystem(spriteSystem)

        // Input System
        val inputSystem = InputSystem(viewport, hud)
        inputSystem.addedToEngine(engine)
        engine.addSystem(inputSystem)

        // Position Synchronization
        val positionSynchroSystem = PositionSynchroSystem()
        positionSynchroSystem.addedToEngine(engine)
        engine.addSystem(positionSynchroSystem)

        // Character Sprite System
        val characterSpriteSystem = CharacterSpriteSystem()
        characterSpriteSystem.addedToEngine(engine)
        engine.addSystem(characterSpriteSystem)

        // Movement System
        val movementSystem = MovementSystem(this, gameArea.warpPoints, gameArea.healFields)
        movementSystem.addedToEngine(engine)
        engine.addSystem(movementSystem)

        // Camera System
        val cameraSystem = CameraSystem(viewport.camera as OrthographicCamera, gameArea.tiledMap)
        cameraSystem.addedToEngine(engine)
        engine.addSystem(cameraSystem)

        // Path System
        val pathSystem = PathSystem(gameArea)
        pathSystem.addedToEngine(engine)
        engine.addSystem(pathSystem)

        // GameSaveManager
        saveGameManager.addedToEngine(engine)
        engine.addSystem(saveGameManager)

        // Debugging
        val debuggingSystem = DebuggingSystem()
        debuggingSystem.addedToEngine(engine)
        engine.addSystem(debuggingSystem)
    }

    fun deleteGameAreasEntities()
    {
        engine.entities.forEach { if(it !is HeroEntity) { engine.removeEntity(it) } }
    }

    /**
     * Update game world every single game render iteration
     * @param delta time since last update
     */
    fun update(delta: Float)
    {
        engine.update(delta)
        hud.update(delta)
        gameArea.update(delta)
    }

    /**
     * Render ECS stuff like debugger and so on
     * @param batch
     * @param shape
     */
    fun render(batch: Batch, shape: ShapeRenderer)
    {
        if (Constant.DEBUGGING_ON) engine.getSystem(DebuggingSystem::class.java).render(shape)
    }

    /**
     * Change to another game area/map
     * @param mapID         map to load
     * @param startFieldID  start point on new map
     */
    fun changeGameArea(mapID: Int, startFieldID: Int)
    {
        Services.ScreenManager().pushScreen(WorldScreen(mapID, startFieldID, false))
    }

    /**
     * Render Heads Up Display
     */
    fun draw()
    {
        hud.stage.viewport.apply()
        hud.draw()
    }
}
