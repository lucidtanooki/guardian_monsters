package de.limbusdev.guardianmonsters.guardians.monsters;

/**
 * LevelUpReport
 *
 * @author Georg Eckert 2017
 */

public class LevelUpReport
{
    public Statistics oldStats;
    public Statistics newStats;
    public int oldLevel, newLevel;

    public LevelUpReport(Statistics oldStats, Statistics newStats, int oldLevel, int newLevel)
    {
        this.oldStats = oldStats;
        this.newStats = newStats;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    /**
     * For Serialization only!
     */
    LevelUpReport() {}
}
