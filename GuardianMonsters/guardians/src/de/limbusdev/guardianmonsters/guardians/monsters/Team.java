package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.ArrayMap;

/**
 * Team
 *
 * @author Georg Eckert 2017
 */

public class Team extends ArrayMap<Integer,AGuardian>
{
    private int maximumTeamSize;
    private int activeTeamSize;

    public Team(int maximumTeamSize) {
        this(7,1,1);
    }

    public Team(int capacity, int maximumTeamSize, int activeTeamSize)
    {
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
    public boolean swapPositions(int position1, int position2)
    {
        AGuardian guardian1 = get(position1);
        AGuardian guardian2 = get(position2);

        if(guardian1 == null || guardian2 == null) {
            return false;
        }

        put(position1, guardian2);
        put(position2, guardian1);

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

    public boolean isMember(AGuardian guardian) {
        return containsValue(guardian,false);
    }

    public int getPosition(AGuardian guardian) {
        return getKey(guardian, false);
    }
}
