package de.limbusdev.guardianmonsters.guardians;

import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.Statistics;

/**
 * StatCalculator
 *
 * For Formulas see "Documents/Stat- and Battle-Calculations.md"
 *
 * @author Georg Eckert 2017
 */

public class StatCalculator
{
    /**
     * @param cf    character factor
     * @param lvl
     * @param baseHP
     * @param indiBaseHP
     * @param growthHP
     * @return
     */
    public static int calculateHP(float cf, int lvl, int baseHP, int indiBaseHP, int growthHP)
    {
        int HP = lvl * 10 + 100 + MathUtils.floor(cf * ((2f * baseHP + indiBaseHP + growthHP*lvl) / 10f));
        return HP;
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
    public static int calculateMP(float cf, int lvl, int baseMP, int indiBaseMP, int growthMP)
    {
        int MP = lvl * 2 + 20 + MathUtils.floor(cf * ((2f * baseMP + indiBaseMP + growthMP*lvl) / 20f));
        return MP;
    }

    /**
     * For PStr, PDef, MStr, MDef, Speed
     * @param cf character factor
     * @return
     */
    public static int calculateStat(float cf, int lvl, int base, int indiBase, int growth)
    {
        int stat = lvl + 50 + MathUtils.floor(cf * ((2f * base + indiBase + growth*lvl) / 10f));
        return stat;
    }

    /**
     * Calculates how much EXP are available at the given level
     * @param level
     * @return
     */
    public static int calcEXPtoReachLevel(int level)
    {
        if(level <= 1) return 0;
        float levelFactor = (float) Math.floor(Math.pow(level, Constant.LVL_EXPONENT));
        return MathUtils.floor(levelFactor);
    }

    /**
     * Takes all base values of a guardians {@link IndividualStatistics} and calculates the complete
     * Stats from them.
     * @param characterFactors
     * @param level
     * @param commonBaseValues
     * @param individualBaseValues
     * @param growthBaseValues
     * @return
     */
    public static Statistics calculateAllStats(
        float[] characterFactors, int level,
        Statistics commonBaseValues, Statistics individualBaseValues, Statistics growthBaseValues)
    {
        return new Statistics(
            calculateHP(characterFactors[IndividualStatistics.StatType.HP], level,
                commonBaseValues.getHP(), individualBaseValues.getHP(), growthBaseValues.getHP()),
            calculateMP(characterFactors[IndividualStatistics.StatType.MP], level,
                commonBaseValues.getMP(), individualBaseValues.getMP(), growthBaseValues.getMP()),
            calculateStat(characterFactors[IndividualStatistics.StatType.PSTR], level,
                commonBaseValues.getPStr(), individualBaseValues.getPStr(), growthBaseValues.getPStr()),
            calculateStat(characterFactors[IndividualStatistics.StatType.PDEF], level,
                commonBaseValues.getPDef(), individualBaseValues.getPDef(), growthBaseValues.getPDef()),
            calculateStat(characterFactors[IndividualStatistics.StatType.MSTR], level,
                commonBaseValues.getMStr(), individualBaseValues.getMStr(), growthBaseValues.getMStr()),
            calculateStat(characterFactors[IndividualStatistics.StatType.MDEF], level,
                commonBaseValues.getMDef(), individualBaseValues.getMDef(), growthBaseValues.getMDef()),
            calculateStat(characterFactors[IndividualStatistics.StatType.SPEED], level,
                commonBaseValues.getSpeed(), individualBaseValues.getSpeed(), growthBaseValues.getSpeed())
        );
    }

    public static int calcEXPavailableAtLevel(int level)
    {
        return calcEXPtoReachLevel(level+1) - calcEXPtoReachLevel(level);
    }
}
