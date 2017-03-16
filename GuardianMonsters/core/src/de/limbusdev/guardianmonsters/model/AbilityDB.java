package de.limbusdev.guardianmonsters.model;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.monsters.Element;
import de.limbusdev.guardianmonsters.utils.XMLAbilityParser;

/**
 * Contains all existing attacks, sorted by element
 * @author Georg Eckert 2017
 */
public class AbilityDB {

    private static AbilityDB instance;
    private ArrayMap<Element, ArrayMap<Integer, Ability>> abilities;

    private AbilityDB() {
        abilities = new ArrayMap<>();

        FileHandle handle = Gdx.files.internal("data/attacks.xml");
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element element;
        try {
            element = xmlReader.parse(handle);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < element.getChildCount(); i++) {
            Ability att = XMLAbilityParser.parseAbility(element.getChild(i));
            if(!abilities.containsKey(att.element)) {
                abilities.put(att.element, new ArrayMap<Integer, Ability>());
            }
            abilities.get(att.element).put(att.ID, att);
        }
    }

    public static AbilityDB getInstance() {
        if(instance == null) {
            instance = new AbilityDB();
        }
        return instance;
    }

    /* .............................................................
    /**
     * Returns attack of the given element and index
     * @param e
     * @param index
     * @return
     */
    public static Ability getAttack(Element e, int index) {
        AbilityDB db = getInstance();
        return db.abilities.get(e).get(index);
    }

}