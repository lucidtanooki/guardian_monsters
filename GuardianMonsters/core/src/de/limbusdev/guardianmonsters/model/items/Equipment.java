package de.limbusdev.guardianmonsters.model.items;

import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;
import de.limbusdev.guardianmonsters.model.MonsterData;
import de.limbusdev.guardianmonsters.model.Stat;

/**
 * Equipment extends the {@link Stat}s of a monster in the following way:
 *
 * HP   .. by factor (1 + addsHP/100)
 * MP   .. by factor (1 + addsMP/100)
 * PStr .. by adding addsPStr
 * PDef .. by adding addsPDef
 * MStr .. by adding addsMStr
 * MDef .. by adding addsMDef
 * Speed.. by adding addsSpeed
 * EXP  .. by factor (1 + addsEXP/100)
 *
 * @author Georg Eckert
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

    public final int addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsEXP;
    public final EQUIPMENT_TYPE type;

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


    // ........................................................................... DERIVED EQUIPMENT

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
