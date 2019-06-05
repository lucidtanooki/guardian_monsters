package de.limbusdev.guardianmonsters.services

import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.model.gamestate.GameState

class NullGameStateService : IGameStateService
{
    override fun destroy() {}

    override fun getSaveGameManager(): SaveGameManager? = null

    override fun getGameState(): GameState? = null
}
