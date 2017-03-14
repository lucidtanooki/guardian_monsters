package de.limbusdev.guardianmonsters.model;

/**
 * BaseStat contains all base values. BaseStats are stats of a monster at level 1 without any
 * additional stuff. On creating a new monster, the BaseStat values are copied over.
 *
 * The Basic Status Values (BaseStats) are:
 *
 * HP   ..  Health Points
 * MP   ..  Magic Points
 * PStr ..  Physical Strength
 * PDef ..  Physical Defense
 * MStr ..  Magical Strength
 * MDef ..  Magical Defense
 * Speed
 *
 * Created by Georg Eckert 2016
 */

public class BaseStat {

    public final int ID;
    public final int baseHP;
    public final int baseMP;
    public final int basePStr;
    public final int basePDef;
    public final int baseMStr;
    public final int baseMDef;
    public final int baseSpeed;

    public BaseStat(int ID) {
        this(ID, 300, 100, 10, 10, 10, 10, 10);
    }

    public BaseStat(int ID, int baseHP, int baseMP, int basePStr, int basePDef, int baseMStr, int baseMDef, int baseSpeed) {
        this.ID = ID;
        this.baseHP = baseHP;
        this.baseMP = baseMP;
        this.basePStr = basePStr;
        this.basePDef = basePDef;
        this.baseMStr = baseMStr;
        this.baseMDef = baseMDef;
        this.baseSpeed = baseSpeed;
    }
}
