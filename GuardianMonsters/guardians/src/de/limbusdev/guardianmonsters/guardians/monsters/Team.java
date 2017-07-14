package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.ArrayMap;

/**
 * Team
 *
 * @author Georg Eckert 2017
 */

public class Team extends ArrayMap<Integer,Monster>
{
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
