package de.limbusdev.guardianmonsters

import de.limbusdev.guardianmonsters.services.IGameStateService
import de.limbusdev.guardianmonsters.services.NullGameStateService

object CoreServiceLocator {

    private var gameState: IGameStateService? = null

    interface Service
    {
        /** If using Singletons as service, set instance null on destroy */
        fun destroy()
    }

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