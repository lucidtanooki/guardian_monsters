package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 21.11.16.
 */

public class BaseStat {
    public int ID;
    public int baseHP;
    public int baseMP;
    public int basePhysStrength;
    public int basePhysDefense;
    public int baseMagStrength;
    public int baseMagDefense;
    public int baseSpeed;

    public BaseStat(int ID) {
        this(ID,99, 20, 49, 49, 26, 26, 49);
    }

    public BaseStat(int ID, int baseHP, int baseMP, int basePhysStrength, int basePhysDefense, int baseMagStrength, int baseMagDefense, int baseSpeed) {
        this.ID = ID;
        this.baseHP = baseHP;
        this.baseMP = baseMP;
        this.basePhysStrength = basePhysStrength;
        this.basePhysDefense = basePhysDefense;
        this.baseMagStrength = baseMagStrength;
        this.baseMagDefense = baseMagDefense;
        this.baseSpeed = baseSpeed;
    }
}
