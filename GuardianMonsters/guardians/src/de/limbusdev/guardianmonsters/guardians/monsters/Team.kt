package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.utils.extensions.set

/**
 * Team
 *
 * @author Georg Eckert 2017
 */
class Team(

        capacity: Int,
        var maximumTeamSize: Int,
        var activeTeamSize: Int)

    : ArrayMap<Int, AGuardian>(true, capacity)
{

    constructor(maximumTeamSize: Int) : this(7, 1, 1) {}

    /**
     * Swaps positions of two monsters, if both positions are populated.
     * @param position1
     * @param position2
     * @return  whether the swap was successful
     */
    fun swapPositions(position1: Int, position2: Int): Boolean
    {
        val guardian1 = this[position1]
        val guardian2 = this[position2]

        if(guardian1 == null || guardian2 == null) {
            return false
        }

        this[position1] = guardian2
        this[position2] = guardian1

        return true
    }

    fun isMember(guardian: AGuardian): Boolean
    {
        return containsValue(guardian, false)
    }

    fun getPosition(guardian: AGuardian): Int
    {
        return getKey(guardian, false)
    }

    fun teamKO(): Boolean
    {
        var ko = true
        for(guardian in values())
        {
            ko = guardian.individualStatistics.isKO && ko
        }
        return ko
    }
}
