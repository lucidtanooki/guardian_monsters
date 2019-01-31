package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.utils.extensions.set
import java.lang.IllegalStateException

/**
 * The GuardoSphere is a dimension usually only Guardian Monsters can reach.
 * Humans may only get access by opening their chakras and deep meditation.
 *
 * It is a storage place for [AGuardian]s. Up to 300 Guardians can be stored
 * in the GuardoSphere. This limits the number of Guardians a player may
 * ban and collect.
 *
 * @author Georg Eckert 2019
 */

class GuardoSphere
{
    // Lists whether a Guardian and it's form are unknown, have been seen or already banned.
    private val status = ArrayMap<Int, ArrayMap<Int, State>>(300)
    private val sphere = ArrayMap<Int, AGuardian?>(300)

    init
    {
        // Initialize sphere slots
        for(slot in 0..299) sphere[slot] = null

        // Initialize all EncycloStates to UNKNOWN
        for(species in 0..299)
        {
            status[species] = ArrayMap()
            for(form in 0..4) status[species][form] = State.UNKNOWN
        }
    }

    /**
     * Puts an [AGuardian] into the [GuardoSphere].
     *
     * Syntax:
     *
     *      guardoSphere[slot] = Guardian()
     *
     * @param slot      sphere slot, where the given Guardian should reside
     * @param guardian  Guardian which should stay in sphere
     * @return          null if slot was empty, former occupant of the slot otherwise
     */
    operator fun set(slot: Int, guardian: AGuardian?) : AGuardian?
    {
        if(slot !in 0..299) throw IndexOutOfBoundsException("Slot must be in 0..299")
        val formerOccupant = sphere[slot]
        sphere[slot] = guardian
        return formerOccupant
    }

    /**
     * Retrieves an [AGuardian] from the [GuardoSphere].
     *
     * Syntax:
     *
     *      val myGuardian = guardoSphere[slot]
     *
     * @param slot a slot in 0..299
     * @return Guardian if slot was occupied, null otherwise
     */
    operator fun get(slot: Int) : AGuardian?
    {
        if(slot !in 0..299) throw IndexOutOfBoundsException("Slot must be in 0..299")
        return sphere[slot]
    }

    /**
     * Pushes an [AGuardian] into the [GuardoSphere] if there are free slots.
     *
     * @param guardian Guardian to be pushed
     * @return slot, where the Guardian has been placed, null if sphere is full
     */
    operator fun plus(guardian: AGuardian) : Int
    {
        for(slot in 0..299)
        {
            if(this[slot] == null)
            {
                this[slot] = guardian
                return slot
            }
        }
        throw IllegalStateException("Sphere is full. This should not happen.")
    }

    /**
     * Checks if there are vacant slots in the sphere.
     *
     * @return if sphere is full or not
     */
    fun isFull() : Boolean = (vacantSlots() == 0)

    fun vacantSlots() : Int
    {
        var counter = 0
        for(slot in 0..299)
            if(this[slot] == null)
                counter++
        return counter
    }

    fun occupiedSlots() : Int = 300 - vacantSlots()

    /**
     * Swaps two slots in the [GuardoSphere] and the potentially occupying [AGuardian]s.
     */
    fun swap(slot1: Int, slot2: Int)
    {
        if(slot1 !in 0..299 || slot2 !in 0..299)
            throw IndexOutOfBoundsException("Slot must be in 0..299")

        val value1 = this[slot1]
        val value2 = this[slot2]

        this[slot2] = value1
        this[slot1] = value2
    }

    fun getEncycloStateOf(speciesID: Int, metaForm: Int): State
    {
        if(status.containsKey(speciesID) && status[speciesID].containsKey(metaForm))
        {
            return status[speciesID][metaForm]
        }
        else
        {
            return State.UNKNOWN
        }
    }

    fun setEncycloStateOf(speciesID: Int, metaForm: Int, state: State)
    {
        // Do not downgrade state
        if(state == State.UNKNOWN) return

        if(!status.containsKey(speciesID))
        {
            status[speciesID] = ArrayMap()
        }

        // Do not downgrade state
        if(status[speciesID].containsKey(metaForm) && status[speciesID][metaForm] == State.BANNED)
        {
            return
        }
        else
        {
            status[speciesID][metaForm] = state
        }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Inner Classes
    enum class State
    {
        UNKNOWN, SEEN, BANNED
    }
}
