package de.limbusdev.guardianmonsters.guardians;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.guardians.monsters.GuardianData;


/**
 * @author Georg Eckert 2017
 */
public class MonsterDB {
    /* ............................................................................ ATTRIBUTES .. */
    private ArrayMap<Integer, GuardianData> statusInfos;
    private static MonsterDB instance;


    /* ........................................................................... CONSTRUCTOR .. */

    public static MonsterDB getInstance() {
        if(instance == null) instance = new MonsterDB();
        return instance;
    }

    private MonsterDB() {
        statusInfos = new ArrayMap<>();

        FileHandle handle = Gdx.files.internal("data/guardians.xml");
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element rootElement;

        try {
            rootElement = xmlReader.parse(handle);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        GuardianData ancestor = null;
        for (int i = 0; i < rootElement.getChildCount(); i++) {
            GuardianData info = XMLMonsterParser.parseMonster(rootElement.getChild(i), ancestor);
            statusInfos.put(info.getID(),info);
            if(info.getMetamorphesTo() == info.getID()+1) {
                ancestor = info;
            } else {
                ancestor = null;
            }
        }

    }

    public static ArrayMap<Integer, GuardianData> getStatusInfos() {
        MonsterDB db = getInstance();
        return db.statusInfos;
    }

    public static GuardianData getData(int monsterID) {
        MonsterDB db = getInstance();
        return db.statusInfos.get(monsterID);
    }

    public static String getNameById(int id) {
        MonsterDB db = getInstance();
        return db.statusInfos.get(id).getNameID();
    }

    public static int getNumberOfAncestors(int id) {
        if(id == 1) return 0;

        boolean hasAncestor = true;
        int ancestors = 0;

        while(hasAncestor) {
            GuardianData possibleAncestor = getData(id - 1 - ancestors);
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
}
