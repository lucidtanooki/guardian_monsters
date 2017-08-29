package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.ItemDB;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
import de.limbusdev.guardianmonsters.guardians.monsters.CommonStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.Statistics;

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
    public CommonStatistics base;

    public int HP, MP, PStr, PDef, MStr, MDef, Speed;
    public int HPmax, MPmax, PStrMax, PDefMax, MStrMax, MDefMax, SpeedMax;

    public String hand, body, head, foot;

    @ForSerializationOnly
    public SerializableStat() {}

    public SerializableStat(IndividualStatistics statistics) {
        this.character = statistics.character;
        this.level = statistics.getLevel();
        this.abilityLevels = statistics.getAbilityLevels();
        this.exp = statistics.getEXP();
        this.base = statistics.base;
        this.HP = statistics.getHP();
        this.MP = statistics.getMP();
        this.PStr = statistics.getPStr();
        this.PDef = statistics.getPDef();
        this.MStr = statistics.getMStr();
        this.MDef = statistics.getMDef();
        this.Speed = statistics.getSpeed();
        this.HPmax = statistics.getHPmax();
        this.MPmax = statistics.getMPmax();
        this.PStrMax = statistics.getPStrMax();
        this.PDefMax = statistics.getPDefMax();
        this.MStrMax = statistics.getMStrMax();
        this.MDefMax = statistics.getMDefMax();
        this.SpeedMax = statistics.getSpeedMax();
        if(statistics.hasHandsEquipped()) {
            this.hand = statistics.getHands().getName();
        }
        if(statistics.hasBodyEquipped()) {
            this.body = statistics.getBody().getName();
        }
        if(statistics.hasFeetEquipped()) {
            this.foot = statistics.getFeet().getName();
        }
        if(statistics.hasHeadEquipped()) {
            this.head = statistics.getHead().getName();
        }
    }

    public static IndividualStatistics deserialize(SerializableStat sStat)
    {
        Statistics stats = new Statistics(
            sStat.HP,
            sStat.MP,
            sStat.PStr,
            sStat.PDef,
            sStat.MStr,
            sStat.MDef,
            sStat.Speed
        );
        Statistics maxStats = new Statistics(
            sStat.HPmax,
            sStat.MPmax,
            sStat.PStrMax,
            sStat.PDefMax,
            sStat.MStrMax,
            sStat.MDefMax,
            sStat.SpeedMax
        );
        return new IndividualStatistics(
            sStat.level,
            sStat.abilityLevels,
            sStat.exp,
            sStat.character,
            sStat.base,
            stats,
            maxStats,
            (Equipment)ItemDB.getItem(sStat.hand),
            (Equipment)ItemDB.getItem(sStat.head),
            (Equipment)ItemDB.getItem(sStat.body),
            (Equipment)ItemDB.getItem(sStat.foot)
        );
    }
}
