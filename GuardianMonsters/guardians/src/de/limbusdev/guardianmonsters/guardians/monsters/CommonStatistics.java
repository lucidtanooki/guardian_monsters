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

public class CommonStatistics extends Statistics
{
    /**
     * For Serialization only
     */
    public CommonStatistics()
    {
        this(500, 100, 50, 50, 50, 50, 10);
    }

    protected CommonStatistics(int HP, int MP, int PStr, int PDef, int MStr, int MDef, int Speed)
    {
        super(HP, MP, PStr, PDef, MStr, MDef, Speed);
    }


    public int getBaseHP() {
        return getHP();
    }

    public int getBaseMP() {
        return getMP();
    }

    public int getBasePStr() {
        return getPStr();
    }

    public int getBasePDef() {
        return getPDef();
    }

    public int getBaseMStr() {
        return getMStr();
    }

    public int getBaseMDef() {
        return getMDef();
    }

    public int getBaseSpeed() {
        return getSpeed();
    }
}
