package de.limbusdev.guardianmonsters.utils;

import com.badlogic.gdx.utils.XmlReader;

import de.limbusdev.guardianmonsters.model.items.BodyEquipment;
import de.limbusdev.guardianmonsters.model.items.BodyPart;
import de.limbusdev.guardianmonsters.model.items.Equipment;
import de.limbusdev.guardianmonsters.model.items.FootEquipment;
import de.limbusdev.guardianmonsters.model.items.HandEquipment;
import de.limbusdev.guardianmonsters.model.items.HeadEquipment;
import de.limbusdev.guardianmonsters.model.items.Item;
import de.limbusdev.guardianmonsters.model.items.KeyItem;
import de.limbusdev.guardianmonsters.model.items.MedicalItem;

/**
 * XMLItemParser
 *
 * @author Georg Eckert 2017
 */

public class XMLItemParser {

    public static Item parseXmlItem(XmlReader.Element e) {
        Item item;

        try {
            switch (e.getName()) {
                case "medicine":
                    item = parseMedicine(e);
                    break;
                case "Equipment":
                    item = parseEquipmentItem(e);
                    break;
                case "Key":
                    item = parseKeyItem(e);
                    break;
                default:
                    item = new MedicalItem("Water", 0, MedicalItem.Type.HP_CURE);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            item = new MedicalItem("Water", 0, MedicalItem.Type.HP_CURE);
        }

        return item;
    }

    private static Item parseMedicine(XmlReader.Element element) {
        switch(element.get("type")) {
            case "HPcure":
                return parseHPCuringItem(element);
            case "MPcure":
                return parseMPCuringItem(element);
            case "revive":
                return parseRevivingItem(element);
            default:
                return new MedicalItem("Water", 0, MedicalItem.Type.HP_CURE);
        }
    }

    private static Item parseHPCuringItem(XmlReader.Element e) {
        return new MedicalItem(e.get("nameID", "Water"), e.getInt("value", 0), MedicalItem.Type.HP_CURE);
    }

    private static Item parseMPCuringItem(XmlReader.Element e) {
        return new MedicalItem(e.get("nameID", "Water"), e.getInt("value", 0), MedicalItem.Type.MP_CURE);
    }

    private static Item parseRevivingItem(XmlReader.Element e) {
        return new MedicalItem(e.get("nameID", "Water"), e.getInt("fraction", 0), MedicalItem.Type.REVIVE);
    }

    private static Item parseKeyItem(XmlReader.Element e) {
        return new KeyItem(e.get("nameID", "Water"));
    }

    private static Item parseEquipmentItem(XmlReader.Element e) {
        BodyPart bodyPart = BodyPart.valueOf(e.get("body-part", "hands").toUpperCase());

        String nameID = e.get("nameID", "claws-wood");
        int pStr = e.getInt("addsPStr",    0);
        int pDef = e.getInt("addsPDef",    0);
        int mStr = e.getInt("addsMStr",    0);
        int mDef = e.getInt("addsMDef",    0);
        int speed = e.getInt("addsSpeed",   0);
        int hp = e.getInt("addsHP",      0);
        int mp = e.getInt("addsMP",      0);
        int exp = e.getInt("addsEXP",     0);
        String type = e.getChildByName("body-part").getAttribute("type").toUpperCase();

        Equipment equip;
        switch(bodyPart) {
            case HEAD:
                equip = new HeadEquipment(nameID, HeadEquipment.Type.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            case BODY:
                equip = new BodyEquipment(nameID, BodyEquipment.Type.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            case FEET:
                equip = new FootEquipment(nameID, FootEquipment.Type.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            default:
                // HandEquipment
                equip = new HandEquipment(nameID, HandEquipment.Type.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
        }

        return equip;
    }
}
