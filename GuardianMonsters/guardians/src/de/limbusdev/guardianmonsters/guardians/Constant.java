package de.limbusdev.guardianmonsters.guardians;

/**
 * Constant
 *
 * @author Georg Eckert 2017
 */

public interface Constant
{
    // How much more difficult should it get every new level
    // to reach the next one
    float LVL_EXPONENT = 1.5f;
    // Base Value: 100f to reach Level 1, 2^1.5 to reach level 2 aso
    float BASE_EXP = 1000f;

    boolean DEBUGGING_ON = true;
}
