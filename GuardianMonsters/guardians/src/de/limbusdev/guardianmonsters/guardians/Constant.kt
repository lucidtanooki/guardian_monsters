package de.limbusdev.guardianmonsters.guardians

/**
 * Constant
 *
 * @author Georg Eckert 2017
 */

interface Constant
{
    companion object
    {
        // How much more difficult should it get every new level
        // to reach the next one
        const val LVL_EXPONENT = 3f

        const val DEBUGGING_ON = true

        const val PRINT_PARSED_GUARDIAN = false

        const val LEFT = true

        const val RIGHT = false

        val HERO = Side.LEFT

        val OPPONENT = Side.RIGHT
    }
}
