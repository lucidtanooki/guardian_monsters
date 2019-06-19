package de.limbusdev.guardianmonsters.guardians.monsters

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph


/**
 * Design Pattern: Factory Method, Singleton
 *
 * @author Georg Eckert 2017
 */
object GuardianFactory : AGuardianFactory()
{
    override fun createGuardian(ID: Int, level: Int): AGuardian
    {
        val species = GuardiansServiceLocator.species
        // ...................................................................... create core object

        val UUID = createNewUUID()
        val newGuardian = Guardian(UUID)

        // ....................................................................... create components

        // Component 1: SpeciesDescription - Get Common Guardian Data from DataBase
        val speciesDescription = species.getSpeciesDescription(ID)
        newGuardian.injectSpeciesDescription(speciesDescription)

        // Component 2: IndividualStatistics - Copy Base Stats
        val individualStatistics = IndividualStatistics(newGuardian, speciesDescription.commonStatistics, level)

        // Component 3: AbilityGraph - Initialize Ability Graph
        val abilityGraph = AbilityGraph(newGuardian, speciesDescription)
        abilityGraph.activateNode(0)
        abilityGraph.setActiveAbility(0, 0)

        // Activate Evolution Abilities of Ancestors
        //        for(int i = 0; i < species.getNumberOfAncestors(ID); i++)
        //        {
        //            int metamorphosisNode = abilityGraph.getMetamorphosisNodes().get(i);
        //            abilityGraph.activateNode(metamorphosisNode);
        //        }

        // ....................................................................... inject components

        newGuardian.injectIndividualStatistics(individualStatistics)
        newGuardian.injectAbilityGraph(abilityGraph)

        // Return complete Guardian
        return newGuardian
    }
}
