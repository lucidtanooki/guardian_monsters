package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport

import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.battle.BattleScreen
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CameraComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InventoryComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.SpriteSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ui.HUD
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen
import de.limbusdev.guardianmonsters.services.Services


/**
 * TODO Quest System
 *
 * Easy start:
 *
 * + people and objects can have a trigger ID
 * + this trigger ID is then entered into the quest system
 * + trigger dependent stuff can check the quest system if a trigger ID is already achieved
 * + after interaction with a triggering object, the map can be reloaded to apply changes
 */


/**
 * EntityComponentSystem
 *
 * @author Georg Eckert 2015-11-21
 */

/**
 * Base Game Engine Component. The Entity-Component-System (ECS) updates every [Entity]
 * every single Update Cycle according to the present changes in the game world.
 *
 * @param viewport          screen size
 * @param gameArea          active game level/map
 * @param fromSave          whether to initialize a new game or restore game state from save game
 * @param gameScreen        screen
 * @param saveGameManager   the SaveGameManager
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

    var hud     : HUD

    val inputProcessor: InputProcessor get() = hud


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        CoreSL.world.add(setupHero(fromSave))
        val inventory = CoreSL.world.hero.get<InventoryComponent>()!!.inventory
        hud = HUD(BattleScreen(inventory), saveGameManager, CoreSL.world.hero, gameArea)
        setUpEntitySystems(gameArea, viewport, hud)
    }


    // --------------------------------------------------------------------------------------------- METHODS
    /**
     * Creates the hero instance
     * @param fromSave  whether to create hero or reconstruct from game save
     */
    private fun setupHero(fromSave: Boolean) : LimbusGameObject
    {
        return entityFactory.createHero(gameArea.startPosition, gameArea.startLayer, fromSave)
    }

    private fun setUpEntitySystems(gameArea: GameArea, viewport: Viewport, hud: HUD)
    {
        // Sprite System
        val spriteSystem = SpriteSystem(gameArea.mapRenderer)
        spriteSystem.addedToEngine(engine)
        engine.addSystem(spriteSystem)

        // Camera System
        val cameraComponent = CameraComponent(viewport.camera as OrthographicCamera, gameArea.tiledMap)
        CoreSL.world.hero.add(cameraComponent)

        // GameSaveManager
        saveGameManager.addedToEngine(engine)
        engine.addSystem(saveGameManager)
    }

    /**
     * Update game world every single game render iteration
     * @param delta time since last update
     */
    fun update(delta: Float)
    {
        CoreSL.world.update(delta)
        engine.update(delta)
        hud.update(delta)
        gameArea.update(delta)
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
