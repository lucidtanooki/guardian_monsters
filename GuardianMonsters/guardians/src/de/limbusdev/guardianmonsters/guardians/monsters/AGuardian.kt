package de.limbusdev.guardianmonsters.guardians.monsters

import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph
import java.util.*

/**
 * AGuardian
 *
 * Abstract guardian class that defines how a guardian
 * looks internally and what it provides.
 *
 * @author Georg Eckert 2017
 */
abstract class AGuardian protected constructor(val uuid: String) : Observable()
{
    // Unique ID for Guardian Identification, must be stored when persisted
    abstract var nickname: String

    // ............................................................................................. COMPONENTS

    abstract val speciesDescription: SpeciesDescription
    abstract val individualStatistics: IndividualStatistics
    abstract val abilityGraph: IAbilityGraph

    // ............................................................................................. OBSERVABLE

    var changeType: Class<*>? = null
        private set

    // ............................................................................................. DELEGATIONS

    // ............................................................. delegations: SpeciesDescription

    abstract val speciesID: Int
    abstract val commonStatistics: CommonStatistics

    fun setStatisticsChanged()
    {
        this.setChanged()
        this.changeType = IndividualStatistics::class.java
    }

    fun setAbilitiesChanged()
    {
        this.setChanged()
        this.changeType = IAbilityGraph::class.java
    }


    // ............................................................................................. OBJECT
    override fun equals(other: Any?): Boolean
    {
        if(other == null || other !is AGuardian)
        {
            return false
        }

        return other.uuid == uuid
    }

    override fun hashCode(): Int
    {
        return uuid.hashCode()
    }

    // ........................................................... delegations: IndividualStatistics

    // ................................................................... delegations: AbilityGraph
}
