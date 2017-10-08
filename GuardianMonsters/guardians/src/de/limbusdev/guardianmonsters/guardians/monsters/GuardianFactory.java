package de.limbusdev.guardianmonsters.guardians.monsters;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;


/**
 * Design Pattern: Factory Method, Singleton
 *
 * @author Georg Eckert 2017
 */
public class GuardianFactory extends AGuardianFactory
{
    private static AGuardianFactory instance;

    public static AGuardianFactory getInstance()
    {
        if(instance == null) instance = new GuardianFactory();
        return instance;
    }

    private GuardianFactory() {}

    @Override
    public AGuardian createGuardian(int ID, int level)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        // ...................................................................... create core object

        String UUID = createNewUUID();
        Guardian newGuardian = new Guardian(UUID);


        // ....................................................................... create components

        // Component 1: SpeciesDescription - Get Common Guardian Data from DataBase
        SpeciesDescription speciesDescription = species.getSpeciesDescription(ID);
        newGuardian.injectSpeciesDescription(speciesDescription);

        // Component 2: IndividualStatistics - Copy Base Stats
        IndividualStatistics individualStatistics =
            new IndividualStatistics(newGuardian, speciesDescription.getCommonStatistics(), level);

        // Component 3: AbilityGraph - Initialize Ability Graph
        IAbilityGraph abilityGraph = new AbilityGraph(newGuardian, speciesDescription);
        abilityGraph.activateNode(0);
        abilityGraph.setActiveAbility(0,0);

        // Activate Evolution Abilities of Ancestors
        for(int i = 0; i < species.getNumberOfAncestors(ID); i++)
        {
            int metamorphosisNode = abilityGraph.getMetamorphosisNodes().get(i);
            abilityGraph.activateNode(metamorphosisNode);
        }


        // ....................................................................... inject components

        newGuardian.injectIndiviualStatistics(individualStatistics);
        newGuardian.injectAbilityGraph(abilityGraph);

        // Return complete Guardian
        return newGuardian;
    }

    @Override
    public void destroy()
    {
        super.destroy();
        instance = null;
    }
}
