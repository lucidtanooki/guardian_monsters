package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.viewport.Viewport

import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.battle.BattleScreen
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Camera
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InventoryComponent
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

    private val entityFactory   = EntityFactory(gameArea)

    var hud     : HUD

    val inputProcessor: InputProcessor get() = hud


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        CoreSL.world.add(entityFactory.createHero(gameArea.startPosition, gameArea.startLayer, fromSave))
        CoreSL.world.add(gameArea.gameObject)
        val inventory = CoreSL.world.hero.get<InventoryComponent>()!!.inventory
        hud = HUD(BattleScreen(inventory), saveGameManager, CoreSL.world.hero, gameArea)


        // Camera System
        val cameraComponent = Camera(gameArea.tiledMap)
        CoreSL.world.hero.add(cameraComponent)

        // GameSaveManager
        // TODO saveGameManager.addedToEngine(engine)
    }


    // --------------------------------------------------------------------------------------------- METHODS
    /**
     * Update game world every single game render iteration
     * @param delta time since last update
     */
    fun update(delta: Float)
    {
        hud.update(delta)
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
    fun render()
    {
        hud.stage.viewport.apply()
        hud.draw()
    }
}
