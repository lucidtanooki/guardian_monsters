package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere;
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
    public GuardoSphere guardoSphere;
    public Inventory inventory;
    public int maxTeamSize;
    public int activeTeamSize;
    /* ........................................................................... CONSTRUCTOR .. */

    @ForSerializationOnly
    public GameState(int map, int x, int y, int maxTeamSize, int activeTeamSize,
                     Team team, Inventory inventory, GuardoSphere guardoSphere) {
        this.map = map;
        this.gridx = x;
        this.gridy = y;
        this.maxTeamSize = maxTeamSize;
        this.activeTeamSize = activeTeamSize;
        this.team = team.copy();
        this.inventory = inventory;
        this.guardoSphere = guardoSphere.copy();
    }

    public String toString()
    {
        StringBuilder out = new StringBuilder();
        out.append("")
                .append("== Current Game State ==\n")
                .append("Last position: (").append(gridx).append("|").append(gridy).append(")")
                .append(" at map ").append(map).append("\n")
                .append("Team Size: ").append(maxTeamSize).append("\n")
                .append("Team:\n");

        for(AGuardian m : team.values())
        {
            out.append(m.toString()).append("\n");
        }

        out.append("GuardoSphere:\n");

        out.append(guardoSphere.toString());


        return out.toString();
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
