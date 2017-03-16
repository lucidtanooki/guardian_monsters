package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.items.BodyEquipment;
import de.limbusdev.guardianmonsters.model.items.BodyPart;
import de.limbusdev.guardianmonsters.model.items.FootEquipment;
import de.limbusdev.guardianmonsters.model.items.HandEquipment;
import de.limbusdev.guardianmonsters.model.items.HeadEquipment;
import de.limbusdev.guardianmonsters.model.monsters.Element;
import de.limbusdev.guardianmonsters.model.monsters.BaseStat;
import de.limbusdev.guardianmonsters.model.monsters.MonsterData;
import de.limbusdev.guardianmonsters.utils.XMLMonsterParser;


/**
 * @author Georg Eckert 2017
 */
public class MonsterDB {
    /* ............................................................................ ATTRIBUTES .. */
    private ArrayMap<Integer, MonsterData> statusInfos;
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

        MonsterData ancestor = null;
        for (int i = 0; i < rootElement.getChildCount(); i++) {
            MonsterData info = XMLMonsterParser.parseMonster(rootElement.getChild(i), ancestor);
            statusInfos.put(info.ID,info);
            if(info.metamorphesTo == i+1) {
                ancestor = info;
            } else {
                ancestor = null;
            }
        }

    }

    public static ArrayMap<Integer, MonsterData> getStatusInfos() {
        MonsterDB db = getInstance();
        return db.statusInfos;
    }

    public static MonsterData getData(int monsterID) {
        MonsterDB db = getInstance();
        return db.statusInfos.get(monsterID);
    }

    public static String getNameById(int id) {
        MonsterDB db = getInstance();
        return db.statusInfos.get(id).nameID;
    }

    public static String getLocalNameById(int id) {
        return Services.getL18N().i18nMonsters().get(getNameById(id));
    }
}
