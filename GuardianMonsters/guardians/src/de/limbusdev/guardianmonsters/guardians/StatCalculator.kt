package de.limbusdev.guardianmonsters.guardians

import com.badlogic.gdx.math.MathUtils

import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.guardians.monsters.Statistics

/**
 * StatCalculator
 *
 * For Formulas see "Documents/Stat- and Battle-Calculations.md"
 *
 * @author Georg Eckert 2017
 */
object StatCalculator
{
    /**
     * @param cf    character factor
     * @param lvl
     * @param baseHP
     * @param indiBaseHP
     * @param growthHP
     * @return
     */
    fun calculateHP(cf: Float, lvl: Int, baseHP: Int, indiBaseHP: Int, growthHP: Int): Int
    {
        return lvl * 10 + 100 + MathUtils.floor(cf * ((2f * baseHP + indiBaseHP.toFloat() + (growthHP * lvl).toFloat()) / 10f))
    }

    /**
     *
     * @param cf    character factor
     * @param lvl
     * @param baseMP
     * @param indiBaseMP
     * @param growthMP
     * @return
     */
    fun calculateMP(cf: Float, lvl: Int, baseMP: Int, indiBaseMP: Int, growthMP: Int): Int
    {
        return lvl * 2 + 20 + MathUtils.floor(cf * ((2f * baseMP + indiBaseMP.toFloat() + (growthMP * lvl).toFloat()) / 20f))
    }

    /**
     * For PStr, PDef, MStr, MDef, Speed
     * @param cf character factor
     * @return
     */
    fun calculateStat(cf: Float, lvl: Int, base: Int, indiBase: Int, growth: Int): Int
    {
        return lvl + 50 + MathUtils.floor(cf * ((2f * base + indiBase.toFloat() + (growth * lvl).toFloat()) / 10f))
    }

    /**
     * Calculates how much EXP are available at the given level
     * @param level
     * @return
     */
    fun calcEXPtoReachLevel(level: Int): Int
    {
        if(level <= 1) return 0
        val levelFactor = Math.floor(Math.pow(level.toDouble(), Constant.LVL_EXPONENT.toDouble())).toFloat()
        return MathUtils.floor(levelFactor)
    }

    /**
     * Takes all base values of a guardians [IndividualStatistics] and calculates the complete
     * Stats from them.
     * @param characterFactors
     * @param level
     * @param commonBaseValues
     * @param individualBaseValues
     * @param growthBaseValues
     * @return
     */
    fun calculateAllStats(
            characterFactors: FloatArray,
            level: Int,
            commonBaseValues: Statistics,
            individualBaseValues: Statistics,
            growthBaseValues: Statistics): Statistics
    {
        return Statistics(
                calculateHP(characterFactors[IndividualStatistics.StatType.HP], level,
                        commonBaseValues.hp, individualBaseValues.hp, growthBaseValues.hp),
                calculateMP(characterFactors[IndividualStatistics.StatType.MP], level,
                        commonBaseValues.mp, individualBaseValues.mp, growthBaseValues.mp),
                calculateStat(characterFactors[IndividualStatistics.StatType.PSTR], level,
                        commonBaseValues.pStr, individualBaseValues.pStr, growthBaseValues.pStr),
                calculateStat(characterFactors[IndividualStatistics.StatType.PDEF], level,
                        commonBaseValues.pDef, individualBaseValues.pDef, growthBaseValues.pDef),
                calculateStat(characterFactors[IndividualStatistics.StatType.MSTR], level,
                        commonBaseValues.mStr, individualBaseValues.mStr, growthBaseValues.mStr),
                calculateStat(characterFactors[IndividualStatistics.StatType.MDEF], level,
                        commonBaseValues.mDef, individualBaseValues.mDef, growthBaseValues.mDef),
                calculateStat(characterFactors[IndividualStatistics.StatType.SPEED], level,
                        commonBaseValues.speed, individualBaseValues.speed, growthBaseValues.speed)
        )
    }

    fun calcEXPavailableAtLevel(level: Int): Int {
        return calcEXPtoReachLevel(level + 1) - calcEXPtoReachLevel(level)
    }
}
