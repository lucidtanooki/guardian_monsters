package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.model.items.Item;

/**
 * ItemDB provides information about all items. Items are created from data/items.xml
 *
 * Created by Georg Eckerton 20.02.17.
 */

public class ItemDB {

    private static ItemDB instance;
    private ArrayMap<String, de.limbusdev.guardianmonsters.model.items.Item> items;

    private ItemDB() {
        items = new ArrayMap<>();

        FileHandle handle = Gdx.files.internal("data/items.xml");
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element element;
        try {
            element = xmlReader.parse(handle);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for(int i=0; i<element.getChildCount(); i++) {
            de.limbusdev.guardianmonsters.model.items.Item item = parseXmlItem(element.getChild(i));
            if(!items.containsKey(item.getName())) {
                items.put(item.getName(), item);
            }
        }
    }


    // ................................................................................. XML PARSING
    private de.limbusdev.guardianmonsters.model.items.Item parseXmlItem(XmlReader.Element e) {
        de.limbusdev.guardianmonsters.model.items.Item item;

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
                    item = new de.limbusdev.guardianmonsters.model.items.Item.Medicine("Water", 0, de.limbusdev.guardianmonsters.model.items.Item.TYPE.HP_CURE);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            item = new de.limbusdev.guardianmonsters.model.items.Item.Medicine("Water", 0, de.limbusdev.guardianmonsters.model.items.Item.TYPE.HP_CURE);
        }

        return item;
    }

    private de.limbusdev.guardianmonsters.model.items.Item parseMedicine(XmlReader.Element element) {
        switch(element.get("type")) {
            case "HPcure":
                return parseHPCuringItem(element);
            case "MPcure":
                return parseMPCuringItem(element);
            case "revive":
                return parseRevivingItem(element);
            default:
                return new de.limbusdev.guardianmonsters.model.items.Item.Medicine("Water", 0, de.limbusdev.guardianmonsters.model.items.Item.TYPE.HP_CURE);
        }
    }

    private de.limbusdev.guardianmonsters.model.items.Item parseHPCuringItem(XmlReader.Element e) {
        return new de.limbusdev.guardianmonsters.model.items.Item.Medicine(e.get("nameID", "Water"), e.getInt("value", 0), de.limbusdev.guardianmonsters.model.items.Item.TYPE.HP_CURE);
    }

    private de.limbusdev.guardianmonsters.model.items.Item parseMPCuringItem(XmlReader.Element e) {
        return new de.limbusdev.guardianmonsters.model.items.Item.Medicine(e.get("nameID", "Water"), e.getInt("value", 0), de.limbusdev.guardianmonsters.model.items.Item.TYPE.MP_CURE);
    }

    private de.limbusdev.guardianmonsters.model.items.Item parseRevivingItem(XmlReader.Element e) {
        return new de.limbusdev.guardianmonsters.model.items.Item.Medicine(e.get("nameID", "Water"), e.getInt("fraction", 0), de.limbusdev.guardianmonsters.model.items.Item.TYPE.REVIVE);
    }

    private de.limbusdev.guardianmonsters.model.items.Item parseKeyItem(XmlReader.Element e) {
        return new de.limbusdev.guardianmonsters.model.items.Item.Key(e.get("nameID", "Water"));
    }

    private de.limbusdev.guardianmonsters.model.items.Item parseEquipmentItem(XmlReader.Element e) {
        de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE bodyPart = de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE.valueOf(e.get("body-part", "hands").toUpperCase());

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

        de.limbusdev.guardianmonsters.model.items.Equipment equip;
        switch(bodyPart) {
            case HEAD:
                equip = new de.limbusdev.guardianmonsters.model.items.Equipment.Head(nameID, de.limbusdev.guardianmonsters.model.items.Equipment.HeadEquipment.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            case BODY:
                equip = new de.limbusdev.guardianmonsters.model.items.Equipment.Body(nameID, de.limbusdev.guardianmonsters.model.items.Equipment.BodyEquipment.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            case FEET:
                equip = new de.limbusdev.guardianmonsters.model.items.Equipment.Feet(nameID, de.limbusdev.guardianmonsters.model.items.Equipment.FootEquipment.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            default:
                // Hands
                equip = new de.limbusdev.guardianmonsters.model.items.Equipment.Hands(nameID, de.limbusdev.guardianmonsters.model.items.Equipment.HandEquipment.valueOf(type), pStr,
                    pDef, mStr, mDef, speed, hp, mp, exp);
                break;
        }

        return equip;
    }




    // ............................................................................. GETTER & SETTER
    public static ItemDB singleton() {
        if(instance == null) {
            instance = new ItemDB();
        }
        return instance;
    }

    public Item getItem(String name) {
        return items.get(name);
    }

}
