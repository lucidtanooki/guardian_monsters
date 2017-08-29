package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

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
        FileHandle handle = Gdx.files.internal("data/guardians.xml");

        XmlReader xmlReader = new XmlReader();
        XmlReader.Element rootElement;

        try {
            rootElement = xmlReader.parse(handle);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SpeciesDescription ancestor = null;
        for (int i = 0; i < rootElement.getChildCount(); i++) {
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
        // Get Common Guardian Data from DataBase
        SpeciesDescription data = getSpeciesDescription(ID);

        // Copy Base Stats
        IndividualStatistics statistics = new IndividualStatistics(1, data.getBaseStat());

        // Initialize Ability Graph
        IAbilityGraph graph = new AbilityGraph(data);
        graph.activateNode(0);
        graph.setActiveAbility(0,0);

        // Activate Evolution Abilities of Ancestors
        for(int i=0; i<getNumberOfAncestors(ID); i++)
        {
            int metamorphNode = graph.getMetamorphosisNodes().get(i);
            graph.activateNode(metamorphNode);
        }

        String UUID = createNewUUID();
        // Put it all together
        AGuardian guardian = new Guardian(UUID, ID, data, statistics, graph);

        // Return complete Guardian
        return guardian;
    }
}
