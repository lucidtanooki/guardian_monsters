package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.ObjectMap
import ktx.log.info
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
    private val slots = mutableListOf<AGuardian>()
    val range: IntRange
        get() {return 0 until slots.size}

    constructor(maximumTeamSize: Int) : this(7, 1, 1)

    operator fun get(slot: Int) : AGuardian
    {
        if(slot !in range) throw IndexOutOfBoundsException(messageOutOfRange.format(range))

        return slots[slot]
    }

    private operator fun set(slot: Int, guardian: AGuardian) : AGuardian
    {
        if(slot !in range) throw IndexOutOfBoundsException(messageOutOfRange.format(range))
        if(isMember(guardian)) throw java.lang.IllegalArgumentException("Guardian is already in this team.")

        val formerOccupant = slots[slot]
        slots[slot] = guardian
        return formerOccupant
    }

    operator fun plusAssign(guardian: AGuardian) { plus(guardian) }
    operator fun minusAssign(guardian: AGuardian) { minus(guardian) }
    operator fun plusAssign(guardians: ObjectMap<Int,AGuardian>) { plus(guardians) }

    /**
     * Adds all given guardians to the team
     */
    operator fun plus(guardians: ObjectMap<Int,AGuardian>)
    {
        for(position in guardians.keys())
        {
            plusAssign(guardians[position])
        }
    }

    fun copy() : Team
    {
        val teamCopy = Team(capacity, maximumTeamSize, activeTeamSize)
        for(guardian in slots) teamCopy += guardian
        return teamCopy
    }

    /**
     * Adds the AGuardian instance on the right side to the team.
     * @param guardian guardian to be added
     */
    operator fun plus(guardian: AGuardian) : Int
    {
        if(slots.size >= capacity) throw IllegalStateException("Team is full. More members not allowed.")
        if(isMember(guardian)) throw java.lang.IllegalArgumentException("Guardian is already in this team.")
        slots.add(guardian)
        return slots.size-1
    }

    operator fun minus(guardian: AGuardian) : AGuardian
    {
        if(slots.size == 1) throw IllegalStateException("Cannot remove last Guardian from team.")
        if(!slots.contains(guardian)) throw IllegalArgumentException("Given guardian is not a member.")

        slots.remove(guardian)
        return guardian
    }

    fun remove(slot: Int) : AGuardian
    {
        if(slot !in range) throw IndexOutOfBoundsException(messageOutOfRange.format(range))

        return minus(slots[slot])
    }

    /**
     * Swaps positions of two monsters, if both positions are populated.
     * @param slotA
     * @param slotB
     * @return  whether the swap was successful
     */
    fun swap(slotA: Int, slotB: Int)
    {
        if(slotA == slotB)
        {
            info(TAG) { "[INFO] SlotA == SlotB! Swapping has no effect." }
            return
        }
        if(slotA !in range || slotB !in range)
            throw IndexOutOfBoundsException("Position must be in 0..${capacity-1}")

        val guardian1 = this[slotA]
        val guardian2 = this[slotB]

        slots[slotA] = guardian2    // Do not use this[position1] here, since it will throw an
        slots[slotB] = guardian1    // exception because both are already in the team.
    }

    fun replace(slot: Int, guardian: AGuardian) : AGuardian
    {
        if(slot >= size) throw IndexOutOfBoundsException("Slot must be in $range")

        val replacedGuardian = slots[slot]
        this[slot] = guardian
        return replacedGuardian
    }

    val size: Int get() { return slots.size }

    fun isMember(guardian: AGuardian): Boolean
    {
        return slots.contains(guardian)
    }

    fun getPosition(guardian: AGuardian): Int
    {
        return slots.indexOf(guardian)
    }

    fun teamKO(): Boolean
    {
        var ko = true
        for(guardian in slots)
        {
            ko = guardian.individualStatistics.isKO && ko
        }
        return ko
    }

    fun keys() : Array<Int>
    {
        return (IntArray(capacity) {it}).toTypedArray()
    }

    fun values() : List<AGuardian>
    {
        return slots
    }

    companion object
    {
        const val TAG = "Team"
        const val messageOutOfRange = "Out of capacity. Slot must be in %s."
    }
}
