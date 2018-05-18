package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.ArrayMap;

import static de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere.State.BANNED;
import static de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere.State.UNKNOWN;

/**
 * GuardoSphere
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphere extends ArrayMap<Integer,AGuardian>
{
    public enum State {

        UNKNOWN, SEEN, BANNED,
    }

    // Lists whether a Guardian and it's form are unknown, have been seen or already banned.
    private ArrayMap<Integer, ArrayMap<Integer, State>> encycloStates;

    public GuardoSphere() {

        super(300);

        // Initialize all Encyclostates to UNKNOWN
        encycloStates = new ArrayMap<>();
        for(int speciesID = 1; speciesID <= 300; speciesID++) {

            encycloStates.put(speciesID, new ArrayMap<>());
            for(int metaForm = 0; metaForm <= 4; metaForm++) {

                encycloStates.get(speciesID).put(metaForm, State.UNKNOWN);
            }
        }
    }

    public State getEncycloStateOf(int speciesID, int metaForm) {

        if(encycloStates.containsKey(speciesID) && encycloStates.get(speciesID).containsKey(metaForm)) {

            return encycloStates.get(speciesID).get(metaForm);

        } else {

            return State.UNKNOWN;
        }
    }

    public void setEncycloStateOf(int speciesID, int metaForm, State state) {

        // Do not downgrade state
        if(state == UNKNOWN) {

            return;
        }

        if(!encycloStates.containsKey(speciesID)) {

            encycloStates.put(speciesID, new ArrayMap<>());
        }

        // Do not downgrade state
        if(encycloStates.get(speciesID).containsKey(metaForm)
                && (encycloStates.get(speciesID).get(metaForm) == BANNED)) {

            return;
        } else {
            encycloStates.get(speciesID).put(metaForm, state);
        }

    }
}
