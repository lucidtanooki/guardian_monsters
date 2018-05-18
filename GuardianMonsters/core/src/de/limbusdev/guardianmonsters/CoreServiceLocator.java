package de.limbusdev.guardianmonsters;

import de.limbusdev.guardianmonsters.services.IGameStateService;
import de.limbusdev.guardianmonsters.services.NullGameStateService;

public class CoreServiceLocator
{
    public interface Service
    {
        /**
         * If using Singletons as service, set instance null on destroy
         */
        void destroy();
    }

    private static IGameStateService gameState;

    public static void provide(IGameStateService service) {
        gameState = service;
    }

    public static IGameStateService getGameState() {
        if(gameState == null) {
            System.err.println("SERVICES: No Game State service injected yet with Services.provide(IGameStateService). Returning NullGameStateService.");
            return new NullGameStateService();
        } else {
            return gameState;
        }
    }


    public static void destroy()
    {
        if(gameState != null) {
            gameState.destroy();
            gameState = null;
        }
    }
}