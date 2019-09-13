package de.limbusdev.guardianmonsters.fwmengine.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.GuardoSphereComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Transform
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.SaveGameComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems.GameArea
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.model.gamestate.GameState
import de.limbusdev.guardianmonsters.model.gamestate.SerializableGameState
import de.limbusdev.guardianmonsters.utils.getComponent
import de.limbusdev.utils.logDebug


/**
 * @author Georg Eckert 2017
 */
class SaveGameManager : EntitySystem
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private lateinit var savableEntities: ImmutableArray<Entity>
    private lateinit var gameArea: GameArea


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    constructor() : super()
    {
        getCurrentGameState()
    }

    constructor(gameArea: GameArea) : super()
    {
        this.gameArea = gameArea
    }


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        val saveGameComps = Family.all(
                SaveGameComponent::class.java,
                TeamComponent::class.java).get()

        return // TODO
        savableEntities = engine.getEntitiesFor(saveGameComps)
        val firstEntity = savableEntities.first()

        gameState = firstEntity.getComponent<SaveGameComponent>()!!.gameState
        getCurrentGameState().map = gameArea.areaID
        getCurrentGameState().team = firstEntity.getComponent<TeamComponent>()!!.team.copy()
        getCurrentGameState().guardoSphere = firstEntity.getComponent<GuardoSphereComponent>()!!.guardoSphere.copy()

        logDebug(TAG) { savableEntities.toString() }
        logDebug(TAG) { savableEntities.first().toString() }
        logDebug(TAG) { getCurrentGameState().guardoSphere.toString() }

        val posComp = firstEntity.getComponent<Transform>()!!

        getCurrentGameState().gridx = posComp.onGrid.x
        getCurrentGameState().gridy = posComp.onGrid.y
    }

    /**
     * Updates the savable data
     * @param deltaTime
     */
    override fun update(deltaTime: Float)
    {
        return // TODO
        for (entity in savableEntities)
        {
            val position = entity.getComponent<Transform>()!!
            val saveGame = entity.getComponent<SaveGameComponent>()!!
            saveGame.gameState.gridx = position.onGrid.x
            saveGame.gameState.gridy = position.onGrid.y
        }
    }

    /**
     * Serializes the current save game object with Kryo and writes it to a file in binary format
     */
    fun saveGame()
    {
        val kryo = Kryo()
        KryoSerializer.addLibGdxSerializers(kryo)
        try
        {
            val state = SerializableGameState(gameState!!)
            val handle = Gdx.files.local("gamestate/gamestate0.sav")
            val output = Output(handle.write(false))
            kryo.writeObject(output, state)
            output.close()
        }
        catch (e: Exception) { e.printStackTrace() }

        logDebug(TAG) { gameState!!.toString() }
    }

    companion object
    {

        private const val TAG = "SaveGameManager"
        private var gameState: GameState? = null

        fun getCurrentGameState() : GameState
        {
            if(gameState == null) { gameState = loadSaveGame() }
            return gameState!!
        }

        fun newSaveGame(): GameState
        {
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

            val team = TeamComponent()
            val factory = GuardiansServiceLocator.guardianFactory
            team.team.plus(factory.createGuardian(1, 1))

            gameState = GameState(

                    Constant.startMap,
                    Constant.startX,
                    Constant.startY,
                    1, 1,
                    team.team,
                    inventory,
                    GuardoSphere()
            )

            return gameState!!
        }

        /**
         * Loads the most recent game save file from internal storage and parses it into a gamestate obj
         * @return
         */
        fun loadSaveGame(): GameState
        {
            val kryo = Kryo()
            KryoSerializer.addLibGdxSerializers(kryo)

            if (doesGameSaveExist())
            {
                try
                {
                    val handle = Gdx.files.local("gamestate/gamestate0.sav")
                    val input = Input(handle.read())
                    val state = kryo.readObject(input, SerializableGameState::class.java)
                    gameState = SerializableGameState.deserialize(state)
                    println(gameState!!.toString())
                    input.close()
                }
                catch (e: Exception) { e.printStackTrace() }
            }
            else { newSaveGame() }

            return gameState!!
        }

        /**
         * Searches for game state files in the internal storage
         * @return
         */
        fun doesGameSaveExist() = Gdx.files.local("gamestate/gamestate0.sav").exists()
    }
}
