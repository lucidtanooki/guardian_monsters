package de.limbusdev.utils;

import com.badlogic.gdx.math.MathUtils;

/**
 * MathTool
 *
 * Provides several mathematical calculations for simple access.
 *
 * @author Georg Eckert 2017
 */
public class MathTool
{
    /**
     * Rolls a dice and adds the results
     * @param rolls how many times the dice is rolled
     * @param sides how many sides the used dice has
     * @param add   how much base value should be added to the result
     * @return      the resulting value
     */
    public static int dice(int rolls, int sides, int add)
    {
        int result=add;
        for(int i=0; i<rolls; i++)
        {
            result += MathUtils.random(sides);
        }
        return result;
    }

    /**
     * Rolls a dice and adds the results
     * @param triple    [rolls, sides, add]
     * rolls ...        how many times the dice is rolled
     * sides ...        how many sides the used dice has
     * add   ...        how much base value should be added to the result
     * @return          the resulting value
     */
    public static int dice(int[] triple)
    {
        if(triple.length < 3)
        {
            throw new IllegalArgumentException("Not enough values. Use (rolls, sides, add)");
        }
        return dice(triple[0], triple[1], triple[2]);
    }
}
