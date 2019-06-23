package de.limbusdev.guardianmonsters.guardians.abilities


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue

import java.util.ArrayList

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics

/**
 * Contains all existing attacks, sorted by element
 * @author Georg Eckert 2017
 */
class AbilityService
private constructor(jsonAbilitiesResources: ArrayMap<Element, String>)
    : IAbilityService
{
    // ............................................................................... Inner Classes
    /**
     * Simple Container for JSON parsed Object
     */
    private class JsonAbility
    {
        var ID                              : Int = 0
        var name                            : String = ""
        var damage                          : Int = 0
        var MPcost                          : Int = 0
        var damageType                      : String = "PHYSICAL"
        var element                         : String = "NONE"
        var statusEffect                    : String = "HEALTHY"
        var canChangeStatusEffect           : Boolean = false
        var probabilityToChangeStatusEffect : Int = 0
        var areaDamage                      : Boolean = false

        var modifiedStats                   : JsonAbilityModifiedStats = JsonAbilityModifiedStats()
        var healedStats                     : JsonAbilityHealedStats   = JsonAbilityHealedStats()

        class JsonAbilityModifiedStats
        {
            var PStr  : Int = 0
            var PDef  : Int = 0
            var MStr  : Int = 0
            var MDef  : Int = 0
            var Speed : Int = 0
            fun changesStats() = (PStr != 0 || PDef != 0 || MStr != 0 || MDef != 0 || Speed != 0)
        }

        class JsonAbilityHealedStats
        {
            var HP : Int = 0
            var MP : Int = 0
            fun curesStats() = (HP != 0 || MP != 0)
        }

        override fun toString() = "Ability:\n$ID $name\nDamage: $damage MPcost: $MPcost"
    }


    // ................................................................................ Constructors
    init
    {
        for (key in jsonAbilitiesResources.keys())
        {
            val elAbilities = readAbilitiesFromJsonString(jsonAbilitiesResources.get(key))
            abilities.put(key, elAbilities)
        }
    }

    /**
     * Returns attack of the given element and index
     * @param e
     * @param index
     * @return
     */
    override fun getAbility(e: Element, index: Int) : Ability = abilities.get(e).get(index)

    override operator fun get(aID: Ability.aID): Ability = getAbility(aID)

    override fun getAbility(aID: Ability.aID) : Ability = getAbility(aID.element, aID.ID)

    override fun destroy() { instance = null }

    companion object
    {
        private var instance  : AbilityService? = null
        private var abilities : ArrayMap<Element, ArrayMap<Int, Ability>> = ArrayMap()

        private fun readAbilitiesFromJsonString(jsonString: String): ArrayMap<Int, Ability>
        {
            val elAbilities = ArrayMap<Int, Ability>()
            val json = Json()

            val elementList: ArrayList<JsonValue>

            elementList = if (json.fromJson(ArrayList::class.java, jsonString) != null)
            {
                json.fromJson(ArrayList::class.java, jsonString) as ArrayList<JsonValue>;
            }
            else ArrayList()

            var jsa: JsonAbility
            var ability: Ability
            for (v in elementList)
            {
                jsa = json.readValue(JsonAbility::class.java, v)
                ability = Ability(
                        jsa.ID,
                        Ability.DamageType.valueOf(jsa.damageType.toUpperCase()),
                        Element.valueOf(jsa.element.toUpperCase()),
                        jsa.damage,
                        jsa.name,
                        jsa.MPcost,
                        jsa.areaDamage,
                        jsa.canChangeStatusEffect,
                        IndividualStatistics.StatusEffect.valueOf(jsa.statusEffect.toUpperCase()),
                        jsa.probabilityToChangeStatusEffect,
                        jsa.modifiedStats.changesStats(),
                        jsa.modifiedStats.PStr,
                        jsa.modifiedStats.PDef,
                        jsa.modifiedStats.MStr,
                        jsa.modifiedStats.MDef,
                        jsa.modifiedStats.Speed,
                        jsa.healedStats.curesStats(),
                        jsa.healedStats.HP,
                        jsa.healedStats.MP
                )
                elAbilities.put(ability.ID, ability)
            }

            return elAbilities
        }

        /**
         * Best practice: Use only once, when providing [IAbilityService] to
         * [GuardiansServiceLocator], afterwards always retrieve it from there.
         *
         * @param jsonAbilitiesResources
         * @return
         */
        @Synchronized
        fun getInstance(jsonAbilitiesResources: ArrayMap<Element, String>): AbilityService
        {
            if (instance == null) { instance = AbilityService(jsonAbilitiesResources) }
            return instance as AbilityService
        }

        /**
         * Best practice: Use only once, when providing [IAbilityService] to
         * [GuardiansServiceLocator], afterwards always retrieve it from there.
         *
         * @param jsonFilePaths Files that contain Abilities in Json format
         * @return
         */
        @Synchronized
        fun getInstanceFromFile(jsonFilePaths: ArrayMap<Element, String>): AbilityService
        {
            val jsonResources = ArrayMap<Element, String>()
            for (key in jsonFilePaths.keys())
            {
                val handleJson = Gdx.files.internal(jsonFilePaths.get(key))
                val jsonString = handleJson.readString()
                jsonResources.put(key, jsonString)
            }

            return getInstance(jsonResources)
        }
    }
}