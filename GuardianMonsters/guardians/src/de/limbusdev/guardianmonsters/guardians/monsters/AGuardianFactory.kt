package de.limbusdev.guardianmonsters.guardians.monsters

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import java.util.*

/**
 * AGuardianFactory
 *
 * Design Pattern: Factory Method
 *
 * @author Georg Eckert 2017
 */

abstract class AGuardianFactory protected constructor()
    : GuardiansServiceLocator.Service
{
    // ............................................................................................. ABSTRACT METHODS

    /**
     * FACTORY METHOD
     *
     * Takes full responsibility of creating a guardian.
     *
     * @param ID    ID of the guardian to be created, defines species
     * @param level level if the guardian to be created
     * @return      the complete generated guardian
     */
    abstract fun createGuardian(ID: Int, level: Int): AGuardian     // P04: Factory Method

    override fun destroy() {UUIDs.removeAll(UUIDs)}

    companion object
    {
        val UUIDs: MutableCollection<String> = ArrayList()

        // ......................................................................................... IMPLEMENTED METHODS

        /**
         * Creates a unique identifier for a new Guardian
         * @return
         */
        fun createNewUUID(): String
        {
            var unique = false
            var newUUID = ""
            while(!unique)
            {
                newUUID = UUID.randomUUID().toString()
                if(!UUIDs.contains(newUUID))
                {
                    unique = true
                }
            }
            UUIDs.add(newUUID)
            return newUUID
        }
    }
}
