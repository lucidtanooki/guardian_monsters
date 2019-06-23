package de.limbusdev.guardianmonsters.guardians

import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService
import de.limbusdev.guardianmonsters.guardians.items.IItemService
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
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
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Properties
    private const val TAG = "GuardiansServiceLocator"
    private const val debugString = "[WARN] $TAG: No %s service injected with $TAG.provide(I%s service). Returning Null%sService.\n"

    private fun printNullServiceWarning(name: String) = System.err.format(debugString, name, name, name)


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Methods
    // .................................................... abilities
    var abilities: IAbilityService = NullAbilityService
        get()
        {
            if(field == NullAbilityService) printNullServiceWarning("AbilityService")
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
            if(field == NullItemService) printNullServiceWarning("ItemService")
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
            if(field == NullSpeciesService) printNullServiceWarning("SpeciesService")
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
            if(field == NullGuardianFactory) printNullServiceWarning("GuardianFactory")
            return field
        }
        private set

    fun provide(service: AGuardianFactory)
    {
        guardianFactory = service
    }

    interface Service
    {
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

        override operator fun get(aID: Ability.aID) : Ability = getAbility(aID)

        override fun destroy() {}
    }

    object NullItemService : IItemService
    {
        override fun getItem(name: String): Item
        {
            return MedicalItem("bread", 100, AMedicalItem.Type.HP_CURE)
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
