package de.limbusdev.guardianmonsters.guardians;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;

/**
 * GuardiansServiceLocator
 *
 * This Class combines the Patterns Service Locator and Singleton. Every Service provided must
 * implement the Singleton Pattern and the SingletonService Interface.
 *
 * @author Georg Eckert 2017
 */

public class GuardiansServiceLocator
{
    private static IAbilityService abilities;

    public static void provide(IAbilityService service) {
        abilities = service;
    }

    public static IAbilityService getAbilities() {
        if(abilities == null) {

            System.err.println("SERVICES: No AbilityService service injected yet with " +
                "Services.provide(IAbilityService abilities). Returning NullAbilityService.");
            return (Element e, int index)
                -> new Ability(1, Ability.DamageType.PHYSICAL, Element.NONE, 0, "attNone1_selfdef");

        } else {

            return abilities;

        }
    }
}
