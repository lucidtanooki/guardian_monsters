package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.gamestate.ForSerializationOnly;
import de.limbusdev.guardianmonsters.model.items.Inventory;
import de.limbusdev.guardianmonsters.model.monsters.Monster;


/**
 * @author Georg Eckert 2017
 */
public class GameState {
    /* ............................................................................ ATTRIBUTES .. */
    public int gridx,gridy,map;
    public ArrayMap<Integer,Monster> team;
    public Inventory inventory;
    public int maxTeamSizeInBattle;
    /* ........................................................................... CONSTRUCTOR .. */

    @ForSerializationOnly
    public GameState(int map, int x, int y, int maxTeamSizeInBattle,
                     ArrayMap<Integer, Monster> team, Inventory inventory) {
        this.map = map;
        this.gridx = x;
        this.gridy = y;
        this.maxTeamSizeInBattle = maxTeamSizeInBattle;
        this.team = team;
        this.inventory = inventory;
    }

    public GameState(int x, int y, int map) {
        this.gridx = x;
        this.gridy = y;
        this.map = map;
        this.team = new ArrayMap<>();
        this.maxTeamSizeInBattle = 1;
    }

    public String toString() {
        String out = "";
        out += "== Current Game State ==\n";
        out += "Last position: (" + gridx + "|" + gridy + ")" + " at map " + map + "\n";
        out += "Team Size: " + maxTeamSizeInBattle + "\n";
        out += "Team:\n";

        for(Monster m : team.values()) {
            out += m.toString() + "\n";
        }

        return out;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
