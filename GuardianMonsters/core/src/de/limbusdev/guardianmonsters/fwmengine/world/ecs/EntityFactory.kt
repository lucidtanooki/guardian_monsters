package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedPersonSprite
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.utils.UnitConverter
import de.limbusdev.utils.geometry.IntVec2


/**
 * EntityFactory
 *
 * @author Georg Eckert 2015-11-23
 */
class EntityFactory(private val area: GameArea)
{
    // --------------------------------------------------------------------------------------------- METHODS
    // ............................................................................. Factory Methods
    /**
     * Creates a hero [Entity] and adds it to the [Engine].
     * @return
     */
    fun createHero(startField: IntVec2, startLayer: Int, restoreSave: Boolean): LimbusGameObject
    {
        val hero = CoreSL.world.hero

        // Add Sprite
        val spriteComponent = CharacterSpriteComponent(AnimatedPersonSprite("hero"))
        hero.add(spriteComponent)

        // Input
        val inputComponent = InputComponent()
        hero.add(inputComponent)
        hero.transform.x = startField.x
        hero.transform.y = startField.y
        hero.transform.width = UnitConverter.tilesToPixels(1)
        hero.transform.height = UnitConverter.tilesToPixels(1)
        hero.transform.layer = startLayer

        val transform = hero.transform

        // Movement
        val movement = TileWiseMovementComponent()
        hero.add(movement)
        hero.add(HeroComponent(movement))

        // Collider
        val collider = ColliderComponent(true, transform.x, transform.y, transform.width, transform.height)
        hero.add(collider)

        hero.add(PusherComponent())

        // Game State
        val gameState = SaveGameManager.getCurrentGameState()
        hero.add(SaveGameComponent)
        SaveGameComponent.gameState = gameState
        SaveGameComponent.gameObject = hero
        if (restoreSave)
        {
            hero.transform.x = gameState.gridx * Constant.TILE_SIZE
            hero.transform.y = gameState.gridy * Constant.TILE_SIZE
        }

        // Add Team
        val team = TeamComponent()
        team.team = gameState.team
        hero.add(team)


        // Add GuardoSphere
        val sphereComponent = GuardoSphereComponent()
        if (restoreSave)
        {
            sphereComponent.guardoSphere = gameState.guardoSphere
        }
        hero.add(sphereComponent)


        val items = GuardiansServiceLocator.items

        // Inventory
        val inventory = Inventory()
        inventory.putIntoInventory(items.getItem("bread"))
        inventory.putIntoInventory(items.getItem("bread"))
        inventory.putIntoInventory(items.getItem("bread"))
        inventory.putIntoInventory(items.getItem("bread"))
        inventory.putIntoInventory(items.getItem("potion-blue"))
        inventory.putIntoInventory(items.getItem("potion-blue"))
        inventory.putIntoInventory(items.getItem("potion-blue"))
        inventory.putIntoInventory(items.getItem("angel-tear"))
        inventory.putIntoInventory(items.getItem("sword-wood"))
        inventory.putIntoInventory(items.getItem("claws-wood"))

        val inventoryComp = InventoryComponent(inventory)
        hero.add(inventoryComp)

        hero.addAndRemoveComponentsNow()

        return hero
    }
}
