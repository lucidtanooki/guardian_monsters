package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * SerializableGameState
 *
 * @author Georg Eckert 2017
 */

public class SerializableGameState {

    public SerializableInventory inventory;
    public SerializablePosition  position;
    public SerializableMonster[] team;
    public SerializableMonster[] guardoSphere;
    public SerializableProgress progress;
    public int activeTeamSize;


    @ForSerializationOnly
    public SerializableGameState() {}

    /**
     * Creates a serializable game state object from a given {@link GameState}
     * @param gameState
     * @return
     */
    public SerializableGameState(GameState gameState)
    {
        position = new SerializablePosition(gameState.gridx, gameState.gridy, gameState.map);

        inventory = new SerializableInventory(gameState.inventory);

        team = new SerializableMonster[7];
        for(ObjectMap.Entry<Integer,AGuardian> entry : gameState.team.entries()) {
            SerializableMonster monster = new SerializableMonster(entry.value);
            team[entry.key] = monster;
        }

        guardoSphere = new SerializableMonster[300];
        for(ObjectMap.Entry<Integer,AGuardian> entry : gameState.guardoSphere.entries()) {
            SerializableMonster monster = new SerializableMonster(entry.value);
            guardoSphere[entry.key] = monster;
        }

        progress = new SerializableProgress(1);

        activeTeamSize = gameState.activeTeamSize;
    }

    public static GameState deserialize(SerializableGameState state) {

        ArrayMap<Integer,AGuardian> team = new ArrayMap<>();
        for(int i=0; i<state.team.length; i++) {
            if(state.team[i] != null) {
                team.put(i, SerializableMonster.deserialize(state.team[i]));
            }
        }

        ArrayMap<Integer,AGuardian> guardoSphere = new ArrayMap<>();
        for(int i=0; i<state.guardoSphere.length; i++) {
            if(state.guardoSphere[i] != null) {
                guardoSphere.put(i, SerializableMonster.deserialize(state.guardoSphere[i]));
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
            inventory,
                guardoSphere
        );

        return gameState;
    }

}
