package de.limbusdev.guardianmonsters.model;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.enums.AnimationType;
import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.enums.SFXType;

/**
 * Contains all existing attacks, sorted by element
 * Created by Georg Eckert 2017
 */
public class AttackInfo {

    private static AttackInfo instance;
    private ArrayMap<Element, ArrayMap<Integer, de.limbusdev.guardianmonsters.model.abilities.Ability>> attacks;

    private AttackInfo() {
        attacks = new ArrayMap<>();

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
            de.limbusdev.guardianmonsters.model.abilities.Ability att = parseAttack(element.getChild(i));
            if(!attacks.containsKey(att.element)) {
                attacks.put(att.element, new ArrayMap<Integer, de.limbusdev.guardianmonsters.model.abilities.Ability>());
            }
            attacks.get(att.element).put(att.ID, att);
        }
    }

    public static AttackInfo getInst() {
        if(instance == null) {
            instance = new AttackInfo();
        }
        return instance;
    }

    /* ............................................................................ ATTRIBUTES .. */

    private de.limbusdev.guardianmonsters.model.abilities.Ability parseAttack(XmlReader.Element element) {
        Element e = Element.valueOf(element.getAttribute("element", "none").toUpperCase());
        AttackType a = AttackType.valueOf(element.get("category", "physical").toUpperCase());
        int id = element.getIntAttribute("id", 0);
        int damage = element.getInt("damage", 0);
        String nameID = element.getAttribute("nameID");
        SFXType sfxType = SFXType.valueOf(element.getChildByName("sfx").getAttribute("type").toUpperCase());
        int sfxIndex = element.getChildByName("sfx").getIntAttribute("index", 0);
        AnimationType animType = AnimationType.valueOf(element.get("animation", "none").toUpperCase());

        de.limbusdev.guardianmonsters.model.abilities.Ability att;
        if(element.getChildByName("mpcost") == null) {
            att = new de.limbusdev.guardianmonsters.model.abilities.Ability(id, a, e, damage, nameID, sfxType, sfxIndex, animType);
        } else {
            int mpcost = element.getInt("mpcost", 0);
            att = new de.limbusdev.guardianmonsters.model.abilities.Ability(id, a, e, damage, nameID, sfxType, sfxIndex, animType, mpcost);
        }
        return att;
    }

    /**
     * Returns attack of the given element and index
     * @param e
     * @param index
     * @return
     */
    public de.limbusdev.guardianmonsters.model.abilities.Ability getAttack(Element e, int index) {
        return attacks.get(e).get(index);
    }

}