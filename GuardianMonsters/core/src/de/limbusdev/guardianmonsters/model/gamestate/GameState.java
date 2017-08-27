package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;


/**
 * @author Georg Eckert 2017
 */
public class GameState {
    /* ............................................................................ ATTRIBUTES .. */
    public int gridx,gridy,map;
    public Team team;
    public Inventory inventory;
    public int maxTeamSize;
    public int activeTeamSize;
    /* ........................................................................... CONSTRUCTOR .. */

    @ForSerializationOnly
    public GameState(int map, int x, int y, int maxTeamSize, int activeTeamSize,
                     ArrayMap<Integer, AGuardian> team, Inventory inventory) {
        this.map = map;
        this.gridx = x;
        this.gridy = y;
        this.maxTeamSize = maxTeamSize;
        this.activeTeamSize = activeTeamSize;
        this.team = new Team(7,maxTeamSize,activeTeamSize);
        this.team.putAll(team);
        this.inventory = inventory;
    }

    public String toString() {
        String out = "";
        out += "== Current Game State ==\n";
        out += "Last position: (" + gridx + "|" + gridy + ")" + " at map " + map + "\n";
        out += "Team Size: " + maxTeamSize + "\n";
        out += "Team:\n";

        for(AGuardian m : team.values()) {
            out += m.toString() + "\n";
        }

        return out;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
