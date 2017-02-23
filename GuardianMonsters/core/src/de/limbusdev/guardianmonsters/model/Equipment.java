package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 20.02.17.
 */

public class Equipment extends Item {

    @Override
    public void apply(Monster m) {

    }

    @Override
    public boolean applicable(Monster m) {
        return true;
    }

    public enum EQUIPMENT_TYPE {
        WEAPON, HELMET, ARMOR, SHOES,
    }

    private int addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsEXP;
    private EQUIPMENT_TYPE type;

    public Equipment(String name, EQUIPMENT_TYPE type, int addsPStr, int addsPDef, int addsMStr, int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp) {
        super(name, TYPE.EQUIPMENT, CATEGORY.EQUIPMENT);
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

}
