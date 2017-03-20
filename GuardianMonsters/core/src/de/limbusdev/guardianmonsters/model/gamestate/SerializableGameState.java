package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.utils.GameState;

/**
 * SerializableGameState
 *
 * @author Georg Eckert 2017
 */

public class SerializableGameState {

    public SerializableInventory inventory;
    public SerializablePosition  position;
    public SerializableMonster[] team;
    public SerializableMonster[] allBannedGuardians;
    public SerializableProgress progress;


    /**
     * For Serialization Only
     */
    public SerializableGameState() {

    }

    /**
     * Creates a serializable game state object from a given {@link GameState}
     * @param gameState
     * @return
     */
    public static SerializableGameState convertToSerializable(GameState gameState) {
        SerializableGameState state = new SerializableGameState();

        SerializablePosition position =
            new SerializablePosition(gameState.gridx, gameState.gridy, gameState.map);
        state.position = position;

        SerializableInventory inventory = new SerializableInventory();
        state.inventory = inventory;

        SerializableMonster[] team = new SerializableMonster[7];
        for(ObjectMap.Entry<Integer,Monster> entry : gameState.team.entries()) {
            SerializableMonster monster = new SerializableMonster();
            team[entry.key] = monster;
        }
        state.team = team;

        SerializableMonster[] allBannedGuardians = new SerializableMonster[300];
        state.allBannedGuardians = allBannedGuardians;

        SerializableProgress progress = new SerializableProgress(1);
        state.progress = progress;

        return state;
    }

    public static GameState deserialize(SerializableGameState state) {
        GameState gameState = new GameState();

        gameState.map = state.position.map;
        gameState.gridx = state.position.x;
        gameState.gridy = state.position.y;

        gameState.maxTeamSizeInBattle = state.progress.maxBattleTeamSize;

        return gameState;
    }

}
