package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemDB;

/**
 * SerializableInventory
 *
 * @author Georg Eckert 2017
 */

public class SerializableInventory {

    public String[] items;
    public int[] amount;

    @ForSerializationOnly
    public SerializableInventory() {}

    public SerializableInventory(Inventory inventory) {
        items = new String[inventory.getItems().size];
        amount = new int[inventory.getItems().size];

        int counter=0;
        for(Item item : inventory.getItems().keys()) {
            items[counter] = item.getName();
            amount[counter] = inventory.getItems().get(item);
            counter++;
        }
    }


    public static Inventory deserialize(SerializableInventory sInventory) {

        Inventory inventory = new Inventory();

        for(int i=0; i<sInventory.items.length; i++) {
            for(int j=0; j<sInventory.amount[i]; j++) {
                inventory.putItemInInventory(ItemDB.getItem(sInventory.items[i]));
            }
        }

        return inventory;
    }
}