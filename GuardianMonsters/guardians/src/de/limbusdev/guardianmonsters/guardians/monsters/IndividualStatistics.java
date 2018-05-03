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
 * when the guardian levels up (0..2). They range from 0-15 for every Stat, except HP and MP,
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

    public enum StatusEffect
    {
        HEALTHY, SLEEPING, PETRIFIED, BLIND, LUNATIC, POISONED,
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

    private Statistics currentStatValues;   // current values of the various Stats (HP, MP, PStr, ...)
    private Statistics fullStatValues;      // fully healed values of the various Stats, recalculate at level-up
    private Statistics indiBaseValues;      // individual base values of the various Stats, decided at birth
    private Statistics growthBaseValues;    // accumulated values earned during level-up

    private StatusEffect statusEffect;

    private int level;
    private int abilityLevels;
    private int EXP;
    private int remainingLevelUps;          // How many levels have been reached without level-up

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
        Statistics fullStatValues,
        Statistics indiBaseValues,
        Statistics growthBaseValues,
        Equipment hands,
        Equipment head,
        Equipment body,
        Equipment feet)
    {
        this.core = core;
        this.level = level;
        this.remainingLevelUps = 0;
        this.abilityLevels = abilityLevels;
        this.EXP = EXP;
        this.character = character;
        this.currentStatValues = stats;
        this.fullStatValues = fullStatValues;
        this.indiBaseValues = indiBaseValues;
        this.growthBaseValues = growthBaseValues;

        this.hands = hands;
        this.head = head;
        this.body = body;
        this.feet = feet;
        Statistics nullStats = new Statistics(0,0,0,0,0,0,0);
        lvlUpReport = new LevelUpReport(nullStats, nullStats, 0, 0);
    }

    protected IndividualStatistics(AGuardian core, CommonStatistics commonStatistics, int level, int character)
    {
        construct(core, commonStatistics, level, character);
    }

    /**
     * Creates an IndividualStatistics object, containing all information that is unique to the
     * submitted guardian.
     * @param core              the guardian, this object will belong to
     * @param commonStatistics  the common stats, which are equal for all guardians of this species
     * @param level             the level, the created guardian will have
     */
    protected IndividualStatistics(AGuardian core, CommonStatistics commonStatistics, int level)
    {
        int character;
        // Choose a random character, to create really individual guardians
        switch(MathUtils.random(0,2))
        {
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
        this.remainingLevelUps = level;
        this.statusEffect = StatusEffect.HEALTHY;

        // ......................................................................................... base values
        this.growthBaseValues = new Statistics(0,0,0,0,0,0,0);

        // Individual Base Values are determined once, and shall be never changed again
        this.indiBaseValues = new Statistics(
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
            commonStatistics = new CommonStatistics(300,50,10,11,12,13,14);
            indiBaseValues = new CommonStatistics(0,0,0,0,0,0,0);
            this.character = Character.BALANCED;
        }


        // ......................................................................................... stats
        // Calculate the visible Stat values from the base values

        this.fullStatValues     = StatCalculator.calculateAllStats(
            characterGrowthRates[character],
            level,
            core.getCommonStatistics(),
            indiBaseValues,
            growthBaseValues
        );

        this.currentStatValues  = fullStatValues.clone();


        // ......................................................................................... leveling
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
    public LevelUpReport levelUp()
    {
        // Raise growth base values randomly
        growthBaseValues = new Statistics(
            growthBaseValues.getHP() + MathTool.dice(2,3,0),
            growthBaseValues.getMP() + MathTool.dice(2,2,0),
            growthBaseValues.getPStr() + MathTool.dice(1,2,0),
            growthBaseValues.getPDef() + MathTool.dice(1,2,0),
            growthBaseValues.getMStr() + MathTool.dice(1,2,0),
            growthBaseValues.getMDef() + MathTool.dice(1,2,0),
            growthBaseValues.getSpeed() + MathTool.dice(1,2,0)
        );

        // Debugging
        if(Constant.DEBUGGING_ON) {
            growthBaseValues = new CommonStatistics(0,0,0,0,0,0,0);
        }


        CommonStatistics commonBaseValues = core.getCommonStatistics();
        int newLevel = this.level + 1;

        // Calculate the Stats on the new level
        Statistics newFullStatValues = StatCalculator.calculateAllStats(
            characterGrowthRates[character],
            newLevel,
            commonBaseValues,
            indiBaseValues,
            growthBaseValues
        );

        LevelUpReport report = new LevelUpReport(
            fullStatValues.clone(),
            newFullStatValues,
            this.level,
            newLevel
        );

        this.level      = report.newLevel;
        this.fullStatValues = report.newStats;

        this.abilityLevels += 1;

        this.lvlUpReport = report;
        this.remainingLevelUps--;

        return report;
    }

    public void earnEXP(int EXP)
    {
        // TODO add growth values for every victory, 0 when debugging
        float extFactor = 100f;
        int potentiallyReachableLevel = this.level;

        if(hands != null)   extFactor += hands.addsEXP;
        if(body != null)    extFactor += body.addsEXP;
        if(feet != null)    extFactor += feet.addsEXP;
        if(head != null)    extFactor += head.addsEXP;

        int extEXP = MathUtils.round(EXP * extFactor / 100f);

        this.EXP += extEXP;

        while(this.EXP >= StatCalculator.calcEXPtoReachLevel(potentiallyReachableLevel))
        {
            this.EXP -= StatCalculator.calcEXPtoReachLevel(potentiallyReachableLevel);
            potentiallyReachableLevel++;
            remainingLevelUps++;
        }

        core.setStatisticsChanged();
        core.notifyObservers();
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
        setStatusEffect(StatusEffect.HEALTHY);
    }

    public void healHP(int value) {
        setHP(currentStatValues.HP + value);
    }

    public void decreaseHP(int value) {
        setHP(currentStatValues.HP - value);
    }

    public void healMP(int value) {
        setMP(currentStatValues.MP + value);
    }

    public void decreaseMP(int value) {
        setMP(currentStatValues.MP - value);
    }

    /**
     * Increases Physical Strength, by the given fraction (%)
     * @param fraction
     */
    public void modifyPStr(int fraction)
    {
        setPStr(MathUtils.round(currentStatValues.PStr * (100 + fraction)/(100f)));
    }

    /**
     * Increases Physical Defense, by the given fraction (%)
     * @param fraction
     */
    public void modifyPDef(int fraction)
    {
        setPDef(MathUtils.round(currentStatValues.PDef * (100 + fraction)/(100f)));
    }

    /**
     * Increases Magical Strength, by the given fraction (%)
     * @param fraction
     */
    public void modifyMStr(int fraction)
    {
        setMStr(MathUtils.round(currentStatValues.MStr * (100 + fraction)/(100f)));
    }

    /**
     * Increases Magical Defense, by the given fraction (%)
     * @param fraction
     */
    public void modifyMDef(int fraction)
    {
        setMDef(MathUtils.round(currentStatValues.MDef * (100 + fraction)/(100f)));
    }

    /**
     * Increases Speed, by the given fraction (%)
     * @param fraction
     */
    public void modifySpeed(int fraction)
    {
        setSpeed(MathUtils.round(currentStatValues.Speed * (100 + fraction)/(100f)));
    }

    // ............................................................................................. CALCULATED VALUES

    public String getHPfractionAsString()
    {
        return (Integer.toString(currentStatValues.HP) + "/" + Integer.toString(getHPmax()));
    }

    public String getMPfractionAsString()
    {
        return (Integer.toString(currentStatValues.MP) + "/" + Integer.toString(getMPmax()));
    }

    public int getEXPfraction()
    {
        return MathUtils.round((EXP*1f) / StatCalculator.calcEXPtoReachLevel(level+1) * 100f);
    }

    /**
     * Returns HP fraction in %
     * @return
     */
    public int getHPfraction()
    {
        return MathUtils.round(100f* currentStatValues.HP/getHPmax());
    }

    public int getMPfraction()
    {
        return MathUtils.round(100f* currentStatValues.MP/getMPmax());
    }



    /**
     * Calculates how much EXP are still needed to reach the next level
     * @return
     */
    public int getEXPtoNextLevel()
    {
        return (StatCalculator.calcEXPtoReachLevel(level) - EXP);
    }

    public int getRemainingLevelUps()
    {
        return remainingLevelUps;
    }

    public boolean didLevelUp()
    {
        return (getRemainingLevelUps() > 0);
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
        return currentStatValues.HP > 0;
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
        return currentStatValues.HP;
    }

    public int getMP() {
        return currentStatValues.MP;
    }

    public int getPStr() {
        return currentStatValues.PStr;
    }

    public int getPDef() {
        return currentStatValues.PDef;
    }

    public int getMStr() {
        return currentStatValues.MStr;
    }

    public int getMDef() {
        return currentStatValues.MDef;
    }

    public int getSpeed() {
        return currentStatValues.Speed;
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
            indiBaseValues.getHP()+50,
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
            indiBaseValues.getMP()+50,
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
            indiBaseValues.getPStr()+50,
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
            indiBaseValues.getPDef()+50,
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
            indiBaseValues.getMStr()+50,
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
            indiBaseValues.getMDef()+50,
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
            indiBaseValues.getSpeed()+50,
            255
        );
        return maxPossSpeed;
    }

    /**
     * @return maximum HP, taking {@link Equipment} into account
     */
    public int getHPmax()
    {
        float extFactor = 0;

        if(hands != null)   extFactor += hands.addsHP;
        if(body != null)    extFactor += body.addsHP;
        if(feet != null)    extFactor += feet.addsHP;
        if(head != null)    extFactor += head.addsHP;

        extFactor /= 100f;

        return fullStatValues.HP + MathUtils.floor(fullStatValues.HP * extFactor);
    }

    /**
     * @return maximum MP, taking {@link Equipment} into account
     */
    public int getMPmax()
    {
        float extFactor = 0;

        if(hands != null)   extFactor += hands.addsHP;
        if(body != null)    extFactor += body.addsHP;
        if(feet != null)    extFactor += feet.addsHP;
        if(head != null)    extFactor += head.addsHP;

        extFactor /= 100f;

        return fullStatValues.MP + MathUtils.floor(fullStatValues.MP * extFactor);
    }

    /**
     * @return maximum Pstr, taking {@link Equipment} into account
     */
    public int getPStrMax()
    {
        int extPStr = fullStatValues.PStr;

        if(hands != null)   extPStr += hands.addsPStr;
        if(body != null)    extPStr += body.addsPStr;
        if(feet != null)    extPStr += feet.addsPStr;
        if(head != null)    extPStr += head.addsPStr;

        return extPStr;
    }

    /**
     * @return maximum PDef, taking {@link Equipment} into account
     */
    public int getPDefMax()
    {
        int extPDef = fullStatValues.PDef;

        if(hands != null)   extPDef += hands.addsPDef;
        if(body != null)    extPDef += body.addsPDef;
        if(feet != null)    extPDef += feet.addsPDef;
        if(head != null)    extPDef += head.addsPDef;

        return extPDef;
    }

    /**
     * @return maximum MStr, taking {@link Equipment} into account
     */
    public int getMStrMax()
    {
        int extMStr = fullStatValues.MStr;

        if(hands != null)   extMStr += hands.addsMStr;
        if(body != null)    extMStr += body.addsMStr;
        if(feet != null)    extMStr += feet.addsMStr;
        if(head != null)    extMStr += head.addsMStr;

        return extMStr;
    }

    /**
     * @return maximum MDef, taking {@link Equipment} into account
     */
    public int getMDefMax()
    {
        int extMDef = fullStatValues.MDef;

        if(hands != null)   extMDef += hands.addsMDef;
        if(body != null)    extMDef += body.addsMDef;
        if(feet != null)    extMDef += feet.addsMDef;
        if(head != null)    extMDef += head.addsMDef;

        return extMDef;
    }

    /**
     * @return maximum Speed, taking {@link Equipment} into account
     */
    public int getSpeedMax()
    {
        int extSpeed = fullStatValues.Speed;

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

    public StatusEffect getStatusEffect()
    {
        return statusEffect;
    }

    public void setStatusEffect(StatusEffect statusEffect)
    {
        this.statusEffect = statusEffect;
        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setHP(int HP)
    {
        currentStatValues.HP = HP;
        if(HP > getHPmax()) {
            currentStatValues.HP = getHPmax();
        }
        if(HP < 0)          {
            currentStatValues.HP = 0;
            setStatusEffect(StatusEffect.HEALTHY);
        }

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setMP(int MP) {
        currentStatValues.MP = MP;

        if(MP > getMPmax()) currentStatValues.MP = getMPmax();
        if(MP < 0)          currentStatValues.MP = 0;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setPStr(int PStr) {
        currentStatValues.PStr = PStr;

        if(PStr > getPStrMax()*1.5f) currentStatValues.PStr = MathUtils.floor(getPStrMax()*1.5f);
        if(PStr < 1)            currentStatValues.PStr = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setPDef(int PDef) {
        currentStatValues.PDef = PDef;

        if(PDef > getPDefMax()*1.5f) MathUtils.floor(getPDefMax()*1.5f);
        if(PDef < 1)            currentStatValues.PDef = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setMStr(int MStr) {
        currentStatValues.MStr = MStr;

        if(MStr > getMStrMax()*1.5f) currentStatValues.MStr = MathUtils.floor(getMStrMax()*1.5f);
        if(MStr < 1)            currentStatValues.MStr = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setMDef(int MDef) {
        currentStatValues.MDef = MDef;

        if(MDef > getMDefMax()*1.5f) currentStatValues.MDef = MathUtils.floor(getMDefMax()*1.5f);
        if(MDef < 1)            currentStatValues.MDef = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    public void setSpeed(int Speed) {

        currentStatValues.Speed = Speed;

        if(Speed > getSpeedMax()) currentStatValues.Speed = MathUtils.floor(getSpeedMax()*1.5f);
        if(Speed < 1)          currentStatValues.Speed = 1;

        core.setStatisticsChanged();
        core.notifyObservers();
    }

    /**
     * Resets boostable Stats: PStr, PDef, MStr, MDef, Speed
     */
    public void resetModifiedStats()
    {
        setPStr(getPStrMax());
        setPDef(getPDefMax());
        setMStr(getMStrMax());
        setMDef(getMDefMax());
        setSpeed(getSpeedMax());
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

    public Statistics getIndiBaseValues()
    {
        return indiBaseValues;
    }

    public Statistics getGrowthBaseValues()
    {
        return growthBaseValues;
    }

    // ............................................................................................. DELEGATIONS


    @Override
    public String toString()
    {
        String stats = String.format("| HP: %d / %d\tMP: %d / %d\n| PStr: %d\tPDef: %d\tMStr: %d\tMDef: %d\tSpeed: %d",
            currentStatValues.getHP(), fullStatValues.getHP(),
            currentStatValues.getMP(), fullStatValues.getMP(),
            fullStatValues.getPStr(),
            fullStatValues.getPDef(),
            fullStatValues.getMStr(),
            fullStatValues.getMDef(),
            fullStatValues.getSpeed()
        );
        return stats;
    }
}
