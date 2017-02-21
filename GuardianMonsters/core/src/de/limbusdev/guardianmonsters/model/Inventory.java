package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.OrderedMap;

import java.util.Comparator;
import java.util.Observable;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class Inventory extends Observable {
    private OrderedMap<Item, Integer> items;

    public Inventory() {
        this.items = new OrderedMap<>();
    }

    public OrderedMap<Item, Integer> getItems() {
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
                items.remove(item);
            }
            setChanged();
            notifyObservers(item);
            return item;
        } else {
            return null;
        }
    }

    public Integer getItemAmount(Item item) {
        if(items.containsKey(item)) {
            return items.get(item);
        } else {
            return 0;
        }
    }

    public void sortItemsByID() {
        items.orderedKeys().sort(new IDComparator());
        setChanged();
        notifyObservers();
    }

    private class IDComparator implements Comparator<Item> {

        @Override
        public int compare(Item o1, Item o2) {
            if(o1.ID < o2.ID) return -1;
            if(o1.ID > o2.ID) return 1;
            return 0;
        }
    }
}
