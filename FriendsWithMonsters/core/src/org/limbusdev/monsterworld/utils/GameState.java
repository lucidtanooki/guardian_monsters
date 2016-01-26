package org.limbusdev.monsterworld.utils;

import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.model.Monster;

/**
 * Created by georg on 03.12.15.
 */
public class GameState {
    /* ............................................................................ ATTRIBUTES .. */
    public int x,y,gridx,gridy,map;
    public Array<Monster> team;
    /* ........................................................................... CONSTRUCTOR .. */

    public GameState(int x, int y, int map) {
        this.x = x;
        this.y = y;
        this.gridx = x/GlobalSettings.TILE_SIZE;
        this.gridy = y/GlobalSettings.TILE_SIZE;
        this.map = map;
        this.team = new Array<Monster>();
    }

    public GameState() {
        // ONLY FOR JSON CREATION
        this.team = new Array<Monster>();
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
