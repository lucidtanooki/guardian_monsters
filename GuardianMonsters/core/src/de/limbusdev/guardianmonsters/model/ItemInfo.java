package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by georg on 20.02.17.
 */

public class ItemInfo {

    private static ItemInfo instance;
    private ArrayMap<String,Item> items;

    private ItemInfo() {
        items = new ArrayMap<>();

        items.put("bread", new Item.HPCure("bread", 10));
        items.put("potion-blue", new Item.HPCure("potion-blue", 50));
        items.put("potion-red", new Item.HPCure("potion-red", 100));
        items.put("medicine-blue", new Item.MPCure("medicine-blue", 10));
        items.put("angel-tear", new Item.Reviver("angel-tear", .5f));

        items.put("sword-wood", new Equipment("sword-wood", Equipment.EQUIPMENT_TYPE.WEAPON, 1,0,0,0,0,0,0));
        items.put("claws-wood", new Equipment("claws-wood", Equipment.EQUIPMENT_TYPE.WEAPON, 1,0,0,0,0,0,0));
        items.put("sword-rusty", new Equipment("sword-rusty", Equipment.EQUIPMENT_TYPE.WEAPON, 2,0,0,0,0,0,0));
        items.put("claws-rusty", new Equipment("claws-rusty", Equipment.EQUIPMENT_TYPE.WEAPON, 2,0,0,0,0,0,0));
        items.put("sword-iron", new Equipment("sword-iron", Equipment.EQUIPMENT_TYPE.WEAPON, 3,0,0,0,0,0,0));
        items.put("sword-steel", new Equipment("sword-steel", Equipment.EQUIPMENT_TYPE.WEAPON, 4,0,0,0,0,0,0));
        items.put("sword-silver", new Equipment("sword-silver", Equipment.EQUIPMENT_TYPE.WEAPON, 5,0,0,0,0,0,0));
        items.put("sword-titanium", new Equipment("sword-titanium", Equipment.EQUIPMENT_TYPE.WEAPON, 6,0,0,0,0,0,0));
        items.put("sword-knightly-steel", new Equipment("sword-knightly-steel", Equipment.EQUIPMENT_TYPE.WEAPON, 5,0,0,0,0,0,0));
        items.put("sword-broad-steel", new Equipment("sword-broad-steel", Equipment.EQUIPMENT_TYPE.WEAPON, 2,0,0,0,-1,0,0));
        items.put("sword-barb-steel", new Equipment("sword-barb-steel", Equipment.EQUIPMENT_TYPE.WEAPON, 4,0,0,0,1,0,0));
        items.put("sword-gap-steel", new Equipment("sword-gap-steel", Equipment.EQUIPMENT_TYPE.WEAPON, 6,0,0,0,-1,0,0));

    }

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
