package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.utils.Constant;
import de.limbusdev.guardianmonsters.utils.MathTool;

import static de.limbusdev.guardianmonsters.model.Stat.Growth.FAST;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.FASTHP;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.FASTMP;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.MED;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.MEDHP;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.MEDMP;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.SLOW;
import static de.limbusdev.guardianmonsters.model.Stat.Growth.SLOWHP;

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

public class Stat {

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

    public final int character;   // for growth rates

    private int HP, MP, PStr, PDef, MStr, MDef, Speed;
    private int HPmax, MPmax, PStrMax, PDefMax, MStrMax, MDefMax, SpeedMax;
    public Array<Element> elements;

    private Equipment hands;
    private Equipment head;
    private Equipment body;
    private Equipment feet;

    private LevelUpReport lvlUpReport;

    // ................................................................................. CONSTRUCTOR
    public Stat(int level, BaseStat baseStat, Array<Element> elements) {

        // Choose a random character
        switch(MathUtils.random(0,2)) {
            case 2:  character = Character.VIVACIOUS; break;
            case 1:  character = Character.PRUDENT; break;
            default: character = Character.BALANCED; break;
        }

        this.level = level;
        this.abilityLevels = level;

        this.HPmax      = baseStat.baseHP;
        this.MPmax      = baseStat.baseMP;
        this.PStrMax    = baseStat.basePStr;
        this.PDefMax    = baseStat.basePDef;
        this.MStrMax    = baseStat.baseMStr;
        this.MDefMax    = baseStat.baseMDef;
        this.SpeedMax   = baseStat.baseSpeed;

        for(int i=1; i<level; i++) {
            levelUp();
        }

        healCompletely();

        this.EXP = 0;
        this.elements = elements;

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

        return report;
    }

    public void earnEXP(int EXP) {
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
        }
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


    // ........................................................................... CALCULATED VALUES

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
        switch(equipment.type) {
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
        return oldEquipment;
    }

    // ........................................................................... SETTERS & GETTERS

    public int getLevel() {
        return level;
    }

    public int getAbilityLevels() {
        return abilityLevels;
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

    /**
     * @return maximum HP, taking {@link Equipment} into account
     */
    public int getHPmax() {
        float extFactor = 100f;

        if(hands != null)   extFactor += hands.addsHP;
        if(body != null)    extFactor += body.addsHP;
        if(feet != null)    extFactor += feet.addsHP;
        if(head != null)    extFactor += head.addsHP;

        return MathUtils.round((HP * extFactor) / 100f);
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

        return MathUtils.round((MP * extFactor) / 100f);
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

    public Array<Element> getElements() {
        return elements;
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
    }

    public void setMP(int MP) {
        this.MP = MP;
    }

    public void setPStr(int PStr) {
        this.PStr = PStr;
    }

    public void setPDef(int PDef) {
        this.PDef = PDef;
    }

    public void setMStr(int MStr) {
        this.MStr = MStr;
    }

    public void setMDef(int MDef) {
        this.MDef = MDef;
    }

    public void setSpeed(int speed) {
        Speed = speed;
    }

    public static class LevelUpReport {
        public int oldHP, oldMP, oldPStr, oldPDef, oldMStr, oldMDef, oldSpeed;
        public int newHP, newMP, newPStr, newPDef, newMStr, newMDef, newSpeed;
        public int oldLevel, newLevel;

        public LevelUpReport(int oldHP, int oldMP, int oldPStr, int oldPDef, int oldMStr, int oldMDef, int oldSpeed,
                             int newHP, int newMP, int newPStr, int newPDef, int newMStr, int newMDef, int newSpeed,
                             int oldLevel, int newLevel) {
            this.oldHP = oldHP;
            this.oldMP = oldMP;
            this.oldPStr = oldPStr;
            this.oldPDef = oldPDef;
            this.oldMStr = oldMStr;
            this.oldMDef = oldMDef;
            this.oldSpeed = oldSpeed;
            this.newHP = newHP;
            this.newMP = newMP;
            this.newPStr = newPStr;
            this.newPDef = newPDef;
            this.newMStr = newMStr;
            this.newMDef = newMDef;
            this.newSpeed = newSpeed;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
        }
    }

    public LevelUpReport getLatestLevelUpReport() {
        return lvlUpReport;
    }

}
