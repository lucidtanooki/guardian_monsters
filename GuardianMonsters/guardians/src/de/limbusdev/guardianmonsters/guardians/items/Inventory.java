package de.limbusdev.guardianmonsters.guardians.items;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.Comparator;

/**
 * @author Georg Eckert 2017
 */

public class Inventory extends Signal<ItemSignal> {
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

        dispatch(new ItemSignal(item, ItemSignal.Message.ADDED));
    }

    public Item takeItemFromInventory(Item item) {
        if(items.containsKey(item)) {
            items.put(item,items.get(item)-1);
            if(items.get(item) < 1) {
                items.remove(item);
            }

            dispatch(new ItemSignal(item, ItemSignal.Message.DELETED));

            return item;
        } else {
            return null;
        }
    }

    public boolean containsItem(Item item) {
        if(getItemAmount(item) == 0) return false;
        else return true;
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

        dispatch(null);
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
