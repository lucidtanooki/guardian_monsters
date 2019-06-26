/**
 * Copyright (C) 2019 Georg Eckert - All Rights Reserved
 */

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
        var maximumTeamSize : Int,
        var activeTeamSize  : Int
) {
    // .................................................................................. Properties
    private val slots = mutableListOf<AGuardian>()

    val size      : Int      get() = slots.size
    val range     : IntRange get() = 0 until size
    val meanLevel : Float    get() = slots.sumBy { if(it.stats.isFit) it.stats.level else 0 }.toFloat() / size
    val allKO     : Boolean  get() = slots.all { it.stats.isKO }


    // ................................................................................ Constructors
    constructor(maximumTeamSize: Int) : this(7, 1, 1)


    // ................................................................................... Operators
    operator fun get(slot: Int) : AGuardian
    {
        check(slot in range) { messageOutOfRange.format(range) }

        return slots[slot]
    }

    private operator fun set(slot: Int, guardian: AGuardian) : AGuardian
    {
        check(slot in range) { messageOutOfRange.format(range) }
        check(!isMember(guardian)) { "Guardian is already in this team." }

        val formerOccupant = slots[slot]
        slots[slot] = guardian
        return formerOccupant
    }

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

    /**
     * Adds the AGuardian instance on the right side to the team.
     * @param guardian guardian to be added
     */
    operator fun plus(guardian: AGuardian) : Int
    {
        check(size < capacity)     { "Team is full. More members not allowed." }
        check(!isMember(guardian)) { "Guardian is already in this team."       }

        slots.add(guardian)
        return slots.size-1
    }

    operator fun minus(guardian: AGuardian) : AGuardian
    {
        check(size != 1)          { "Cannot remove last Guardian from team." }
        check(isMember(guardian)) { "Given guardian is not a member."        }

        slots.remove(guardian)
        return guardian
    }

    operator fun plusAssign(guardian: AGuardian)                 { plus(guardian)  }
    operator fun minusAssign(guardian: AGuardian)                { minus(guardian) }
    operator fun plusAssign(guardians: ObjectMap<Int,AGuardian>) { plus(guardians) }


    // ..................................................................................... Methods
    fun copy() : Team
    {
        val teamCopy = Team(capacity, maximumTeamSize, activeTeamSize)
        for(guardian in slots) { teamCopy += guardian }
        return teamCopy
    }

    fun remove(slot: Int) : AGuardian
    {
        check(slot in range) { messageOutOfRange.format(range) }

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
        check(slotA in range && slotB in range) { "Position must be in 0..${capacity - 1}" }

        if(slotA == slotB)
        {
            info(TAG) { "[INFO] SlotA == SlotB! Swapping has no effect." }
            return
        }

        val guardian1 = this[slotA]
        val guardian2 = this[slotB]

        slots[slotA] = guardian2    // Do not use this[slotA] here, since it will throw an
        slots[slotB] = guardian1    // exception because both are already in the team.
    }

    fun replace(slot: Int, guardian: AGuardian) : AGuardian
    {
        check(slot < size) { "Slot must be in $range" }

        val replacedGuardian = slots[slot]
        this[slot] = guardian
        return replacedGuardian
    }


    // ........................................................................... Getters & Setters
    fun isMember(guardian: AGuardian): Boolean = slots.contains(guardian)

    fun getPosition(guardian: AGuardian): Int = slots.indexOf(guardian)

    /** Returns the keys of all team slots. Includes empty slots. */
    fun keys()              : Array<Int> = (IntArray(capacity) {it}).toTypedArray()
    fun occupiedSlotsKeys() : Array<Int> = (IntArray(size) {it}).toTypedArray()
    fun values()            : List<AGuardian> = slots


    // ................................................................................... Companion
    companion object
    {
        const val TAG = "Team"
        const val messageOutOfRange = "Out of capacity. Slot must be in %s."
    }
}
