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
    // ............................................................................................. ATTRIBUTES
    private ArrayMap<Integer, SpeciesData> speciesDB;
    private static GuardianDB instance;


    // ............................................................................................. CONSTRUCTOR
    public static GuardianDB getInstance() {
        if(instance == null) instance = new GuardianDB();
        return instance;
    }

    private GuardianDB() {
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

        SpeciesData ancestor = null;
        for (int i = 0; i < rootElement.getChildCount(); i++) {
            SpeciesData info = XMLMonsterParser.parseMonster(rootElement.getChild(i), ancestor);
            speciesDB.put(info.getID(),info);
            if(info.getMetamorphesTo() == info.getID()+1) {
                ancestor = info;
            } else {
                ancestor = null;
            }
        }

    }

    public static ArrayMap<Integer, SpeciesData> getSpeciesDB() {
        GuardianDB db = getInstance();
        return db.speciesDB;
    }

    public static SpeciesData getData(int monsterID) {
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
            SpeciesData possibleAncestor = getData(id - 1 - ancestors);
            if(possibleAncestor != null) {
                if (possibleAncestor.getMetamorphesTo() == id - ancestors) {
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
        SpeciesData data = getData(ID);

        // Copy Base Stats
        Stat stat = new Stat(1, data.getBaseStat());

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

        // Put it all together
        AGuardian guardian = new Guardian(ID, data, stat, graph);

        // Return complete Guardian
        return guardian;
    }
}
