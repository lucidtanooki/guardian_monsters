package de.limbusdev.guardianmonsters.guardians.monsters

import de.limbusdev.utils.extensions.set
import com.badlogic.gdx.utils.Array as GdxArray
import kotlin.IllegalStateException

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
    val slots = GdxArray<AGuardian?>(true, capacity)
    val range = 0 until capacity

    init
    {
        for(i in range) slots.add(null)
    }

    constructor(maximumTeamSize: Int) : this(7, 1, 1)

    operator fun get(slot: Int) : AGuardian?
    {
        if(slot !in range)
            throw IndexOutOfBoundsException("Out of capacity. Slot mus be in 0..${capacity-1}")

        return slots[slot]
    }

    operator fun set(slot: Int, guardian: AGuardian) : AGuardian?
    {
        if(slot !in range)
            throw IndexOutOfBoundsException("Out of capacity. Slot mus be in 0..${capacity-1}")

        val formerOccupant = slots[slot]
        slots[slot] = guardian
        return formerOccupant
    }

    operator fun plus(guardian: AGuardian) : Int
    {
        if(isMember(guardian)) throw java.lang.IllegalArgumentException("Guardian is already in this team.")

        for(slot in range)
        {
            if(this[slot] == null)
            {
                this[slot] = guardian
                return slot
            }
        }

        throw IllegalStateException("Team is full. This should not happen.")
    }

    operator fun minus(guardian: AGuardian) : AGuardian
    {
        if(slots[1] == null)
            throw IllegalStateException("Cannot remove last Guardian from team.")

        val slot = slots.indexOf(guardian)
        val removedGuardian = slots[slot] ?: throw java.lang.IllegalStateException("Given guardian is not a member.")

        // Move other team members to close gap
        for(s in range)
        {
            if(s > slot)
            {
                slots[s-1] = slots[s]
                slots[s] = null
            }
        }

        return removedGuardian
    }

    /**
     * Swaps positions of two monsters, if both positions are populated.
     * @param position1
     * @param position2
     * @return  whether the swap was successful
     */
    fun swap(position1: Int, position2: Int)
    {
        if(position1 !in range || position2 !in range)
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
        return slots.contains(guardian, false)
    }

    fun getPosition(guardian: AGuardian): Int
    {
        return slots.indexOf(guardian, false)
    }

    fun teamKO(): Boolean
    {
        var ko = true
        for(guardian in slots)
        {
            if(guardian != null)
            {
                ko = guardian.individualStatistics.isKO && ko
            }
        }
        return ko
    }
}
