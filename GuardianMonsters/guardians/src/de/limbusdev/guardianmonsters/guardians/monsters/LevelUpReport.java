package de.limbusdev.guardianmonsters.guardians.monsters;

/**
 * LevelUpReport
 *
 * @author Georg Eckert 2017
 */

public class LevelUpReport {
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

    /**
     * For Serialization only!
     */
    LevelUpReport() {}
}
