package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.CombatTeam;

/**
 * Team
 *
 * @author Georg Eckert 2017
 */

public class Team extends ArrayMap<Integer,Monster> {
    private int maximumTeamSize;
    private int activeTeamSize;

    public Team(int maximumTeamSize) {
        this(7,1,1);
    }

    public Team(int capacity, int maximumTeamSize, int activeTeamSize) {
        super(true, capacity);
        this.maximumTeamSize = maximumTeamSize;
        this.activeTeamSize = activeTeamSize;
    }

    /**
     * Returns a team of monsters for battle. Team size is determined by the maximum possible
     * team size and the chosen active team size.
     *
     * Even defeated monsters are added. They can be revived during battle. If all monsters of the
     * active team get defeated during battle, even if there are other monsters available, the
     * player is game over, because he can't call them anymore.
     *
     * Therefore players must watch out, that the active team never gets defeated completely.
     *
     * Only players can revive members of the combat party. Enemies can't.
     *
     * @return
     */
    public CombatTeam getCombatTeam() {
        int teamSize = Math.min(maximumTeamSize, activeTeamSize);
        CombatTeam combatTeam = new CombatTeam();

        for(int i=0; i < this.size && i < teamSize; i++) {
            combatTeam.put(i,get(i));
        }

        return combatTeam;
    }

    /**
     * Swaps positions of two monsters, if both positions are populated.
     * @param position1
     * @param position2
     * @return  wether the swap was successful
     */
    public boolean swapPositions(int position1, int position2) {
        Monster monster1 = get(position1);
        Monster monster2 = get(position2);

        if(monster1 == null || monster2 == null) {
            return false;
        }

        put(position1, monster2);
        put(position2, monster1);

        return true;
    }

    public int getMaximumTeamSize() {
        return maximumTeamSize;
    }

    public void setMaximumTeamSize(int maximumTeamSize) {
        this.maximumTeamSize = maximumTeamSize;
    }

    public int getActiveTeamSize() {
        return activeTeamSize;
    }

    public void setActiveTeamSize(int activeTeamSize) {
        this.activeTeamSize = activeTeamSize;
    }

    public boolean isMember(Monster monster) {
        return containsValue(monster,false);
    }

    public int getPosition(Monster monster) {
        return getKey(monster, false);
    }
}
