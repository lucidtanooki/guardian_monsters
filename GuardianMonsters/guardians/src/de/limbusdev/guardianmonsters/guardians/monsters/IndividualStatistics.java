package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.StatCalculator;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.EquipmentPotential;
import de.limbusdev.utils.MathTool;


/**
 * Guardians Individual Statistic Component
 *
 * IndividualStatistics contains all statistic values of a {@link AGuardian}. The statistic values at
 * level 1 should be copied over from {@link CommonStatistics}
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
 * Individual Base Stats (IndBaseStats) are different in every Guardian. They change randomly,
 * when the guardian levels up (0..2). The Range from 0-15 for every Stat, except HP and MP,
 * there it's 0-63.
 *
 * Growth Stats determine how a Guardian ist developing. They range from 1..3 in every Battle.
 * And every Stat can get a maximum of 0..255.
 *
 * Development Mechanics based on:
 * http://howtomakeanrpg.com/a/how-to-make-an-rpg-levels.html
 * @author Georg Eckert 2016
 */

public class IndividualStatistics
{
    public interface Character
    {
        int BALANCED=0, VIVACIOUS=1, PRUDENT=2;
    }

    /**
     * The triple stands for rolling a dive parameters (Rolls, Sides, Base-Value)
     * 1D6 means one roll of a 6-sided dice
     *
     * Use Common for PStr, PDef, MStr, MDef, Speed
     *
     * Stat     SLOW        MED         FAST
     * Common   1D2         1D3         3D2
     * HP       4D30+100    4D40+100    4D50+100
     * MP       2D20+30     3D30+40     4D30+50
     */
    public interface Growth
    {
        float SLOW =    0.9f,        MED =   1.0f,    FAST =  1.1f;
    }

    public interface StatType
    {
        int PSTR=0, PDEF=1, MSTR=2, MDEF=3, SPEED=4, HP=5, MP=6;
    }

    public static final float[][] characterGrowthRates = {
        /* PStr         PDef            MStr            MDef            Speed           HP              MP          */
        {Growth.MED,    Growth.MED,     Growth.MED,     Growth.MED,     Growth.MED,     Growth.MED,   Growth.MED},   // BALANCED
        {Growth.FAST,   Growth.SLOW,    Growth.MED,     Growth.SLOW,    Growth.FAST,    Growth.SLOW,  Growth.MED},   // VIVACIOUS
        {Growth.MED,    Growth.FAST,    Growth.SLOW,    Growth.FAST,    Growth.SLOW,    Growth.FAST,  Growth.FAST}   // PRUDENT
    };

    private AGuardian core;  // Core Object

    private Statistics currentStats, maxStats, indiBaseStats, growthStats;

    private int level;
    private int abilityLevels;
    private int EXP;

    public int character;   // for growth rates

    private Equipment hands;
    private Equipment head;
    private Equipment body;
    private Equipment feet;

    private LevelUpReport lvlUpReport;

    // ............................................................................................. CONSTRUCTOR

    /**
     * For Serialization only!
     */
    public IndividualStatistics(
        AGuardian core,
        int level,
        int abilityLevels,
        int EXP,
        int character,
        Statistics stats,
        Statistics maxStats,
        Statistics indiBaseStats,
        Statistics growthStats,
        Equipment hands,
        Equipment head,
        Equipment body,
        Equipment feet)
    {
        this.core = core;
        this.level = level;
        this.abilityLevels = abilityLevels;
        this.EXP = EXP;
        this.character = character;
        this.currentStats = stats;
        this.maxStats = maxStats;
        this.indiBaseStats = indiBaseStats;
        this.growthStats = growthStats;

        this.hands = hands;
        this.head = head;
        this.body = body;
        this.feet = feet;
        Statistics nullStats = new Statistics(0,0,0,0,0,0,0);
        lvlUpReport = new LevelUpReport(nullStats, nullStats, 0, 0);
    }

    protected IndividualStatistics(AGuardian core, CommonStatistics commonStatistics, int level, int character) {
        construct(core, commonStatistics, level, character);
    }

    protected IndividualStatistics(AGuardian core, CommonStatistics commonStatistics, int level)
    {
        int character;
        // Choose a random character
        switch(MathUtils.random(0,2)) {
            case 2:  character = Character.VIVACIOUS; break;
            case 1:  character = Character.PRUDENT; break;
            default: character = Character.BALANCED; break;
        }
        construct(core, commonStatistics, level, character);
    }

