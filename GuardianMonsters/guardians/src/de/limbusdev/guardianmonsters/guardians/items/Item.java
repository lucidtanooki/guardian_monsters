package de.limbusdev.guardianmonsters.guardians.items;


import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * @author Georg Eckert on 17.02.17.
 */

public abstract class Item
{
    public enum Category
    {
        ALL, MEDICINE, EQUIPMENT, KEY,
    }

    private static int INSTANCE_COUNTER=0;

    public final int ID;
    private String name;
    private Category category;

    public Item(String name, Category category)
    {
        this.name = name;
        this.ID = INSTANCE_COUNTER;
        this.category = category;
        INSTANCE_COUNTER++;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public abstract void apply(Guardian m);

    public abstract boolean applicable(Guardian m);

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Item) {
            return ((Item)obj).getName().equals(name);
        } else {
            return false;
        }
    }
}
