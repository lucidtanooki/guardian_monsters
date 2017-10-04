package de.limbusdev.guardianmonsters.guardians;

import com.github.czyzby.autumn.annotation.Inject;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;
import de.limbusdev.guardianmonsters.guardians.items.IItemService;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;

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
    @Inject
    private static IAbilityService abilities;
    @Inject
    private static IItemService items;

    public static void provide(IAbilityService service) {
        abilities = service;
    }

    public static IAbilityService getAbilities()
    {
        if(abilities == null) {

            System.err.println("SERVICES: No AbilityService service injected yet with " +
                "Services.provide(IAbilityService abilities). Returning NullAbilityService.");
            return (Element e, int index)
                -> new Ability(1, Ability.DamageType.PHYSICAL, Element.NONE, 0, "attNone1_selfdef");
        } else {

            return abilities;

        }
    }

    public static void provide(IItemService service)
    {
        items = service;
    }

    public static IItemService getItems()
    {
        if(items == null) {

            System.err.println("SERVICES: No ItemService service injected yet with " +
                "Services.provide(IItemService items). Returning NullItemService.");
            return (String name) -> new MedicalItem("bread", 100, MedicalItem.Type.HP_CURE);

        } else {

            return items;

        }
    }
}
