package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapDescriptionInfo
import de.limbusdev.guardianmonsters.fwmengine.world.model.MapPersonInformation
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
class EntityFactory(private val engine: Engine, private val area: GameArea)
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

        // Game State
        val gameState = SaveGameManager.getCurrentGameState()
        hero.add(SaveGameComponent(gameState))
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

    fun createPerson(pInfo: MapPersonInformation, layer: Int): Entity
    {
        // Set up path component
        val path = Array<SkyDirection>()
        if (!(pInfo.path == null || pInfo.path.isEmpty()))
        {
            val pathStr = pInfo.path.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            pathStr.forEach { path.add(SkyDirection.valueOf(it)) }
        }

        // TODO
        return Entity()
/*
        // Use second Constructor
        return createPerson(

                Transform(true, pInfo.startPosition.x, pInfo.startPosition.y, Constant.TILE_SIZE, Constant.TILE_SIZE, layer),
                path,
                pInfo.moves,
                pInfo.conversation,
                pInfo.name,
                pInfo.male,
                pInfo.spriteIndex
        )*/
    }

    /**
     * Creates a walking person entity
     * @param startField
     * @param path
     * @param moves
     * @param conversation
     * @return
     */
    private fun createPerson
    (
            startField: Transform,
            path: Array<SkyDirection>,
            moves: Boolean,
            conversation: String,
            name: String,
            male: Boolean,
            spriteIndex: Int
    )
            : Entity
    {
        val person = Entity()

        // Path
        val pathComp = PathComponent(path, moves)
        person.add(pathComp)

        // Sprite
        person.add(CharacterSpriteComponent(AnimatedPersonSprite(male, spriteIndex)))

        // Position
        val transform = Transform(LimbusGameObject())
        transform.x = startField.x
        transform.y = startField.y
        transform.width = UnitConverter.tilesToPixels(1)
        transform.height = UnitConverter.tilesToPixels(1)
        transform.layer = startField.layer


        person.add(transform)

        // Collider
        val collider = ColliderComponent(true, transform.x, transform.y, transform.width, transform.height)
        person.add(collider)

        // Conversation
        person.add(ConversationComponent(conversation, name))
        engine.addEntity(person)

        return person
    }
}
