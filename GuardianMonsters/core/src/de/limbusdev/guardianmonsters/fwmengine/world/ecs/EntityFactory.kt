package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.CharacterSpriteComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ConversationComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.GuardoSphereComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.HeroComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InventoryComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SaveGameComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TitleComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity
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
    fun createHero(startField: PositionComponent, restoreSave: Boolean): Entity
    {
        val hero = HeroEntity()

        // Add Sprite
        hero.add(CharacterSpriteComponent(AnimatedPersonSprite("hero")))

        // Input
        hero.add(InputComponent())
        val position = PositionComponent(

                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1),
                startField.layer
        )

        // Position
        position.onGrid = IntVec2(

                position.x / Constant.TILE_SIZE,
                position.y / Constant.TILE_SIZE

        )
        hero.add(position)

        // Collider
        val collider = ColliderComponent(position.x, position.y, position.width, position.height)
        area.addDynamicCollider(collider.collider, startField.layer)
        hero.add(collider)

        // Game State
        val gameState = SaveGameManager.getCurrentGameState()
        hero.add(SaveGameComponent(gameState))
        if (restoreSave)
        {
            position.x = gameState.gridx * Constant.TILE_SIZE
            position.y = gameState.gridy * Constant.TILE_SIZE
            position.onGrid = IntVec2(gameState.gridx, gameState.gridy)
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

        // Mark as hero
        hero.add(HeroComponent())

        engine.addEntity(hero)

        World.hero.add(position)

        return hero
    }

    /**
     * Creates an entity with text content for objects which are readable by the player (signs,
     * book shelves, and so on)
     * @param mapInfo
     * @return
     */
    fun createSign(mapInfo: MapDescriptionInfo, layer: Int): Entity
    {
        val sign = Entity()
        sign.add(ConversationComponent(mapInfo.content))
        sign.add(TitleComponent(mapInfo.title))
        sign.add(ColliderComponent(mapInfo.x, mapInfo.y, Constant.TILE_SIZE, Constant.TILE_SIZE))
        sign.add(PositionComponent(mapInfo.x, mapInfo.y, Constant.TILE_SIZE, Constant.TILE_SIZE, layer))
        engine.addEntity(sign)
        return sign
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

        // Use second Constructor
        return createPerson(

                PositionComponent(pInfo.startPosition.x, pInfo.startPosition.y, Constant.TILE_SIZE, Constant.TILE_SIZE, layer),
                path,
                pInfo.moves,
                pInfo.conversation,
                pInfo.name,
                pInfo.male,
                pInfo.spriteIndex
        )
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
            startField: PositionComponent,
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
        pathComp.moving = moves
        person.add(pathComp)

        // Sprite
        person.add(CharacterSpriteComponent(AnimatedPersonSprite(male, spriteIndex)))

        // Position
        val position = PositionComponent(

                startField.x,
                startField.y,
                UnitConverter.tilesToPixels(1),
                UnitConverter.tilesToPixels(1),
                startField.layer
        )

        person.add(position)

        // Collider
        val collider = ColliderComponent(position.x, position.y, position.width, position.height)
        area.addDynamicCollider(collider.collider, startField.layer)
        person.add(collider)

        // Conversation
        person.add(ConversationComponent(conversation, name))
        engine.addEntity(person)

        return person
    }
}
