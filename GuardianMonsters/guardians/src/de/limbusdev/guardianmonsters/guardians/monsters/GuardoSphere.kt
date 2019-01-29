package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.utils.extensions.set

/**
 * GuardoSphere
 *
 * @author Georg Eckert 2017
 */

class GuardoSphere : ArrayMap<Int, AGuardian>(300)
{
    // Lists whether a Guardian and it's form are unknown, have been seen or already banned.
    private val encycloStates: ArrayMap<Int, ArrayMap<Int, State>> = ArrayMap()

    init
    {
        // Initialize all Encyclostates to UNKNOWN
        for(speciesID in 1..300)
        {
            encycloStates[speciesID] = ArrayMap()
            for(metaForm in 0..4)
            {
                encycloStates[speciesID][metaForm] = State.UNKNOWN
            }
        }
    }

    fun swap(key1: Int, key2: Int)
    {
        val value1 = this[key1]
        val value2 = this[key2]

        if(value1 == null) this.removeKey(key2) else this[key2] = value1
        if(value2 == null) this.removeKey(key1) else this[key1] = value2
    }

    fun getEncycloStateOf(speciesID: Int, metaForm: Int): State
    {
        if(encycloStates.containsKey(speciesID) && encycloStates[speciesID].containsKey(metaForm))
        {
            return encycloStates[speciesID][metaForm]
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

        if(!encycloStates.containsKey(speciesID))
        {
            encycloStates[speciesID] = ArrayMap()
        }

        // Do not downgrade state
        if(encycloStates[speciesID].containsKey(metaForm) && encycloStates[speciesID][metaForm] == State.BANNED)
        {
            return
        }
        else
        {
            encycloStates[speciesID][metaForm] = state
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Inner Classes
    enum class State
    {
        UNKNOWN, SEEN, BANNED
    }
}
