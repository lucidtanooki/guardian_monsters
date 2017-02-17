package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 17.02.17.
 */

public class Item {
    private String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }






    public boolean equals(Item item) {
        return name.equals(item.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item) {
            return equals((Item)obj);
        } else {
            return false;
        }
    }
}
