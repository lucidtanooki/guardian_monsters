package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.Element
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

    var changedProperty: UpdateType = UpdateType.UNCHANGED
        private set

    // ............................................................................................. DELEGATIONS

    // ............................................................. delegations: SpeciesDescription

    abstract val speciesID: Int
    abstract val commonStatistics: CommonStatistics

    fun setStatisticsChanged()
    {
        this.setChanged()
        this.changedProperty = UpdateType.STATS
    }

    fun setAbilitiesChanged()
    {
        this.setChanged()
        this.changedProperty = UpdateType.ABILITIES
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

    override fun hashCode(): Int = uuid.hashCode()

    // ........................................................... delegations: IndividualStatistics
    val stats    : IndividualStatistics get() = individualStatistics
    val species  : SpeciesDescription   get() = speciesDescription
    val elements : Array<Element>       get() = species.getElements(currentForm)

    fun isOfElement(element: Element) : Boolean = elements.contains(element, false)


    // ................................................................... delegations: AbilityGraph
    val currentForm : Int get() = abilityGraph.currentForm;


    // ............................................................................... Inner Classes
    enum class UpdateType { UNCHANGED, ABILITIES, STATS }
}
