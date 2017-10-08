package de.limbusdev.guardianmonsters.guardians.monsters;

/**
 * Statistics
 *
 * The Status Values (Stats) are:
 *
 * HP   ..  Health Points
 * MP   ..  Magic Points
 * PStr ..  Physical Strength
 * PDef ..  Physical Defense
 * MStr ..  Magical Strength
 * MDef ..  Magical Defense
 * Speed
 *
 * @author Georg Eckert 2017
 */
public class Statistics
{
    protected int HP, MP, PStr, PDef, MStr, MDef, Speed;

    /**
     * For Serialization only
     */
    public Statistics() {}

    public Statistics(int HP, int MP, int PStr, int PDef, int MStr, int MDef, int speed)
    {
        this.HP = HP;
        this.MP = MP;
        this.PStr = PStr;
        this.PDef = PDef;
        this.MStr = MStr;
        this.MDef = MDef;
        Speed = speed;
    }

    public int getHP()
    {
        return HP;
    }

    public int getMP()
    {
        return MP;
    }

    public int getPStr()
    {
        return PStr;
    }

    public int getPDef()
    {
        return PDef;
    }

    public int getMStr()
    {
        return MStr;
    }

    public int getMDef()
    {
        return MDef;
    }

    public int getSpeed()
    {
        return Speed;
    }

    @Override
    public String toString()
    {
        return "HP: " + HP + "\tMP: " + MP + "\tPStr: " + PStr
            + "\tPDef: " + PDef + "\tMStr: " + MStr + "\tMDef: " + MDef
            + "\tSpeed: " + Speed;
    }

    @Override
    public Statistics clone()
    {
        return new Statistics(HP, MP, PStr, PDef, MStr, MDef, Speed);
    }
}
