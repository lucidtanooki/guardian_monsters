package de.limbusdev.guardianmonsters.guardians.battle

import java.util.Comparator

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * @author Georg Eckert 2019
 */
class MonsterSpeedComparator : Comparator<AGuardian>
{
    override fun compare(o1: AGuardian, o2: AGuardian): Int
    {
        return o1.individualStatistics.speed - o2.individualStatistics.speed
    }
}
