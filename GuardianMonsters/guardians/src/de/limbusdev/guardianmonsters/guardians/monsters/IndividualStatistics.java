package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.EquipmentPotential;
import de.limbusdev.utils.MathTool;


/**
 * Guardians Individual Statistic Component
 *
 * IndividualStatisticscontains all statistic values of a {@link AGuardian}. The statistic values at
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

    public interface Growth
    {
        int[] SLOW =    {1,2,0},        MED =   {1,3,0},    FAST =  {3,2,0};
        int[] SLOWHP =  {4,30,100},     MEDHP = {4,40,100}, FASTHP ={4,50,100};
        int[] SLOWMP =  {2,20,30},      MEDMP = {3,30,40},  FASTMP ={4,30,50};
    }

    public interface StatType
    {
        int PSTR=0, PDEF=1, MSTR=2, MDEF=3, SPEED=4, HP=5, MP=6;
    }

    public static final int[][][] characterGrowthRates = {
        /* PStr         PDef            MStr            MDef            Speed           HP              MP          */
        {Growth.MED,    Growth.MED,     Growth.MED,     Growth.MED,     Growth.MED,     Growth.MEDHP,   Growth.MEDMP},   // BALANCED
        {Growth.FAST,   Growth.SLOW,    Growth.MED,     Growth.SLOW,    Growth.FAST,    Growth.SLOWHP,  Growth.MEDMP},   // VIVACIOUS
        {Growth.MED,    Growth.FAST,    Growth.SLOW,    Growth.FAST,    Growth.SLOW,    Growth.FASTHP,  Growth.FASTMP}   // PRUDENT
    };

    private AGuardian core;  // Core Object

    private Statistics currentStats, maxStats;

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
        this.hands = hands;
        this.head = head;
        this.body = body;
        this.feet = feet;
        Statistics nullStats = new Statistics(0,0,0,0,0,0,0);
        lvlUpReport = new LevelUpReport(nullStats, nullStats, 0, 0);
    }

    protected IndividualStatistics(AGuardian core, int level, int character) {
        construct(core, level, character);
    }

    protected IndividualStatistics(AGuardian core, int level)
    {
        int character;
        // Choose a random character
        switch(MathUtils.random(0,2)) {
            case 2:  character = Character.VIVACIOUS; break;
            case 1:  character = Character.PRUDENT; break;
            default: character = Character.BALANCED; break;
        }
        construct(core, level, character);
    }

    private void construct(AGuardian core, int level, int character)
    {
        this.character = character;
        this.level = level;
        this.abilityLevels = level-1;

        this.maxStats = core.getCommonStatistics().clone();

        for(int i=1; i<level; i++)
        {
            levelUp();
        }

        healCompletely();

        this.EXP = 0;

        Statistics nullStats = new Statistics(0,0,0,0,0,0,0);
        lvlUpReport = new LevelUpReport(nullStats, nullStats, 0, 0);
    }

    // ............................................................................................. METHODS
    private LevelUpReport levelUp() {

        LevelUpReport report = new LevelUpReport(
            maxStats.clone(),
            new Statistics(
                maxStats.getHP()      + MathTool.dice(characterGrowthRates[character][StatType.HP]),
                maxStats.getMP()      + MathTool.dice(characterGrowthRates[character][StatType.MP]),
                maxStats.getPStr()    + MathTool.dice(characterGrowthRates[character][StatType.PSTR]),
                maxStats.getPDef()    + MathTool.dice(characterGrowthRates[character][StatType.PDEF]),
                maxStats.getMStr()    + MathTool.dice(characterGrowthRates[character][StatType.MSTR]),
                maxStats.getMDef()    + MathTool.dice(characterGrowthRates[character][StatType.MDEF]),
                maxStats.getSpeed()   + MathTool.dice(characterGrowthRates[character][StatType.SPEED])
            ),
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

    public boolean earnEXP(int EXP) {
        boolean leveledUp = false;
        float extFactor = 100f;

        if(hands != null)   extFactor += hands.addsEXP;
        if(body != null)    extFactor += body.addsEXP;
        if(feet != null)    extFactor += feet.addsEXP;
        if(head != null)    extFactor += head.addsEXP;

        int extEXP = MathUtils.round(EXP * extFactor / 100f);

        this.EXP += extEXP;

        if(this.EXP > getEXPAvailableAtLevel(level)) {
            this.EXP -= getEXPAvailableAtLevel(level);
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
    public void healCompletely() {
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
        return MathUtils.round(EXP/1.f/getEXPAvailableAtLevel(level)*100f);
    }

    public int getHPfraction() {
        return MathUtils.round(100f*currentStats.HP/getHPmax());
    }

    public int getMPfraction() {
        return MathUtils.round(100f*currentStats.MP/getMPmax());
    }

    /**
     * Calculates how much EXP are available at the given level
     * @param level
     * @return
     */
    public static int getEXPAvailableAtLevel(int level)
    {
        float levelFactor = (float) Math.round(Math.pow(level, Constant.LVL_EXPONENT));
        return MathUtils.floor(Constant.BASE_EXP * levelFactor);
    }

    /**
     * Calculates how much EXP are still needed to reach the next level
     * @return
     */
    public int getEXPtoNextLevel() {
        return (getEXPAvailableAtLevel(level) - EXP);
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

    public int getMaxPossibleHP() {
        int maxPossHP = core.getCommonStatistics().getBaseHP();
        for(int i=1; i<100; i++) {
            maxPossHP += MathTool.dice(characterGrowthRates[character][StatType.HP]);
        }
        return maxPossHP;
    }

    public int getMaxPossibleMP() {
        int maxPossMP = core.getCommonStatistics().getBaseMP();
        for(int i=1; i<100; i++) {
            maxPossMP += MathTool.dice(characterGrowthRates[character][StatType.MP]);
        }
        return maxPossMP;
    }

    public int getMaxPossiblePStr() {
        int maxPossPStr = core.getCommonStatistics().getBasePStr();
        for(int i=1; i<100; i++) {
            maxPossPStr += MathTool.dice(characterGrowthRates[character][StatType.PSTR]);
        }
        return maxPossPStr;
    }

    public int getMaxPossiblePDef() {
        int maxPossPDef = core.getCommonStatistics().getBasePDef();
        for(int i=1; i<100; i++) {
            maxPossPDef += MathTool.dice(characterGrowthRates[character][StatType.PDEF]);
        }
        return maxPossPDef;
    }

    public int getMaxPossibleMStr() {
        int maxPossMStr = core.getCommonStatistics().getBaseMStr();
        for(int i=1; i<100; i++) {
            maxPossMStr += MathTool.dice(characterGrowthRates[character][StatType.MSTR]);
        }
        return maxPossMStr;
    }

    public int getMaxPossibleMDef() {
        int maxPossMDef = core.getCommonStatistics().getBaseMDef();
        for(int i=1; i<100; i++) {
            maxPossMDef += MathTool.dice(characterGrowthRates[character][StatType.MDEF]);
        }
        return maxPossMDef;
    }

    public int getMaxPossibleSpeed() {
        int maxPossSpeed = core.getCommonStatistics().getBaseSpeed();
        for(int i=1; i<100; i++) {
            maxPossSpeed += MathTool.dice(characterGrowthRates[character][StatType.SPEED]);
        }
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

        return MathUtils.round((maxStats.HP * extFactor) / 100f);
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

        return MathUtils.round((maxStats.MP * extFactor) / 100f);
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

    public void setHP(int HP) {
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


    // ............................................................................................. DELEGATIONS

}
