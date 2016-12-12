package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Created by georg on 16.12.15.
 */
public class TeamComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    // Only 3 monsters may accompany the hero, they can be switched with other monsters at
    // Summoning Points
    public ArrayMap<Integer,Monster> monsters;

    public TeamComponent() {
        this.monsters = new ArrayMap<Integer,Monster>();
    }
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
