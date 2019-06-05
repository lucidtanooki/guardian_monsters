package de.limbusdev.guardianmonsters.services;

import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;

public class NullGameStateService implements IGameStateService
{
    @Override
    public void destroy() { }

    @Override
    public SaveGameManager getSaveGameManager() {
        return null;
    }

    @Override
    public GameState getGameState() {
        return null;
    }
}
