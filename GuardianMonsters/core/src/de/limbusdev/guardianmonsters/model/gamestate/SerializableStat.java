package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.ItemDB;
import de.limbusdev.guardianmonsters.guardians.items.Equipment;
import de.limbusdev.guardianmonsters.guardians.monsters.BaseStat;
import de.limbusdev.guardianmonsters.guardians.monsters.Stat;

/**
 * SerializableStat
 *
 * @author Georg Eckert 2017
 */

public class SerializableStat {

    public int character;
    public int level;
    public int abilityLevels;
    public int exp;
    public BaseStat base;

    public int HP, MP, PStr, PDef, MStr, MDef, Speed;
    public int HPmax, MPmax, PStrMax, PDefMax, MStrMax, MDefMax, SpeedMax;

    public String hand, body, head, foot;

    @ForSerializationOnly
    public SerializableStat() {}

    public SerializableStat(Stat stat) {
        this.character = stat.character;
        this.level = stat.getLevel();
        this.abilityLevels = stat.getAbilityLevels();
        this.exp = stat.getEXP();
        this.base = stat.base;
        this.HP = stat.getHP();
        this.MP = stat.getMP();
        this.PStr = stat.getPStr();
        this.PDef = stat.getPDef();
        this.MStr = stat.getMStr();
        this.MDef = stat.getMDef();
        this.Speed = stat.getSpeed();
        this.HPmax = stat.getHPmax();
        this.MPmax = stat.getMPmax();
        this.PStrMax = stat.getPStrMax();
        this.PDefMax = stat.getPDefMax();
        this.MStrMax = stat.getMStrMax();
        this.MDefMax = stat.getMDefMax();
        this.SpeedMax = stat.getSpeedMax();
        if(stat.hasHandsEquipped()) {
            this.hand = stat.getHands().getName();
        }
        if(stat.hasBodyEquipped()) {
            this.body = stat.getBody().getName();
        }
        if(stat.hasFeetEquipped()) {
            this.foot = stat.getFeet().getName();
        }
        if(stat.hasHeadEquipped()) {
            this.head = stat.getHead().getName();
        }
    }

    public static Stat deserialize(SerializableStat sStat) {
        return new Stat(
            sStat.level,
            sStat.abilityLevels,
            sStat.exp,
            sStat.character,
            sStat.base,
            sStat.HP,
            sStat.MP,
            sStat.PStr,
            sStat.PDef,
            sStat.MStr,
            sStat.MDef,
            sStat.Speed,
            sStat.HPmax,
            sStat.MPmax,
            sStat.PStrMax,
            sStat.PDefMax,
            sStat.MStrMax,
            sStat.MDefMax,
            sStat.SpeedMax,
            (Equipment)ItemDB.getItem(sStat.hand),
            (Equipment)ItemDB.getItem(sStat.head),
            (Equipment)ItemDB.getItem(sStat.body),
            (Equipment)ItemDB.getItem(sStat.foot)
        );
    }
}
