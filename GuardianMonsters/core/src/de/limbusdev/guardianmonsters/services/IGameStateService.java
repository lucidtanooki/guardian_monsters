package de.limbusdev.guardianmonsters.services;

import de.limbusdev.guardianmonsters.CoreSL;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;

public interface IGameStateService extends CoreSL.Service {

    SaveGameManager getSaveGameManager();
    GameState getGameState();
}
