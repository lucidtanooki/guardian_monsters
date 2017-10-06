package de.limbusdev.guardianmonsters.guardians;

import com.badlogic.gdx.math.MathUtils;

/**
 * StatCalculator
 *
 * @author Georg Eckert 2017
 */

public class StatCalculator
{
    /**
     * newHP = character * newLevel + 11 + floor( (newLevel / 100) * (2*baseHP + indBaseHP + floor(growthHP/4))
     * @param characterFactor
     * @param level
     * @param baseHP
     * @param individualBaseHP
     * @param growthHP
     * @return
     */
    public static int calculateHP(float characterFactor, int level, int baseHP, int individualBaseHP, int growthHP)
    {
        int HP = level + 11 + MathUtils.floor(
            characterFactor * (
                (level / 100f) * (2f * baseHP + individualBaseHP + MathUtils.floor(growthHP / 4f))
            )
        );
        return HP;
    }

    public static int calculateMP(float characterFactor, int level, int baseMP, int individualBaseMP, int growthMP)
    {
        int MP = level + 10 + MathUtils.floor(
            characterFactor * (
                (level / 200f) * (2f * baseMP + individualBaseMP + MathUtils.floor(growthMP / 4f))
            )
        );
        return MP;
    }

    /**
     * For PStr, PDef, MStr, MDef, Speed
     * @return
     */
    public static int calculateStat(float characterFactor, int level, int base, int individualBase, int growth)
    {
        int stat = MathUtils.floor(characterFactor * (6f + MathUtils.floor((level/100f) * (2f*base+individualBase+MathUtils.floor(growth/4f)))));
        return stat;
    }
}
