package de.limbusdev.utils

import com.badlogic.gdx.math.MathUtils

/**
 * MathTool
 *
 * Provides several mathematical calculations for simple access.
 *
 * @author Georg Eckert 2017
 */
object MathTool
{
    /**
     * Rolls a dice and adds the results
     * @param rolls how many times the dice is rolled
     * @param sides how many sides the used dice has
     * @param add   how much base value should be added to the result
     * @return      the resulting value
     */
    fun dice(rolls: Int, sides: Int, add: Int): Int
    {
        var result = add
        for(i in 0 until rolls)
        {
            result += MathUtils.random(sides)
        }
        return result
    }

    /**
     * Rolls a dice and adds the results
     * @param triple    [rolls, sides, add]
     * rolls ...        how many times the dice is rolled
     * sides ...        how many sides the used dice has
     * add   ...        how much base value should be added to the result
     * @return          the resulting value
     */
    fun dice(triple: IntArray): Int
    {
        if(triple.size < 3)
        {
            throw IllegalArgumentException("Not enough values. Use (rolls, sides, add)")
        }
        return dice(triple[0], triple[1], triple[2])
    }

    /**
     * Returns the best possible result
     * @param triple
     * @return
     */
    fun diceMax(triple: IntArray): Int
    {
        return triple[0] * triple[1] + triple[2]
    }
}
