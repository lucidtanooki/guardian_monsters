package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.ArrayMap;

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
                     ArrayMap<Integer, AGuardian> team, Inventory inventory, ArrayMap<Integer, AGuardian> guardoSphere) {
        this.map = map;
        this.gridx = x;
        this.gridy = y;
        this.maxTeamSize = maxTeamSize;
        this.activeTeamSize = activeTeamSize;
        this.team = new Team(7,maxTeamSize,activeTeamSize);
        this.team.putAll(team);
        this.inventory = inventory;
        this.guardoSphere = new GuardoSphere();
        this.guardoSphere.putAll(guardoSphere);
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

        for(Integer key : guardoSphere.keys())
        {
            out.append("Position ").append(key).append(": ")
                    .append(guardoSphere.get(key).toString()).append("\n");
        }


        return out.toString();
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
