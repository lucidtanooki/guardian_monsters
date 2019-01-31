package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Array
import de.limbusdev.utils.extensions.set
import java.lang.IllegalStateException

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
        private var capacity: Int,
        var maximumTeamSize: Int,
        var activeTeamSize: Int
) {
    val slots = ArrayMap<Int, AGuardian?>(true, capacity)

    constructor(maximumTeamSize: Int) : this(7, 1, 1) {}

    operator fun get(slot: Int) : AGuardian?
    {
        if(slot !in 0..(capacity-1))
            throw IndexOutOfBoundsException("Out of capacity. Slot mus be in 0..${capacity-1}")

        return slots[slot]
    }

    operator fun set(slot: Int, guardian: AGuardian) : AGuardian?
    {
        if(slot !in 0..(capacity-1))
            throw IndexOutOfBoundsException("Out of capacity. Slot mus be in 0..${capacity-1}")

        val formerOccupant = slots[slot]
        slots[slot] = guardian
        return formerOccupant
    }

    operator fun plus(guardian: AGuardian) : Int
    {
        for(slot in 0..(capacity-1))
        {
            if(this[slot] == null)
            {
                this[slot] = guardian
                return slot
            }
        }
        throw IllegalStateException("Team is full. This should not happen.")
    }

    /**
     * Swaps positions of two monsters, if both positions are populated.
     * @param position1
     * @param position2
     * @return  whether the swap was successful
     */
    fun swap(position1: Int, position2: Int)
    {
        if(position1 !in 0..(capacity-1) || position2 !in 0..(capacity-1))
            throw IndexOutOfBoundsException("Position must be in 0..${capacity-1}")

        val guardian1 = this[position1]
        val guardian2 = this[position2]

        if(guardian1 == null || guardian2 == null)
            throw IllegalArgumentException("Only occupied positions may be swapped.")

        this[position1] = guardian2
        this[position2] = guardian1
    }

    val size: Int get() { return slots.size }

    fun isMember(guardian: AGuardian): Boolean
    {
        return slots.containsValue(guardian, false)
    }

    fun getPosition(guardian: AGuardian): Int
    {
        return slots.getKey(guardian, false)
    }

    fun teamKO(): Boolean
    {
        var ko = true
        for(guardian in slots.values())
        {
            if(guardian != null)
            {
                ko = guardian.individualStatistics.isKO && ko
            }
        }
        return ko
    }
}
