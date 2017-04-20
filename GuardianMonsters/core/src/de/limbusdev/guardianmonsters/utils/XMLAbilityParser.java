package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.utils.XmlReader;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AnimationType;
import de.limbusdev.guardianmonsters.media.SFXType;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.abilities.DamageType;
import de.limbusdev.guardianmonsters.enums.Element;

/**
 * XMLAbilityParser
 *
 * @author Georg Eckert 2017
 */

public class XMLAbilityParser {

    public static Ability parseAbility(XmlReader.Element element) {
        Element e = Element.valueOf(element.getAttribute("element", "none").toUpperCase());
        DamageType a = DamageType.valueOf(element.get("category", "physical").toUpperCase());
        int id = element.getIntAttribute("id", 0);
        int damage = element.getInt("damage", 0);
        String nameID = element.getAttribute("nameID");
        SFXType sfxType = SFXType.valueOf(element.getChildByName("sfx").getAttribute("type").toUpperCase());
        int sfxIndex = element.getChildByName("sfx").getIntAttribute("index", 0);
        AnimationType animType = AnimationType.valueOf(element.get("animation", "none").toUpperCase());

        Ability att;
        if(element.getChildByName("mpcost") == null) {
            att = new Ability(id, a, e, damage, nameID, sfxType, sfxIndex, animType);
        } else {
            int mpcost = element.getInt("mpcost", 0);
            att = new Ability(id, a, e, damage, nameID, sfxType, sfxIndex, animType, mpcost);
        }
        return att;
    }

}
