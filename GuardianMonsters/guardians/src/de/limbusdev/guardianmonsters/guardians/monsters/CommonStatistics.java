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
    private Statistics baseStats;

    /**
     * For Serialization only
     */
    public CommonStatistics() {}

    public CommonStatistics(int ID) {
        this(ID, new Statistics(300, 50, 10, 10, 10, 10, 10));
    }

    public CommonStatistics(int ID, Statistics baseStats) {
        this.ID = ID;
        this.baseStats =  baseStats;
    }

    public int getID() {
        return ID;
    }

    public int getBaseHP() {
        return baseStats.HP;
    }

    public int getBaseMP() {
        return baseStats.MP;
    }

    public int getBasePStr() {
        return baseStats.PStr;
    }

    public int getBasePDef() {
        return baseStats.PDef;
    }

    public int getBaseMStr() {
        return baseStats.MStr;
    }

    public int getBaseMDef() {
        return baseStats.MDef;
    }

    public int getBaseSpeed() {
        return baseStats.Speed;
    }
}
