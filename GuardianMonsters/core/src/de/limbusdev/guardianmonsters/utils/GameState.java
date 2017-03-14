package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.Monster;


/**
 * Created by georg on 03.12.15.
 */
public class GameState {
    /* ............................................................................ ATTRIBUTES .. */
    public int x,y,gridx,gridy,map;
    public ArrayMap<Integer,Monster> team;
    public int maxTeamSizeInBattle;
    /* ........................................................................... CONSTRUCTOR .. */

    public GameState(int x, int y, int map) {
        this.x = x;
        this.y = y;
        this.gridx = x/ Constant.TILE_SIZE;
        this.gridy = y/ Constant.TILE_SIZE;
        this.map = map;
        this.team = new ArrayMap<>();
        this.maxTeamSizeInBattle = 1;
    }

    public GameState() {
        // ONLY FOR JSON CREATION
        this.team = new ArrayMap<>();
    }

    public String toString() {
        String out = "";
        out += "Last position: (" + gridx + "|" + gridy + ")" + " at map " + map + "\n";
        out += "Team:\n";

        for(Monster m : team.values()) {
            out += m.toString() + "\n";
        }

        return out;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
