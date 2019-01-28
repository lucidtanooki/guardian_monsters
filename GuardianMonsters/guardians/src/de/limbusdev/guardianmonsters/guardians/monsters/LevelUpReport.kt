package de.limbusdev.guardianmonsters.guardians.monsters

/**
 * LevelUpReport
 *
 * @author Georg Eckert 2017
 */

class LevelUpReport
(
        var oldStats: Statistics = Statistics(),
        var newStats: Statistics = Statistics(),
        var oldLevel: Int = 0,
        var newLevel: Int = 0

) {
    override fun toString(): String
    {
        return  "Before: Lvl $oldLevel, $oldStats\n" +
                "After:  Lvl $newLevel, $newStats"
    }
}
