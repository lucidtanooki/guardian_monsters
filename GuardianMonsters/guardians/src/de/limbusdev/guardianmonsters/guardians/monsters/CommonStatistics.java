package de.limbusdev.guardianmonsters.guardians.monsters;

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
 * Base Stats are common to all Guardians of the same species, and therefore are part of the
 * {@link SpeciesDescription}.
 *
 * @author Georg Eckert 2016
 */

public class CommonStatistics
{
    private int ID;
    private int baseHP;
    private int baseMP;
    private int basePStr;
    private int basePDef;
    private int baseMStr;
    private int baseMDef;
    private int baseSpeed;

    /**
     * For Serialization only
     */
    public CommonStatistics() {}

    public CommonStatistics(int ID) {
        this(ID, 300, 100, 10, 10, 10, 10, 10);
    }

    public CommonStatistics(int ID, int baseHP, int baseMP, int basePStr, int basePDef, int baseMStr,
                            int baseMDef, int baseSpeed) {
        this.ID = ID;
        this.baseHP = baseHP;
        this.baseMP = baseMP;
        this.basePStr = basePStr;
        this.basePDef = basePDef;
        this.baseMStr = baseMStr;
        this.baseMDef = baseMDef;
        this.baseSpeed = baseSpeed;
    }

    public int getID() {
        return ID;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseMP() {
        return baseMP;
    }

    public int getBasePStr() {
        return basePStr;
    }

    public int getBasePDef() {
        return basePDef;
    }

    public int getBaseMStr() {
        return baseMStr;
    }

    public int getBaseMDef() {
        return baseMDef;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }
}
