package de.limbusdev.guardianmonsters.guardians.monsters;

/**
 * AGuardianFactory
 *
 * Design Pattern: Factory Method
 *
 * @author Georg Eckert 2017
 */

public abstract class AGuardianFactory
{
    public abstract AGuardian createGuardian(int ID, int level);
}
