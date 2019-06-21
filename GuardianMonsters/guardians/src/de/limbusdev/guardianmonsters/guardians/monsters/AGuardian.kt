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
    // ............................................................................................. PROPERTIES
    // Unique ID for Guardian Identification, must be stored when persisted
    abstract var nickname: String


    // ............................................................................................. COMPONENTS
    abstract val speciesDescription   : SpeciesDescription
    abstract val individualStatistics : IndividualStatistics
    abstract val abilityGraph         : IAbilityGraph


    // ............................................................................................. OBSERVABLE
    var changedProperty: UpdateType = UpdateType.UNCHANGED
        private set


    // ............................................................................................. DELEGATIONS
    // ............................................................. delegations: SpeciesDescription
    abstract val speciesID: Int
    abstract val commonStatistics: CommonStatistics

    fun setStatisticsChanged()
    {
        setChanged()
        changedProperty = UpdateType.STATS
    }

    fun setAbilitiesChanged()
    {
        setChanged()
        changedProperty = UpdateType.ABILITIES
    }


    // ........................................................... delegations: IndividualStatistics

    /** Returns [IndividualStatistics] component of this Guardian. */
    val stats    : IndividualStatistics get() = individualStatistics

    /** Returns [SpeciesDescription] component of this Guardian. */
    val species  : SpeciesDescription   get() = speciesDescription

    val elements : Array<Element>       get() = species.getElements(currentForm)
    fun isOfElement(element: Element) : Boolean = elements.contains(element, false)


    // ................................................................... delegations: AbilityGraph
    val currentForm : Int get() = abilityGraph.currentForm;


    // ............................................................................................. OBJECT
    override fun equals(other: Any?) = (other != null) && (other is AGuardian) && (other.uuid == uuid)
    override fun hashCode() = uuid.hashCode()


    // ............................................................................... Inner Classes
    enum class UpdateType { UNCHANGED, ABILITIES, STATS }
}
