package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 20.02.17.
 */

public abstract class Equipment extends Item {

    public enum HandEquipment {
        SWORD, PATA, BRACELET, CLAWS,
    }

    public enum HeadEquipment {
        HELMET, BRIDLE, MASK, HEADBAND,
    }

    public enum BodyEquipment {
        ARMOR, BARDING, SHIELD, BREASTPLATE,
    }

    public enum FootEquipment {
        SHOES, SHINPROTECTION, HORSESHOE, KNEEPADS,
    }

    @Override
    public void apply(Monster m) {

    }

    public enum EQUIPMENT_TYPE {
        HANDS, HEAD, BODY, FEET,
    }

    private int addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsEXP;
    private EQUIPMENT_TYPE type;

    public Equipment(String name, EQUIPMENT_TYPE type, int addsPStr, int addsPDef, int addsMStr, int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp) {
        super(name, CATEGORY.EQUIPMENT);
        this.addsPStr = addsPStr;
        this.addsPDef = addsPDef;
        this.addsMStr = addsMStr;
        this.addsMDef = addsMDef;
        this.addsSpeed = addsSpeed;
        this.addsHP = addsHP;
        this.addsMP = addsMP;
        this.type = type;
        this.addsEXP = addsExp;
    }



    public int getAddsPStr() {
        return addsPStr;
    }

    public int getAddsPDef() {
        return addsPDef;
    }

    public int getAddsMStr() {
        return addsMStr;
    }

    public int getAddsMDef() {
        return addsMDef;
    }

    public int getAddsSpeed() {
        return addsSpeed;
    }

    public int getAddsHP() {
        return addsHP;
    }

    public int getAddsMP() {
        return addsMP;
    }

    public int getAddsEXP() {
        return addsEXP;
    }

    public EQUIPMENT_TYPE getEquipmentType() {
        return type;
    }

    public static class Hands extends Equipment {

        private HandEquipment type;

        public Hands(String name, HandEquipment type, int addsPStr, int addsPDef, int addsMStr, int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp) {
            super(name, EQUIPMENT_TYPE.HANDS, addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsExp);
            this.type = type;
        }

        public HandEquipment getType() {
            return type;
        }

        @Override
        public boolean applicable(Monster m) {
            MonsterData msi = MonsterDB.singleton().getStatusInfos().get(m.ID);
            return (msi.getCompatibleHandEquip().equals(type) && m.abilityGraph.learntEquipment.contains(EQUIPMENT_TYPE.HANDS, false));
        }
    }

    public static class Feet extends Equipment {

        private FootEquipment type;

        public Feet(String name, FootEquipment type, int addsPStr, int addsPDef, int addsMStr, int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp) {
            super(name, EQUIPMENT_TYPE.FEET, addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsExp);
            this.type = type;
        }

        public FootEquipment getType() {
            return type;
        }

        @Override
        public boolean applicable(Monster m) {
            MonsterData msi = MonsterDB.singleton().getStatusInfos().get(m.ID);
            return (msi.getCompatibleFootEquip().equals(type) && m.abilityGraph.learntEquipment.contains(EQUIPMENT_TYPE.FEET, false));
        }
    }

    public static class Head extends Equipment {

        private HeadEquipment type;

        public Head(String name, HeadEquipment type, int addsPStr, int addsPDef, int addsMStr, int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp) {
            super(name, EQUIPMENT_TYPE.HEAD, addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsExp);
            this.type = type;
        }

        public HeadEquipment getType() {
            return type;
        }

        @Override
        public boolean applicable(Monster m) {
            MonsterData msi = MonsterDB.singleton().getStatusInfos().get(m.ID);
            return (msi.getCompatibleHeadEquip().equals(type) && m.abilityGraph.learntEquipment.contains(EQUIPMENT_TYPE.HEAD, false));
        }
    }

    public static class Body extends Equipment {

        private BodyEquipment type;

        public Body(String name, BodyEquipment type, int addsPStr, int addsPDef, int addsMStr, int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp) {
            super(name, EQUIPMENT_TYPE.BODY, addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsExp);
            this.type = type;
        }

        public BodyEquipment getType() {
            return type;
        }

        @Override
        public boolean applicable(Monster m) {
            MonsterData msi = MonsterDB.singleton().getStatusInfos().get(m.ID);
            return (msi.getCompatibleBodyEquip().equals(type)  && m.abilityGraph.learntEquipment.contains(EQUIPMENT_TYPE.BODY, false));
        }
    }


}