    private void construct(AGuardian core, CommonStatistics commonStatistics, int level, int character)
    {
        this.core = core;
        this.character = character;
        this.level = 0;
        this.abilityLevels = -1;

        this.growthStats = new Statistics(0,0,0,0,0,0,0);
        this.indiBaseStats = new Statistics(
            MathUtils.random(0,63),
            MathUtils.random(0,63),
            MathUtils.random(0,15),
            MathUtils.random(0,15),
            MathUtils.random(0,15),
            MathUtils.random(0,15),
            MathUtils.random(0,15)
        );

        // Debugging
        if(Constant.DEBUGGING_ON) {
            commonStatistics = new CommonStatistics(50,50,50,50,50,50,50);
            indiBaseStats = new CommonStatistics(0,0,0,0,0,0,0);
            this.character = Character.BALANCED;
        }

        this.currentStats = commonStatistics.clone();
        this.maxStats = commonStatistics.clone();

        for(int i=0; i<level; i++)
        {
            levelUp();
        }

        healCompletely();

        // set EXP according to level
        this.EXP = StatCalculator.calcEXPtoReachLevel(level);

        Statistics nullStats = new Statistics(0,0,0,0,0,0,0);
        lvlUpReport = new LevelUpReport(nullStats, nullStats, 0, 0);
    }

    // ............................................................................................. METHODS

