package de.limbusdev.guardianmonsters.guardians;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;
import de.limbusdev.guardianmonsters.guardians.items.IItemService;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;

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
    public interface Service
    {
        /**
         * If using Singletons as service, set instance null on destroy
         */
        void destroy();
    }

    private static IAbilityService abilities;
    private static IItemService items;
    private static ISpeciesDescriptionService species;
    private static AGuardianFactory guardianFactory;

    public static void provide(IAbilityService service) {
        abilities = service;
    }

    public static IAbilityService getAbilities()
    {
        if(abilities == null) {

            System.err.println("SERVICES: No AbilityService service injected yet with " +
                "Services.provide(IAbilityService abilities). Returning NullAbilityService.");
            return new IAbilityService()
            {
                @Override
                public Ability getAbility(Element e, int index)
                {
                    return new Ability(1, Ability.DamageType.PHYSICAL, Element.NONE, 0, "attNone1_selfdef");
                }

                @Override
                public void destroy()
                {

                }
            };
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
            return new IItemService()
            {
                @Override
                public Item getItem(String name)
                {
                    return new MedicalItem("bread", 100, MedicalItem.Type.HP_CURE);
                }

                @Override
                public void destroy()
                {

                }
            };

        } else {

            return items;

        }
    }

    public static void provide(ISpeciesDescriptionService service)
    {
        species = service;
    }

    public static ISpeciesDescriptionService getSpecies()
    {
        if(items == null) {

            System.err.println("SERVICES: No SpeciesDescription service injected yet with " +
                "Services.provide(ISpeciesDescription service). Returning NullSpeciesDescriptionService.");
            throw new ExceptionInInitializerError("No SpeciesDescriptionService provided.");

        } else {

            return species;

        }
    }

    public static void provide(AGuardianFactory service)
    {
        guardianFactory = service;
    }

    public static AGuardianFactory getGuardianFactory()
    {
        if(guardianFactory == null) {

            System.err.println("SERVICES: No GuardianFactory service injected yet with " +
                "Services.provide(AGuardianFactory service). Returning NullGuardianFactory.");
            throw new ExceptionInInitializerError("No GuardianFactoryService provided.");

        } else {

            return guardianFactory;

        }
    }

    public static void destroy()
    {
        if(species != null) {
            species.destroy();
            species = null;
        }
        if(guardianFactory != null) {
            guardianFactory.destroy();
            guardianFactory = null;
        }
        if(items != null) {
            items.destroy();
            items = null;
        }
        if(abilities != null) {
            abilities.destroy();
            abilities = null;
        }
    }
}
