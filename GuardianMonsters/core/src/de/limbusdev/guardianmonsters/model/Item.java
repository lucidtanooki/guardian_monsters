package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by georg on 17.02.17.
 */

public abstract class Item {
    private static int idCounter=0;
    public final int ID;
    private String name;
    private TYPE type;
    private CATEGORY category;

    public enum CATEGORY {
        ALL, MEDICINE, EQUIPMENT, KEY,
    }

    public enum TYPE {
        ALL, MAGIC_HEAL, PHYSICAL_HEAL, STATUS_HEAL, EQUIPMENT, MAGIC_BUFF, PHYSICAL_BUFF, REVIVE,
    }

    public Item(String name, TYPE type, CATEGORY category) {
        this.name = name;
        this.type = type;
        this.ID = idCounter;
        this.category = category;
        idCounter++;
    }

    public String getName() {
        return name;
    }

    public TYPE getType() {
        return type;
    }

    public CATEGORY getCategory() {
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


    public static class HPCure extends Item {

        private int value;

        public HPCure(String name, int value) {
            super(name, TYPE.PHYSICAL_HEAL, CATEGORY.MEDICINE);
            this.value = value;
        }

        @Override
        public void apply(Monster m) {
            m.healHP(value);
        }

        @Override
        public boolean applicable(Monster m) {
            return (m.getHP() < m.getHPfull() && m.getHP() > 0);
        }
    }

    public static class Reviver extends Item {

        private float fraction;

        public Reviver(String name, float fraction) {
            super(name, TYPE.REVIVE, CATEGORY.MEDICINE);
            this.fraction = fraction;
        }

        @Override
        public void apply(Monster m) {
            m.healHP(MathUtils.round(m.getHPfull()*fraction));
        }

        @Override
        public boolean applicable(Monster m) {
            return m.getHP() == 0;
        }
    }

    public static class MPCure extends Item {

        private int value;

        public MPCure(String name, int value) {
            super(name, TYPE.MAGIC_HEAL, CATEGORY.MEDICINE);
            this.value = value;
        }

        @Override
        public void apply(Monster m) {
            m.healMP(value);
        }

        @Override
        public boolean applicable(Monster m) {
            return m.getMP() < m.getMPfull();
        }
    }
}
