package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.model.Monster;

/**
 * Created by georg on 16.12.15.
 */
public class TeamComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    // Only 3 monsters may accompany the hero, they can be switched with other monsters at
    // Summoning Points
    public Array<Monster> monsters;

    public TeamComponent() {
        this.monsters = new Array<Monster>();
    }
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
