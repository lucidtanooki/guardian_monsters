package de.limbusdev.guardianmonsters.guardians.monsters

import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph

/**
 * Monster is the basic entity for the BattleSystem
 *
 * @author Georg Eckert 2015
 */
class Guardian internal constructor(UUID: String) : AGuardian(UUID)
{
// ..................................................................................... CONSTRUCTOR

/**
 * The protected constructor makes it available from the [AGuardianFactory] only.
 *
 * After Object creation the missing components have to be injected.
 * @param UUID
 */

    // ............................................................................................. GETTERS & SETTERS

    override var nickname: String = ""

    // Components
    override lateinit var speciesDescription: SpeciesDescription
        private set
    override lateinit var individualStatistics: IndividualStatistics
        private set
    override lateinit var abilityGraph: IAbilityGraph
        private set

    // ............................................................................................. DELEGATED METHODS

    // delegated to SpeciesDescription Component
    override val speciesID: Int
        get() = speciesDescription.ID

    override val commonStatistics: CommonStatistics
        get() = speciesDescription.commonStatistics


    internal fun injectSpeciesDescription(speciesDescription: SpeciesDescription)
    {
        this.speciesDescription = speciesDescription
    }

    internal fun injectIndividualStatistics(individualStatistics: IndividualStatistics)
    {
        this.individualStatistics = individualStatistics
    }

    internal fun injectAbilityGraph(abilityGraph: IAbilityGraph)
    {
        this.abilityGraph = abilityGraph
    }

    // ............................................................................................. OBJECT
    override fun toString(): String
    {
        return  "\n  +-----------------------------------------------+" +
                "\n  | UUID: ${uuid.padEnd(40)}|" +
                "\n  | Name: ${speciesDescription.getSimpleName(currentForm).toUpperCase().padEnd(40)}|" +
                "\n  | SID:  ${speciesID.toString().padEnd(40)}|" +
                "\n  | Lvl:  ${individualStatistics.level.toString().padEnd(40)}|" +
                "\n  +-----------------------------------------------+"
    }
}
