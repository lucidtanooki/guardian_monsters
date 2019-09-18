package de.limbusdev.guardianmonsters

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.EntityComponentSystem
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.World
import de.limbusdev.guardianmonsters.services.IGameStateService
import de.limbusdev.guardianmonsters.services.NullGameStateService
import java.lang.IllegalStateException

/**
 * Service Locator of the Core Module
 */
object CoreSL
{
    private var gameState: IGameStateService? = null
    lateinit var world: World
        private set
    lateinit var ecs: EntityComponentSystem
        private set

    interface Service
    {
        /** If using Singletons as service, set instance null on destroy */
        fun destroy()
    }

    fun provide(world: World) { this.world = world }

    fun provide(ecs: EntityComponentSystem) { this.ecs = ecs }

    fun provide(service: IGameStateService) { gameState = service }

    fun getGameState(): IGameStateService
    {
        return if (gameState == null)
        {
            System.err.println("SERVICES: No Game State service injected yet with Services.provide(IGameStateService). Returning NullGameStateService.")
            NullGameStateService()
        }
        else
        { gameState as IGameStateService }
    }


    fun destroy()
    {
        if (gameState != null)
        {
            gameState!!.destroy()
            gameState = null
        }
    }
}