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
    /* ........................................................................... CONSTRUCTOR .. */

    public GameState(int x, int y, int map) {
        this.x = x;
        this.y = y;
        this.gridx = x/ GS.TILE_SIZE;
        this.gridy = y/ GS.TILE_SIZE;
        this.map = map;
        this.team = new ArrayMap<Integer, Monster>();
    }

    public GameState() {
        // ONLY FOR JSON CREATION
        this.team = new ArrayMap<Integer, Monster>();
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