    /**
     * Formula:
     *  indiStats: raised at every LevelUp, randomly (HP, MP: 0..2, other: 0..1)
     *  newMP = character * newLevel + 6 +  floor( (newLevel / 100) * (2*baseMP + indBaseMP + floor(growthMP/4))
     *  other = floor(character * (5 + floor((newLevel/100) * (2*base+indi+floor(growth/4)))))
     * @return
     */
    private LevelUpReport levelUp()
    {
        // Raise individual Base Stats randomly
        indiBaseStats = new Statistics(
            indiBaseStats.getHP() + MathTool.dice(2,3,0),
            indiBaseStats.getMP() + MathTool.dice(2,2,0),
            indiBaseStats.getPStr() + MathTool.dice(1,2,0),
            indiBaseStats.getPDef() + MathTool.dice(1,2,0),
            indiBaseStats.getMStr() + MathTool.dice(1,2,0),
            indiBaseStats.getMDef() + MathTool.dice(1,2,0),
            indiBaseStats.getSpeed() + MathTool.dice(1,2,0)
        );

        // Debugging
        if(Constant.DEBUGGING_ON) {
            indiBaseStats = new CommonStatistics(0,0,0,0,0,0,0);
        }


        CommonStatistics common = core.getCommonStatistics();
        int newLevel = this.level + 1;

        int newHP = StatCalculator.calculateHP(
            characterGrowthRates[character][StatType.HP],
            newLevel,
            common.getBaseHP(),
            indiBaseStats.getHP(),
            growthStats.getHP()
        );

        int newMP = StatCalculator.calculateMP(
            characterGrowthRates[character][StatType.MP],
            newLevel,
            common.getBaseMP(),
            indiBaseStats.getMP(),
            growthStats.getMP()
        );

        int newPStr = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.PSTR],
            newLevel,
            common.getBasePStr(),
            indiBaseStats.getPStr(),
            growthStats.getPStr()
        );

        int newPDef = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.PDEF],
            newLevel,
            common.getBasePDef(),
            indiBaseStats.getPDef(),
            growthStats.getPDef()
        );

        int newMStr = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.MSTR],
            newLevel,
            common.getBaseMStr(),
            indiBaseStats.getMStr(),
            growthStats.getMStr()
        );

        int newMDef = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.MDEF],
            newLevel,
            common.getBaseMDef(),
            indiBaseStats.getMDef(),
            growthStats.getMDef()
        );

        int newSpeed = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.SPEED],
            newLevel,
            common.getBaseSpeed(),
            indiBaseStats.getSpeed(),
            growthStats.getSpeed()
        );

        LevelUpReport report = new LevelUpReport(
            maxStats.clone(),
            new Statistics(newHP, newMP, newPStr, newPDef, newMStr, newMDef, newSpeed),
            this.level,
            this.level + 1
        );

        this.level      = report.newLevel;
        this.maxStats   = report.newStats;

        this.abilityLevels += 1;

        this.lvlUpReport = report;

        core.setStatisticsChanged();
        core.notifyObservers();

        return report;
    }

    public boolean earnEXP(int EXP)
    {
        // TODO add growth values for every victory, 0 when debugging
        boolean leveledUp = false;
        float extFactor = 100f;

        if(hands != null)   extFactor += hands.addsEXP;
        if(body != null)    extFactor += body.addsEXP;
        if(feet != null)    extFactor += feet.addsEXP;
        if(head != null)    extFactor += head.addsEXP;

        int extEXP = MathUtils.round(EXP * extFactor / 100f);

        this.EXP += extEXP;

        if(this.EXP >= StatCalculator.calcEXPtoReachLevel(level)) {
            this.EXP -= StatCalculator.calcEXPtoReachLevel(level);
            levelUp();
            leveledUp = true;
        }

        core.setStatisticsChanged();
        core.notifyObservers();

        return leveledUp;
    }

    /**
     * Resets all status values to the maximum
     */
    public void healCompletely()
    {
        setHP(getHPmax());
        setMP(getMPmax());
        setPStr(getPStrMax());
        setPDef(getPDefMax());
        setMStr(getMStrMax());
        setMDef(getMDefMax());
        setSpeed(getSpeedMax());
    }

    public void healHP(int value) {
        setHP(currentStats.HP + value);
    }

    public void decreaseHP(int value) {
        setHP(currentStats.HP - value);
    }

    public void healMP(int value) {
        setMP(currentStats.MP + value);
    }

    public void decreaseMP(int value) {
        setMP(currentStats.MP - value);
    }

    /**
     * Increases Physical Strength, by the given fraction (%)
     * @param fraction
     */
    public void increasePStr(int fraction) {
        setPStr(MathUtils.round(currentStats.PStr * (100 + fraction)/(100f)));
    }

    /**
     * Increases Physical Defense, by the given fraction (%)
     * @param fraction
     */
    public void increasePDef(int fraction) {
        setPDef(MathUtils.round(currentStats.PDef * (100 + fraction)/(100f)));
    }

    /**
     * Increases Magical Strength, by the given fraction (%)
     * @param fraction
     */
    public void increaseMStr(int fraction) {
        setMStr(MathUtils.round(currentStats.MStr * (100 + fraction)/(100f)));
    }

    /**
     * Increases Magical Defense, by the given fraction (%)
     * @param fraction
     */
    public void increaseMDef(int fraction) {
        setMDef(MathUtils.round(currentStats.MDef * (100 + fraction)/(100f)));
    }

    /**
     * Increases Speed, by the given fraction (%)
     * @param fraction
     */
    public void increaseSpeed(int fraction) {
        setSpeed(MathUtils.round(currentStats.Speed * (100 + fraction)/(100f)));
    }

    // ............................................................................................. CALCULATED VALUES

    public String getHPfractionAsString() {
        return (Integer.toString(currentStats.HP) + "/" + Integer.toString(getHPmax()));
    }

    public String getMPfractionAsString() {
        return (Integer.toString(currentStats.MP) + "/" + Integer.toString(getMPmax()));
    }

    public int getEXPfraction() {
        return MathUtils.round(EXP/1.f/StatCalculator.calcEXPtoReachLevel(level)*100f);
    }

    public int getHPfraction() {
        return MathUtils.round(100f*currentStats.HP/getHPmax());
    }

    public int getMPfraction() {
        return MathUtils.round(100f*currentStats.MP/getMPmax());
    }



    /**
     * Calculates how much EXP are still needed to reach the next level
     * @return
     */
    public int getEXPtoNextLevel() {
        return (StatCalculator.calcEXPtoReachLevel(level) - EXP);
    }

    public Equipment giveEquipment(Equipment equipment)
    {
        Equipment oldEquipment=null;
        switch(equipment.bodyPart)
        {
            case HANDS:
                oldEquipment = hands;
                hands = equipment;
                break;
            case BODY:
                oldEquipment = body;
                body = equipment;
                break;
            case FEET:
                oldEquipment = feet;
                feet = equipment;
                break;
            case HEAD:
                oldEquipment = head;
                head = equipment;
                break;
        }

        core.setStatisticsChanged();
        core.notifyObservers();

        return oldEquipment;
    }

    /**
     * Calculates the {@link EquipmentPotential} of a given {@link Equipment} for this {@link Guardian}
     * @param eq
     * @return
     */
    public EquipmentPotential getEquipmentPotential(Equipment eq)
    {
        EquipmentPotential pot;

        Equipment currentEquipment;
        switch(eq.bodyPart) {
            case HEAD:
                currentEquipment = head;
                break;
            case BODY:
                currentEquipment = body;
                break;
            case FEET:
                currentEquipment = feet;
                break;
            default:
                currentEquipment = hands;
                break;
        }

        if(currentEquipment == null) {
            pot = new EquipmentPotential(
                eq.addsHP,
                eq.addsMP,
                eq.addsSpeed,
                eq.addsEXP,
                eq.addsPStr,
                eq.addsPDef,
                eq.addsMStr,
                eq.addsMDef
            );
        } else {
            pot = new EquipmentPotential(
                eq.addsHP      - currentEquipment.addsHP,
                eq.addsMP      - currentEquipment.addsMP,
                eq.addsSpeed   - currentEquipment.addsSpeed,
                eq.addsEXP     - currentEquipment.addsEXP,
                eq.addsPStr    - currentEquipment.addsPStr,
                eq.addsPDef    - currentEquipment.addsPDef,
                eq.addsMStr    - currentEquipment.addsMStr,
                eq.addsMDef    - currentEquipment.addsMDef
            );
        }

        return pot;
    }

    // ............................................................................................. SETTERS & GETTERS

    public int getLevel() {
        return level;
    }

    public boolean isFit() {
        return currentStats.HP > 0;
    }

    public boolean isKO() {
        return !isFit();
    }

    /**
     * returns the number of levels, which can be used to activate nodes in the {@link AbilityGraph}
     * @return
     */
    public int getAbilityLevels() {
        return abilityLevels;
    }

    public boolean hasAbilityPoints() {
        return getAbilityLevels() > 0;
    }

    public int getEXP() {
        return EXP;
    }

    public int getHP() {
        return currentStats.HP;
    }

    public int getMP() {
        return currentStats.MP;
    }

    public int getPStr() {
        return currentStats.PStr;
    }

    public int getPDef() {
        return currentStats.PDef;
    }

    public int getMStr() {
        return currentStats.MStr;
    }

    public int getMDef() {
        return currentStats.MDef;
    }

    public int getSpeed() {
        return currentStats.Speed;
    }

    /**
     * Returns the highest value a guardian can reach, if it
     * receives the best values at every level up
     * @return
     */
    public int getMaxPossibleHP()
    {
        int maxPossHP = StatCalculator.calculateHP(
            characterGrowthRates[character][StatType.HP],
            99,
            core.getCommonStatistics().getBaseHP(),
            indiBaseStats.getHP()+50,
            255
        );
        return maxPossHP;
    }

    public int getMaxPossibleMP()
    {
        int maxPossMP = StatCalculator.calculateMP(
            characterGrowthRates[character][StatType.MP],
            99,
            core.getCommonStatistics().getBaseMP(),
            indiBaseStats.getMP()+50,
            255
        );
        return maxPossMP;
    }

    public int getMaxPossiblePStr()
    {
        int maxPossPStr = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.PSTR],
            99,
            core.getCommonStatistics().getBasePStr(),
            indiBaseStats.getPStr()+50,
            255
        );
        return maxPossPStr;
    }

    public int getMaxPossiblePDef()
    {
        int maxPossPDef = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.PDEF],
            99,
            core.getCommonStatistics().getBasePDef(),
            indiBaseStats.getPDef()+50,
            255
        );
        return maxPossPDef;
    }

    public int getMaxPossibleMStr()
    {
        int maxPossMStr = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.MSTR],
            99,
            core.getCommonStatistics().getBaseMStr(),
            indiBaseStats.getMStr()+50,
            255
        );
        return maxPossMStr;
    }

    public int getMaxPossibleMDef()
    {
        int maxPossMDef = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.MDEF],
            99,
            core.getCommonStatistics().getBaseMDef(),
            indiBaseStats.getMDef()+50,
            255
        );
        return maxPossMDef;
    }

    public int getMaxPossibleSpeed()
    {
        int maxPossSpeed = StatCalculator.calculateStat(
            characterGrowthRates[character][StatType.SPEED],
            99,
            core.getCommonStatistics().getBaseSpeed(),
            indiBaseStats.getSpeed()+50,
            255
        );
        return maxPossSpeed;
    }

    /**
     * @return maximum HP, taking {@link Equipment} into account
     */
    public int getHPmax() {
        float extFactor = 100f;

        if(hands != null)   extFactor += hands.addsHP;
        if(body != null)    extFactor += body.addsHP;
        if(feet != null)    extFactor += feet.addsHP;
        if(head != null)    extFactor += head.addsHP;

        return MathUtils.floor((maxStats.HP * extFactor) / 100f);
    }

    /**
     * @return maximum MP, taking {@link Equipment} into account
     */
    public int getMPmax() {
        float extFactor = 100f;

        if(hands != null)   extFactor += hands.addsMP;
        if(body != null)    extFactor += body.addsMP;
        if(feet != null)    extFactor += feet.addsMP;
        if(head != null)    extFactor += head.addsMP;

        return MathUtils.floor((maxStats.MP * extFactor) / 100f);
    }

    /**
     * @return maximum Pstr, taking {@link Equipment} into account
     */
    public int getPStrMax() {
        int extPStr = maxStats.PStr;

        if(hands != null)   extPStr += hands.addsPStr;
        if(body != null)    extPStr += body.addsPStr;
        if(feet != null)    extPStr += feet.addsPStr;
        if(head != null)    extPStr += head.addsPStr;

        return extPStr;
    }

    /**
     * @return maximum PDef, taking {@link Equipment} into account
     */
    public int getPDefMax() {
        int extPDef = maxStats.PDef;

        if(hands != null)   extPDef += hands.addsPDef;
        if(body != null)    extPDef += body.addsPDef;
        if(feet != null)    extPDef += feet.addsPDef;
        if(head != null)    extPDef += head.addsPDef;

        return extPDef;
    }

    /**
     * @return maximum MStr, taking {@link Equipment} into account
     */
    public int getMStrMax() {
        int extMStr = maxStats.MStr;

        if(hands != null)   extMStr += hands.addsMStr;
        if(body != null)    extMStr += body.addsMStr;
        if(feet != null)    extMStr += feet.addsMStr;
        if(head != null)    extMStr += head.addsMStr;

        return extMStr;
    }

    /**
     * @return maximum MDef, taking {@link Equipment} into account
     */
    public int getMDefMax() {
        int extMDef = maxStats.MDef;

        if(hands != null)   extMDef += hands.addsMDef;
        if(body != null)    extMDef += body.addsMDef;
        if(feet != null)    extMDef += feet.addsMDef;
        if(head != null)    extMDef += head.addsMDef;

        return extMDef;
    }

    /**
     * @return maximum Speed, taking {@link Equipment} into account
     */
    public int getSpeedMax() {
        int extSpeed = maxStats.Speed;

        if(hands != null)   extSpeed += hands.addsSpeed;
        if(body != null)    extSpeed += body.addsSpeed;
        if(feet != null)    extSpeed += feet.addsSpeed;
        if(head != null)    extSpeed += head.addsSpeed;

        return extSpeed;
    }


    public boolean hasHandsEquipped() {
        return hands != null;
    }
    public boolean hasHeadEquipped() {
        return head != null;
    }
    public boolean hasBodyEquipped() {
        return body != null;
    }
    public boolean hasFeetEquipped() {
        return feet != null;
    }

    public Equipment getHands() {
        return hands;
    }

    public Equipment getHead() {
        return head;
    }

    public Equipment getBody() {
        return body;
    }

    public Equipment getFeet() {
        return feet;
    }

    public void setHP(int HP)
    {
        currentStats.HP = HP;
        if(HP > getHPmax()) currentStats.HP = getHPmax();
        if(HP < 0)          currentStats.HP = 0;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setMP(int MP) {
        currentStats.MP = MP;

        if(MP > getMPmax()) currentStats.MP = getMPmax();
        if(MP < 0)          currentStats.MP = 0;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setPStr(int PStr) {
        currentStats.PStr = PStr;

        if(PStr > getPStrMax()*1.5f) currentStats.PStr = getPStrMax();
        if(PStr < 1)            currentStats.PStr = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setPDef(int PDef) {
        currentStats.PDef = PDef;

        if(PDef > getPDefMax()*1.5f) currentStats.PDef = getPDefMax();
        if(PDef < 1)            currentStats.PDef = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setMStr(int MStr) {
        currentStats.MStr = MStr;

        if(MStr > getMStrMax()*1.5f) currentStats.MStr = getMStrMax();
        if(MStr < 1)            currentStats.MStr = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setMDef(int MDef) {
        currentStats.MDef = MDef;

        if(MDef > getMDefMax()*1.5f) currentStats.MDef = getMDefMax();
        if(MDef < 1)            currentStats.MDef = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setSpeed(int Speed) {
        currentStats.Speed = Speed;

        if(Speed > getSpeedMax()) currentStats.Speed = getSpeedMax();
        if(Speed < 1)          currentStats.Speed = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    /**
     * Reduces the number of available ability levels by 1,
     * usually, when a field in the ability board is activated
     */
    public void consumeAbilityLevel() {
        this.abilityLevels--;
        core.setStatisticsChanged();
        core.notifyObservers();
    }


    public LevelUpReport getLatestLevelUpReport() {
        return lvlUpReport;
    }

    public Statistics getIndiBaseStats()
    {
        return indiBaseStats;
    }

    public Statistics getGrowthStats()
    {
        return growthStats;
    }

    // ............................................................................................. DELEGATIONS


    @Override
    public String toString()
    {
        return maxStats.toString();
    }
}
