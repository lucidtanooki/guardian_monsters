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
        @JvmField
        val LVL_EXPONENT = 3f

        @JvmField
        val DEBUGGING_ON = true

        @JvmField
        val PRINT_PARSED_GUARDIAN = false

        @JvmField
        val LEFT = true

        @JvmField
        val RIGHT = false

        @JvmField
        val HERO = true

        @JvmField
        val OPPONENT = false
    }
}
