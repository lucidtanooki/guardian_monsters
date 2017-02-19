package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;

/**
 * Created by georg on 17.02.17.
 */

public class Inventory extends Observable {
    private ArrayMap<Item, Integer> items;

    public Inventory() {
        this.items = new ArrayMap<>();
    }

    public ArrayMap<Item, Integer> getItems() {
        return items;
    }

    public void putItemInInventory(Item item) {
        if(items.containsKey(item)) {
            items.put(item, items.get(item)+1);
        } else {
            items.put(item,1);
        }
        setChanged();
        notifyObservers(item);
    }

    public Item takeItemFromInventory(Item item) {
        if(items.containsKey(item)) {
            items.put(item,items.get(item)-1);
            if(items.get(item) < 1) {
                items.removeKey(item);
            }
            setChanged();
            notifyObservers(item);
            return item;
        } else {
            return null;
        }
    }
}
