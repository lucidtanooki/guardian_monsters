package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

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


        for(int i=0; i<gameState.team.getSize(); i++)
        {
            AGuardian guardian = gameState.team.get(i);
            SerializableMonster monster = new SerializableMonster(guardian);
            team[i] = monster;
        }

        guardoSphere = new SerializableMonster[300];
        for(int i=0; i < GuardoSphere.capacity; i++)
        {
            AGuardian guardian = gameState.guardoSphere.get(i);
            if(guardian != null)
            {
                SerializableMonster monster = new SerializableMonster(guardian);
                guardoSphere[i] = monster;
            }
        }

        progress = new SerializableProgress(1);

        activeTeamSize = gameState.activeTeamSize;
    }

    public static GameState deserialize(SerializableGameState state)
    {
        Team team = new Team(state.team.length, state.progress.maxTeamSize, state.activeTeamSize);
        for(int i=0; i<state.team.length; i++) {
            if(state.team[i] != null) {
                team.plus(SerializableMonster.deserialize(state.team[i]));
            }
        }

        GuardoSphere sphere = new GuardoSphere();
        for(int i=0; i<state.guardoSphere.length; i++) {
            if(state.guardoSphere[i] != null) {
                sphere.set(i, SerializableMonster.deserialize(state.guardoSphere[i]));
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
            sphere
        );

        return gameState;
    }

}
