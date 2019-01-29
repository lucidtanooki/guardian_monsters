package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.utils.extensions.set

/**
 * A [Team] can hold any number > 1 of Guardians. The player's [Team] has
 * always a capacity of 7. AI players like enemies and bosses may have
 * more or less than 7, but never < 1.
 *
 * maximumTeamSize and activeTeamSize are <= capacity. And activeTeamSize
 * <= maximumTeamSize. maximumTeamSize is how much Guardians the team may
 * hold. activeTeamSize defines, how many of them take part in the battle.
 *
 * A player always has a team with a capacity of 7. His maximumTeamSize
 * starts with 1 and grows as he opens his other chakras. The
 * activeTeamSize depends on how many (1..3) Guardians are activated by
 * the player in the TeamSubMenu of the Inventory
 *
 * @param maximumTeamSize   number of Guardians, which can be inside the team
 * @param activeTeamSize    number of Guardians, which take part in the battle
 */
class Team
(
        capacity: Int,
        var maximumTeamSize: Int,
        var activeTeamSize: Int
)
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
