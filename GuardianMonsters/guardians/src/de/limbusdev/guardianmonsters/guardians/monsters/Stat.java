package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.items.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.EquipmentPotential;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.utils.MathTool;

import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.FAST;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.FASTHP;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.FASTMP;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.MED;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.MEDHP;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.MEDMP;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.SLOW;
import static de.limbusdev.guardianmonsters.guardians.monsters.Stat.Growth.SLOWHP;

/**
 * Stat contains all statistic values of a {@link Monster}. The statistic values at level 1 should
 * be copied over from {@link BaseStat}
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

public class Stat extends Signal<Stat> {

    public static abstract class Character {
        public static final int BALANCED=0, VIVACIOUS=1, PRUDENT=2;
    }

    public static abstract class Growth {
        public static final int[] SLOW={1,2,0}, MED={1,3,0}, FAST={3,2,0};
        public static final int[] SLOWHP={4,30,100}, MEDHP={4,40,100}, FASTHP={4,50,100};
        public static final int[] SLOWMP={2,20,30},  MEDMP={3,30,40},  FASTMP={4,30,50};
    }

    public static abstract class StatType {
        public static final int PSTR=0, PDEF=1, MSTR=2, MDEF=3, SPEED=4, HP=5, MP=6;
    }

    public static final int[][][] characterGrowthRates = {
        {MED, MED, MED, MED, MED, MEDHP, MEDMP},        // BALANCED
        {FAST, SLOW, MED, SLOW, FAST, SLOWHP, MEDMP},   // VIVACIOUS
        {MED, FAST, SLOW, FAST, SLOW, FASTHP, FASTMP}   // PRUDENT
    };

    private int level;
    private int abilityLevels;
    private int EXP;

    public int character;   // for growth rates
    public BaseStat base;

    private int HP, MP, PStr, PDef, MStr, MDef, Speed;
    private int HPmax, MPmax, PStrMax, PDefMax, MStrMax, MDefMax, SpeedMax;

    private Equipment hands;
    private Equipment head;
    private Equipment body;
    private Equipment feet;

    private LevelUpReport lvlUpReport;

    // ................................................................................. CONSTRUCTOR

    /**
     * For Serialization only!
     */
    public Stat(int level, int abilityLevels, int EXP, int character, BaseStat base,
                int HP, int MP, int PStr, int PDef, int MStr, int MDef, int speed,
                int HPmax, int MPmax, int PStrMax, int PDefMax, int MStrMax, int MDefMax, int speedMax,
                Equipment hands, Equipment head, Equipment body, Equipment feet) {
        this.level = level;
        this.abilityLevels = abilityLevels;
        this.EXP = EXP;
        this.character = character;
        this.base = base;
        this.HP = HP;
        this.MP = MP;
        this.PStr = PStr;
        this.PDef = PDef;
        this.MStr = MStr;
        this.MDef = MDef;
        this.Speed = speed;
        this.HPmax = HPmax;
        this.MPmax = MPmax;
        this.PStrMax = PStrMax;
        this.PDefMax = PDefMax;
        this.MStrMax = MStrMax;
        this.MDefMax = MDefMax;
        this.SpeedMax = speedMax;
        this.hands = hands;
        this.head = head;
        this.body = body;
        this.feet = feet;
        lvlUpReport = new LevelUpReport(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
    }

    public Stat(int level, BaseStat baseStat, int character) {
        construct(level, baseStat, character);
    }

    public Stat(int level, BaseStat baseStat) {
        int character;
        // Choose a random character
        switch(MathUtils.random(0,2)) {
            case 2:  character = Character.VIVACIOUS; break;
            case 1:  character = Character.PRUDENT; break;
            default: character = Character.BALANCED; break;
        }
        construct(level, baseStat, character);
    }

    private void construct(int level, BaseStat baseStat, int character) {
        this.base = baseStat;
        this.character = character;

        this.level = level;
        this.abilityLevels = level-1;

        this.HPmax      = baseStat.getBaseHP();
        this.MPmax      = baseStat.getBaseMP();
        this.PStrMax    = baseStat.getBasePStr();
        this.PDefMax    = baseStat.getBasePDef();
        this.MStrMax    = baseStat.getBaseMStr();
        this.MDefMax    = baseStat.getBaseMDef();
        this.SpeedMax   = baseStat.getBaseSpeed();

        for(int i=1; i<level; i++) {
            levelUp();
        }

        healCompletely();

        this.EXP = 0;

        lvlUpReport = new LevelUpReport(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
    }

    // ..................................................................................... METHODS
    private LevelUpReport levelUp() {

        LevelUpReport report = new LevelUpReport(
            this.HPmax, this.MPmax, this.PStrMax, this.PDefMax, this.MStrMax, this.MDefMax, this.SpeedMax,
            this.HPmax      + MathTool.dice(characterGrowthRates[character][StatType.HP]),
            this.MPmax      + MathTool.dice(characterGrowthRates[character][StatType.MP]),
            this.PStrMax    + MathTool.dice(characterGrowthRates[character][StatType.PSTR]),
            this.PDefMax    + MathTool.dice(characterGrowthRates[character][StatType.PDEF]),
            this.MStrMax    + MathTool.dice(characterGrowthRates[character][StatType.MSTR]),
            this.MDefMax    + MathTool.dice(characterGrowthRates[character][StatType.MDEF]),
            this.SpeedMax   + MathTool.dice(characterGrowthRates[character][StatType.SPEED]),
            this.level,
            this.level + 1
        );

        this.level      = report.newLevel;
        this.HPmax      = report.newHP;
        this.MPmax      = report.newMP;
        this.PStrMax    = report.newPStr;
        this.PDefMax    = report.newPDef;
        this.MStrMax    = report.newMStr;
        this.MDefMax    = report.newMDef;
        this.SpeedMax   = report.newSpeed;

        this.abilityLevels += 1;

        this.lvlUpReport = report;

        dispatch(this);

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

        dispatch(this);

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
        setHP(HP + value);
    }

    public void decreaseHP(int value) {
        setHP(HP - value);
    }

    public void healMP(int value) {
        setMP(MP + value);
    }

    public void decreaseMP(int value) {
        setMP(MP - value);
    }

    /**
     * Increases Physical Strength, by the given fraction (%)
     * @param fraction
     */
    public void increasePStr(int fraction) {
        setPStr(MathUtils.round(PStr * (100 + fraction)/(100f)));
    }

    /**
     * Increases Physical Defense, by the given fraction (%)
     * @param fraction
     */
    public void increasePDef(int fraction) {
        setPDef(MathUtils.round(PDef * (100 + fraction)/(100f)));
    }

    /**
     * Increases Magical Strength, by the given fraction (%)
     * @param fraction
     */
    public void increaseMStr(int fraction) {
        setMStr(MathUtils.round(MStr * (100 + fraction)/(100f)));
    }

    /**
     * Increases Magical Defense, by the given fraction (%)
     * @param fraction
     */
    public void increaseMDef(int fraction) {
        setMDef(MathUtils.round(MDef * (100 + fraction)/(100f)));
    }

    /**
     * Increases Speed, by the given fraction (%)
     * @param fraction
     */
    public void increaseSpeed(int fraction) {
        setSpeed(MathUtils.round(Speed * (100 + fraction)/(100f)));
    }

    // ........................................................................... CALCULATED VALUES

    public String getHPfractionAsString() {
        return (Integer.toString(HP) + "/" + Integer.toString(getHPmax()));
    }

    public String getMPfractionAsString() {
        return (Integer.toString(MP) + "/" + Integer.toString(getMPmax()));
    }

    public int getEXPfraction() {
        return MathUtils.round(EXP/1.f/getEXPAvailableAtLevel(level)*100f);
    }

    public int getHPfraction() {
        return MathUtils.round(100f*HP/getHPmax());
    }

    public int getMPfraction() {
        return MathUtils.round(100f*MP/getMPmax());
    }

    /**
     * Calculates how much EXP are available at the given level
     * @param level
     * @return
     */
    public static int getEXPAvailableAtLevel(int level) {
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

    public Equipment giveEquipment(Equipment equipment) {
        Equipment oldEquipment=null;
        switch(equipment.bodyPart) {
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

        dispatch(this);

        return oldEquipment;
    }

    /**
     * Calculates the {@link EquipmentPotential} of a given {@link Equipment} for this {@link Monster}
     * @param eq
     * @return
     */
    public EquipmentPotential getEquipmentPotential(Equipment eq) {
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

    // ........................................................................... SETTERS & GETTERS

    public int getLevel() {
        return level;
    }

    public boolean isFit() {
        return HP > 0;
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
        return HP;
    }

    public int getMP() {
        return MP;
    }

    public int getPStr() {
        return PStr;
    }

    public int getPDef() {
        return PDef;
    }

    public int getMStr() {
        return MStr;
    }

    public int getMDef() {
        return MDef;
    }

    public int getSpeed() {
        return Speed;
    }

    public int getMaxPossibleHP() {
        int maxPossHP = base.getBaseHP();
        for(int i=1; i<100; i++) {
            maxPossHP += MathTool.dice(characterGrowthRates[character][StatType.HP]);
        }
        return maxPossHP;
    }

    public int getMaxPossibleMP() {
        int maxPossMP = base.getBaseMP();
        for(int i=1; i<100; i++) {
            maxPossMP += MathTool.dice(characterGrowthRates[character][StatType.MP]);
        }
        return maxPossMP;
    }

    public int getMaxPossiblePStr() {
        int maxPossPStr = base.getBasePStr();
        for(int i=1; i<100; i++) {
            maxPossPStr += MathTool.dice(characterGrowthRates[character][StatType.PSTR]);
        }
        return maxPossPStr;
    }

    public int getMaxPossiblePDef() {
        int maxPossPDef = base.getBasePDef();
        for(int i=1; i<100; i++) {
            maxPossPDef += MathTool.dice(characterGrowthRates[character][StatType.PDEF]);
        }
        return maxPossPDef;
    }

    public int getMaxPossibleMStr() {
        int maxPossMStr = base.getBaseMStr();
        for(int i=1; i<100; i++) {
            maxPossMStr += MathTool.dice(characterGrowthRates[character][StatType.MSTR]);
        }
        return maxPossMStr;
    }

    public int getMaxPossibleMDef() {
        int maxPossMDef = base.getBaseMDef();
        for(int i=1; i<100; i++) {
            maxPossMDef += MathTool.dice(characterGrowthRates[character][StatType.MDEF]);
        }
        return maxPossMDef;
    }

    public int getMaxPossibleSpeed() {
        int maxPossSpeed = base.getBaseSpeed();
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

        return MathUtils.round((HPmax * extFactor) / 100f);
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

        return MathUtils.round((MPmax * extFactor) / 100f);
    }

    /**
     * @return maximum Pstr, taking {@link Equipment} into account
     */
    public int getPStrMax() {
        int extPStr = PStrMax;

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
        int extPDef = PDefMax;

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
        int extMStr = MStrMax;

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
        int extMDef = MDefMax;

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
        int extSpeed = SpeedMax;

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
        this.HP = HP;
        if(HP > getHPmax()) this.HP = getHPmax();
        if(HP < 0)          this.HP = 0;

        dispatch(this);
    }

    public void setMP(int MP) {
        this.MP = MP;

        if(MP > getMPmax()) this.MP = getMPmax();
        if(MP < 0)          this.MP = 0;

        dispatch(this);
    }

    public void setPStr(int PStr) {
        this.PStr = PStr;

        if(PStr > getPStrMax()*1.5f) this.PStr = getPStrMax();
        if(PStr < 1)            this.PStr = 1;

        dispatch(this);
    }

    public void setPDef(int PDef) {
        this.PDef = PDef;

        if(PDef > getPDefMax()*1.5f) this.PDef = getPDefMax();
        if(PDef < 1)            this.PDef = 1;

        dispatch(this);
    }

    public void setMStr(int MStr) {
        this.MStr = MStr;

        if(MStr > getMStrMax()*1.5f) this.MStr = getMStrMax();
        if(MStr < 1)            this.MStr = 1;

        dispatch(this);
    }

    public void setMDef(int MDef) {
        this.MDef = MDef;

        if(MDef > getMDefMax()*1.5f) this.MDef = getMDefMax();
        if(MDef < 1)            this.MDef = 1;

        dispatch(this);
    }

    public void setSpeed(int Speed) {
        this.Speed = Speed;

        if(Speed > getSpeedMax()) this.Speed = getSpeedMax();
        if(Speed < 1)          this.Speed = 1;

        dispatch(this);
    }

    /**
     * Reduces the number of available ability levels by 1,
     * usually, when a field in the ability board is activated
     */
    public void consumeAbilityLevel() {
        this.abilityLevels--;
        dispatch(this);
    }


    public LevelUpReport getLatestLevelUpReport() {
        return lvlUpReport;
    }

}
