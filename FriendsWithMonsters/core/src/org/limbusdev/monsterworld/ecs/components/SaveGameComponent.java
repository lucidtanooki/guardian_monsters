package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;

import org.limbusdev.monsterworld.utils.GameState;

/**
 * Created by georg on 03.12.15.
 */
public class SaveGameComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public GameState gameState;
    /* ........................................................................... CONSTRUCTOR .. */

    public SaveGameComponent(GameState gameState) {
        this.gameState = gameState;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
