package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.items.IItemService;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
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

    public int HP, MP, PStr, PDef, MStr, MDef, Speed;
    public int HPmax, MPmax, PStrMax, PDefMax, MStrMax, MDefMax, SpeedMax;
    public int HPindi, MPindi, PStrIndi, PDefIndi, MStrIndi, MDefIndi, SpeedIndi;
    public int HPgrowth, MPgrowth, PStrGrowth, PDefGrowth, MStrGrowth, MDefGrowth, SpeedGrowth;

    public String hand, body, head, foot;

    @ForSerializationOnly
    public SerializableStat() {}

    public SerializableStat(IndividualStatistics statistics) {
        this.character = statistics.getCharacter();
        this.level = statistics.getLevel();
        this.abilityLevels = statistics.getAbilityLevels();
        this.exp = statistics.getExp();
        this.HP = statistics.getHp();
        this.MP = statistics.getMp();
        this.PStr = statistics.getPStr();
        this.PDef = statistics.getPDef();
        this.MStr = statistics.getMStr();
        this.MDef = statistics.getMDef();
        this.Speed = statistics.getSpeed();
        this.HPmax = statistics.getHpMax();
        this.MPmax = statistics.getMPmax();
        this.PStrMax = statistics.getPStrMax();
        this.PDefMax = statistics.getPDefMax();
        this.MStrMax = statistics.getMStrMax();
        this.MDefMax = statistics.getMDefMax();
        this.SpeedMax = statistics.getSpeedMax();
        Statistics indiStats = statistics.getIndiBaseValues();
        this.HPindi = indiStats.getHP();
        this.MPindi = indiStats.getMP();
        this.PStrIndi = indiStats.getPStr();
        this.PDefIndi = indiStats.getPDef();
        this.MStrIndi = indiStats.getMStr();
        this.MDefIndi = indiStats.getMDef();
        this.SpeedIndi = indiStats.getSpeed();
        Statistics growthStats = statistics.getGrowthBaseValues();
        this.HPgrowth = growthStats.getHP();
        this.MPgrowth = growthStats.getMP();
        this.PStrGrowth = growthStats.getPStr();
        this.PDefGrowth = growthStats.getPDef();
        this.MStrGrowth = growthStats.getMStr();
        this.MDefGrowth = growthStats.getMDef();
        this.SpeedGrowth = growthStats.getSpeed();
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

        Statistics indiStats = new Statistics(
            sStat.HPindi,
            sStat.MPindi,
            sStat.PStrIndi,
            sStat.PDefIndi,
            sStat.MStrIndi,
            sStat.MDefIndi,
            sStat.SpeedIndi
        );

        Statistics growthStats = new Statistics(
            sStat.HPgrowth,
            sStat.MPgrowth,
            sStat.PStrGrowth,
            sStat.PDefGrowth,
            sStat.MStrGrowth,
            sStat.MDefGrowth,
            sStat.SpeedGrowth
        );

        // TODO

        IItemService items = GuardiansServiceLocator.INSTANCE.getItems();

        return new IndividualStatistics(
            null,
            sStat.level,
            sStat.abilityLevels,
            sStat.exp,
            sStat.character,
            stats,
            maxStats,
            indiStats,
            growthStats,
            (Equipment) items.getItem(sStat.hand),
            (Equipment) items.getItem(sStat.head),
            (Equipment) items.getItem(sStat.body),
            (Equipment) items.getItem(sStat.foot)
        );
    }
}
