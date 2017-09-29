package de.limbusdev.guardianmonsters.guardians;

import de.limbusdev.guardianmonsters.guardians.abilities.AbilityDB;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianFactory;

/**
 * GuardiansModuleMain
 *
 * Main Class of the Guardians Module. Only for test purposes.
 *
 * @author Georg Eckert 2017
 */

public class GuardiansModuleMain
{
    public static void main(String[] args)
    {
        System.out.println("#########################");
        System.out.println("#   Module: Guardians   #");
        System.out.println("#########################");
    }

    public void testAttackParsing()
    {
        AbilityDB ai = AbilityDB.getInstance();
    }

    public void testMonsterParsing()
    {
        AGuardianFactory mi = GuardianFactory.getInstance();
        System.out.println("Tested");
    }
}
