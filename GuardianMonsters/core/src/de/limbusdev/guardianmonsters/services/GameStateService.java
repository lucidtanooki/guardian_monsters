package de.limbusdev.guardianmonsters.services;

import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;

public class GameStateService implements IGameStateService {

    private SaveGameManager saveGameManager;

    public GameStateService(SaveGameManager saveGameManager) {

        this.saveGameManager = saveGameManager;
    }

    @Override
    public SaveGameManager getSaveGameManager() {
        return null;
    }

    @Override
    public GameState getGameState() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
