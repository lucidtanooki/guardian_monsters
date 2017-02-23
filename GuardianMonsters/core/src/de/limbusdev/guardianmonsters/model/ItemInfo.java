package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

/**
 * ItemInfo provides information about all items. Items are created from data/items.xml
 *
 * Created by Georg Eckerton 20.02.17.
 */

public class ItemInfo {

    private static ItemInfo instance;
    private ArrayMap<String,Item> items;

    private ItemInfo() {
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
            Item item = parseXmlItem(element.getChild(i));
            if(!items.containsKey(item.getName())) {
                items.put(item.getName(), item);
            }
        }
    }


    // ................................................................................. XML PARSING
    private Item parseXmlItem(XmlReader.Element e) {
        Item item;

        try {
            switch (e.getName()) {
                case "HPCure":
                    item = parseHPCuringItem(e);
                    break;
                case "MPCure":
                    item = parseMPCuringItem(e);
                    break;
                case "Reviver":
                    item = parseStatusCuringItem(e);
                    break;
                case "Equipment":
                    item = parseWeaponItem(e);
                    break;
                case "Key":
                    item = parseKeyItem(e);
                    break;
                default:
                    item = new Item.HPCure("Water", 0);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            item = new Item.HPCure("Water", 0);
        }

        return item;
    }

    private Item parseHPCuringItem(XmlReader.Element e) {
        return new Item.HPCure(e.get("name", "Water"), e.getInt("value", 0));
    }

    private Item parseMPCuringItem(XmlReader.Element e) {
        return new Item.MPCure(e.get("name", "Water"), e.getInt("value", 0));
    }

    private Item parseStatusCuringItem(XmlReader.Element e) {
        return new Item.Reviver(e.get("name", "Water"), e.getFloat("fraction", 0f));
    }

    private Item parseKeyItem(XmlReader.Element e) {
        return new Item.Key(e.get("name", "Water"));
    }

    private Item parseWeaponItem(XmlReader.Element e) {
        Equipment.EQUIPMENT_TYPE type;
        String typeStr = e.get("equipment-type", "weapon");
        switch(typeStr) {
            case "weapon":
                type = Equipment.EQUIPMENT_TYPE.WEAPON;
                break;
            case "armor":
                type = Equipment.EQUIPMENT_TYPE.ARMOR;
                break;
            case "helmet":
                type = Equipment.EQUIPMENT_TYPE.HELMET;
                break;
            case "shoes":
                type = Equipment.EQUIPMENT_TYPE.SHOES;
                break;
            default:
                type = Equipment.EQUIPMENT_TYPE.WEAPON;
                break;
        }
        return new Equipment(
            e.get("name", "claws-wood"),
            type,
            e.getInt("addsPStr", 0),
            e.getInt("addsPDef", 0),
            e.getInt("addsMStr", 0),
            e.getInt("addsMDef", 0),
            e.getInt("addsSpeed", 0),
            e.getInt("addsHP", 0),
            e.getInt("addsMP", 0)
            );
    }




    // ............................................................................. GETTER & SETTER
    public static ItemInfo getInst() {
        if(instance == null) {
            instance = new ItemInfo();
        }
        return instance;
    }

    public Item getItem(String name) {
        return items.get(name);
    }

}
