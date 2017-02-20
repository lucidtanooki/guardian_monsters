package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 17.02.17.
 */

public abstract class Item {
    private String name;
    private TYPE type;

    public enum TYPE {
        MAGIC_HEAL, PHYSICAL_HEAL, STATUS_HEAL, EQUIPMENT, MAGIC_BUFF, PHYSICAL_BUFF, REVIVE,
    }

    public Item(String name, TYPE type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
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


    public static class Bread extends Item {

        public Bread() {
            super("bread", TYPE.PHYSICAL_HEAL);
        }

        @Override
        public void apply(Monster m) {
            m.healHP(10);
        }

        @Override
        public boolean applicable(Monster m) {
            return m.getHP() < m.getHPfull();
        }
    }

    public static class AngelTear extends Item {

        public AngelTear() {
            super("angel-tear", TYPE.REVIVE);
        }

        @Override
        public void apply(Monster m) {
            m.healHP(m.getHPfull()/2);
        }

        @Override
        public boolean applicable(Monster m) {
            return m.getHP() == 0;
        }
    }

    public static class MedicineBlue extends Item {

        public MedicineBlue() {
            super("medicine-blue", TYPE.MAGIC_HEAL);
        }

        @Override
        public void apply(Monster m) {
            m.healMP(10);
        }

        @Override
        public boolean applicable(Monster m) {
            return m.getMP() < m.getMPfull();
        }
    }
}
