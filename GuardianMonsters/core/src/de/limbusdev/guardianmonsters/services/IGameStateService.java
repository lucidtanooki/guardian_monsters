package de.limbusdev.guardianmonsters.services;

import de.limbusdev.guardianmonsters.CoreServiceLocator;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;

public interface IGameStateService extends CoreServiceLocator.Service {

    SaveGameManager getSaveGameManager();
    GameState getGameState();
}
