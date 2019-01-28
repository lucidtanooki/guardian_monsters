package de.limbusdev.guardianmonsters.guardians

import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService
import de.limbusdev.guardianmonsters.guardians.items.IItemService
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.*

/**
 * GuardiansServiceLocator
 *
 * This Class combines the Patterns Service Locator and Singleton. Every Service provided must
 * implement the Singleton Pattern and the SingletonService Interface.
 *
 * @author Georg Eckert 2017
 */
object GuardiansServiceLocator
{
    // .................................................... abilities
    var abilities: IAbilityService = NullAbilityService
        get()
        {
            if(field == NullAbilityService)
            {
                error("SERVICES: No AbilityService service injected yet with " +
                        "Services.provide(IAbilityService items). Returning NullAbilityService.")
            }
            return field
        }
    private set

    fun provide(service: IAbilityService)
    {
        abilities = service
    }

    // .................................................... items
    var items: IItemService = NullItemService
        get()
        {
            if(field == NullItemService)
            {
                error("SERVICES: No ItemService service injected yet with " +
                        "Services.provide(IItemService items). Returning NullItemService.")
            }
            return field
        }
    private set

    fun provide(service: IItemService)
    {
        items = service
    }

    // .................................................... species description
    var species: ISpeciesDescriptionService = NullSpeciesService
    get()
    {
        if(field == NullSpeciesService)
        {
            error("SERVICES: No SpeciesDescription service injected yet with " +
                    "Services.provide(ISpeciesDescription service). Returning NullSpeciesDescriptionService.")
        }
        return field
    }
    private set

    fun provide(service: ISpeciesDescriptionService)
    {
        species = service
    }

    // .................................................... guardian factory
    var guardianFactory: AGuardianFactory = NullGuardianFactory
    get()
    {
        if(field == NullGuardianFactory)
        {

            System.err.println("SERVICES: No GuardianFactory service injected yet with " +
                    "Services.provide(AGuardianFactory service). Returning NullGuardianFactory.")
        }
        return field
    }
    private set

    fun provide(service: AGuardianFactory)
    {
        guardianFactory = service
    }

    interface Service
    {
        /**
         * If using Singletons as service, set instance null on destroy
         */
        fun destroy()
    }


    fun destroy()
    {
        species.destroy()
        guardianFactory.destroy()
        items.destroy()
        abilities.destroy()
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Null Services
    object NullAbilityService : IAbilityService
    {
        override fun getAbility(e: Element, index: Int): Ability
        {
            return Ability(1, Ability.DamageType.PHYSICAL, Element.NONE, 0, "attNone1_selfdef")
        }

        override fun getAbility(aID: Ability.aID): Ability
        {
            return Ability(1, Ability.DamageType.PHYSICAL, Element.NONE, 0, "attNone1_selfdef")
        }

        override fun destroy() {}
    }

    object NullItemService : IItemService
    {
        override fun getItem(name: String): Item
        {
            return MedicalItem("bread", 100, MedicalItem.Type.HP_CURE)
        }

        override fun getEquipment(name: String): Equipment
        {
            return BodyEquipment("Jacket", BodyEquipment.Type.ARMOR, 1, 1, 1, 1, 1, 1, 1, 0)
        }

        override fun destroy() {}
    }

    object NullSpeciesService : ISpeciesDescriptionService
    {

        override fun destroy() {}

        override fun getSpeciesDescription(speciesID: Int): SpeciesDescription
        {
            return SpeciesDescription()
        }

        override fun getCommonNameById(speciesID: Int, form: Int): String
        {
            return "NullName"
        }
    }

    object NullGuardianFactory : AGuardianFactory()
    {
        override fun createGuardian(ID: Int, level: Int): AGuardian
        {
            return Guardian("dummyUUID")
        }
    }
}
