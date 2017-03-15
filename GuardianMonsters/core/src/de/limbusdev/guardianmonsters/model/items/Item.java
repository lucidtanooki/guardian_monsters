package de.limbusdev.guardianmonsters.model.items;

import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * @author Georg Eckert on 17.02.17.
 */

public abstract class Item {
    private static int INSTANCE_COUNTER=0;

    public final int ID;
    private String name;
    private Category category;

    public enum Category {
        ALL, MEDICINE, EQUIPMENT, KEY,
    }

    public Item(String name, Category category) {
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

    public abstract void apply(Monster m);

    public abstract boolean applicable(Monster m);


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

    // .................................................................................... MEDICINE
    public enum TYPE {
        REVIVE, HP_CURE, MP_CURE, STATUS_CURE,
    }

    public static class Medicine extends Item {

        private int value;
        private TYPE type;

        public Medicine(String name, int value, TYPE type) {
            super(name, Category.MEDICINE);
            this.value = value;
            this.type = type;
        }

        @Override
        public void apply(Monster m) {
            switch(type) {
                case REVIVE:
                    m.stat.healHP(MathUtils.round(m.stat.getHPmax()*value/100f));
                    break;
                case HP_CURE:
                    m.stat.healHP(value);
                    break;
                case MP_CURE:
                    m.stat.healMP(value);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean applicable(Monster m) {
            switch(type) {
                case REVIVE:
                    return m.stat.isKO();
                case HP_CURE:
                    return (m.stat.isFit() && m.stat.getHP() < m.stat.getHPmax());
                case MP_CURE:
                    return m.stat.getMP() < m.stat.getMPmax();
                default:
                    return false;
            }
        }
    }

    public static class Key extends Item {

        public Key(String name) {
            super(name, Category.KEY);
        }

        @Override
        public void apply(Monster m) {
            // Do nothing
        }

        @Override
        public boolean applicable(Monster m) {
            return false;
        }
    }
}
