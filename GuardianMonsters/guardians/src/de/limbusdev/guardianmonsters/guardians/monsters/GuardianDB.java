package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.guardians.XMLMonsterParser;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;


/**
 * Design Pattern: Factory Method
 *
 * @author Georg Eckert 2017
 */
public class GuardianDB extends AGuardianFactory
{
    private static GuardianDB instance;


    // ............................................................................................. ATTRIBUTES

    private ArrayMap<Integer, SpeciesDescription> speciesDB;


    // ............................................................................................. CONSTRUCTORS

    public static GuardianDB getInstance() {
        if(instance == null) instance = new GuardianDB();
        return instance;
    }

    private GuardianDB()
    {
        speciesDB = new ArrayMap<>();

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
            SpeciesDescription info = XMLMonsterParser.parseMonster(rootElement.getChild(i), ancestor);
            speciesDB.put(info.getID(),info);
            if(info.getMetamorphsTo() == info.getID()+1) {
                ancestor = info;
            } else {
                ancestor = null;
            }
        }

    }

    public static ArrayMap<Integer, SpeciesDescription> getSpeciesDB() {
        GuardianDB db = getInstance();
        return db.speciesDB;
    }

    public static SpeciesDescription getData(int monsterID) {
        GuardianDB db = getInstance();
        return db.speciesDB.get(monsterID);
    }

    public static String getNameById(int id) {
        GuardianDB db = getInstance();
        return db.speciesDB.get(id).getNameID();
    }

    public static int getNumberOfAncestors(int id) {
        if(id == 1) return 0;

        boolean hasAncestor = true;
        int ancestors = 0;

        while(hasAncestor) {
            SpeciesDescription possibleAncestor = getData(id - 1 - ancestors);
            if(possibleAncestor != null) {
                if (possibleAncestor.getMetamorphsTo() == id - ancestors) {
                    ancestors++;
                } else {
                    hasAncestor = false;
                }
            } else {
                hasAncestor = false;
            }
        }

        return ancestors;
    }

    /**
     * FACTORY METHOD
     *
     * Takes full responsibility of creating a guardian.
     *
     * @param ID    ID of the guardian to be created, defines species
     * @param level level if the guardian to be created
     * @return      the complete generated guardian
     */
    @Override
    public AGuardian createGuardian(int ID, int level)
    {
        // Get Common Guardian Data from DataBase
        SpeciesDescription data = getData(ID);

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
