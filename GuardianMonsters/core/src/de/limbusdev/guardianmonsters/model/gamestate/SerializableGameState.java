package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;

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
    public int activeTeamSize;


    @ForSerializationOnly
    public SerializableGameState() {}

    /**
     * Creates a serializable game state object from a given {@link GameState}
     * @param gameState
     * @return
     */
    public SerializableGameState(GameState gameState) {
        position = new SerializablePosition(gameState.gridx, gameState.gridy, gameState.map);

        inventory = new SerializableInventory(gameState.inventory);

        team = new SerializableMonster[7];
        for(ObjectMap.Entry<Integer,Monster> entry : gameState.team.entries()) {
            SerializableMonster monster = new SerializableMonster(entry.value);
            team[entry.key] = monster;
        }

        allBannedGuardians = new SerializableMonster[300];

        progress = new SerializableProgress(1);

        activeTeamSize = gameState.activeTeamSize;
    }

    public static GameState deserialize(SerializableGameState state) {

        ArrayMap<Integer,Monster> team = new ArrayMap<>();
        for(int i=0; i<state.team.length; i++) {
            if(state.team[i] != null) {
                team.put(i, SerializableMonster.deserialize(state.team[i]));
            }
        }

        Inventory inventory = SerializableInventory.deserialize(state.inventory);

        GameState gameState = new GameState(
            state.position.map,
            state.position.x,
            state.position.y,
            state.progress.maxTeamSize,
            state.activeTeamSize,
            team,
            inventory
        );

        return gameState;
    }

}
