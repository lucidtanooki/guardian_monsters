package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

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

    private GuardianFactory()
    {
        // Read common Guardian Descriptions from XML file
        FileHandle handle = Gdx.files.internal("data/guardians.xml");

        XmlReader.Element rootElement = XMLGuardianParser.parseGuardianList(handle.readString());

        SpeciesDescription ancestor = null;
        for (int i = 0; i < rootElement.getChildCount(); i++)
        {
            SpeciesDescription info = XMLGuardianParser.parseMonster(rootElement.getChild(i), ancestor);
            getSpeciesDB().put(info.getID(),info);

            if(info.getMetamorphsTo() == info.getID()+1) {
                ancestor = info;
            } else {
                ancestor = null;
            }
        }
    }

    @Override
    public AGuardian createGuardian(int ID, int level)
    {
        // ...................................................................... create core object

        String UUID = createNewUUID();
        Guardian newGuardian = new Guardian(UUID);


        // ....................................................................... create components

        // Component 1: SpeciesDescription - Get Common Guardian Data from DataBase
        SpeciesDescription speciesDescription = getSpeciesDescription(ID);

        // Component 2: IndividualStatistics - Copy Base Stats
        IndividualStatistics individualStatistics = new IndividualStatistics(newGuardian, 1);

        // Component 3: AbilityGraph - Initialize Ability Graph
        IAbilityGraph abilityGraph = new AbilityGraph(speciesDescription);
        abilityGraph.activateNode(0);
        abilityGraph.setActiveAbility(0,0);

        // Activate Evolution Abilities of Ancestors
        for(int i=0; i<getNumberOfAncestors(ID); i++)
        {
            int metamorphosisNode = abilityGraph.getMetamorphosisNodes().get(i);
            abilityGraph.activateNode(metamorphosisNode);
        }


        // ....................................................................... inject components

        newGuardian.injectSpeciesDescription(speciesDescription);
        newGuardian.injectIndiviualStatistics(individualStatistics);
        newGuardian.injectAbilityGraph(abilityGraph);

        // Return complete Guardian
        return newGuardian;
    }
}
