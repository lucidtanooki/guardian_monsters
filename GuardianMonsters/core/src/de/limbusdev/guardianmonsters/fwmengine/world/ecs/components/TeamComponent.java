package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.model.monsters.Team;

/**
 * @author Georg Eckert 2016
 */
public class TeamComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Team team;

    public TeamComponent() {
        this.team = new Team(7,1,1);
    }
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
