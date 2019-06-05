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
class AbilityService private constructor(jsonAbilitiesResources: ArrayMap<Element, String>) : IAbilityService {

    init {
        abilities = ArrayMap()

        for (key in jsonAbilitiesResources.keys()) {
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
    override fun getAbility(e: Element, index: Int): Ability {
        return abilities.get(e).get(index)
    }

    override fun getAbility(aID: Ability.aID): Ability {
        return getAbility(aID.element, aID.ID)
    }

    override fun destroy() {
        instance = null
    }

    /**
     * Simple Container for JSON parsed Object
     */
    private class JsonAbility {

        var ID: Int = 0
        var name: String? = null
        var damage: Int = 0
        var MPcost: Int = 0
        var damageType: String? = null
        var element: String? = null
        var statusEffect: String? = null
        var canChangeStatusEffect: Boolean = false
        var probabilityToChangeStatusEffect: Int = 0
        var areaDamage: Boolean = false

        var modifiedStats: JsonAbilityModifiedStats? = null
        var healedStats: JsonAbilityHealedStats? = null

        private class JsonAbilityModifiedStats {
            var PStr: Int = 0
            var PDef: Int = 0
            var MStr: Int = 0
            var MDef: Int = 0
            var Speed: Int = 0
            fun changesStats(): Boolean {
                return PStr != 0 || PDef != 0 || MStr != 0 || MDef != 0 || Speed != 0
            }
        }

        private class JsonAbilityHealedStats {
            var HP: Int = 0
            var MP: Int = 0
            fun curesStats(): Boolean {
                return HP != 0 || MP != 0
            }
        }

        override fun toString(): String {
            var out = "Ability:\n"
            out += "$ID $name\nDamage: $damage"
            out += "  MPcost: $MPcost"
            return out
        }
    }

    companion object {
        private var instance: AbilityService? = null
        private var abilities: ArrayMap<Element, ArrayMap<Int, Ability>>

        private fun readAbilitiesFromJsonString(jsonString: String): ArrayMap<Int, Ability> {
            val elAbilities = ArrayMap<Int, Ability>()
            val json = Json()

            val elementList: ArrayList<JsonValue>

            if (json.fromJson(ArrayList<*>::class.java, jsonString) != null)
                elementList = json.fromJson<ArrayList<*>>(ArrayList<*>::class.java, jsonString)
            else
                elementList = ArrayList()

            var jsa: JsonAbility
            var ability: Ability
            for (v in elementList) {
                jsa = json.readValue(JsonAbility::class.java, v)
                ability = Ability(
                        jsa.ID,
                        Ability.DamageType.valueOf(jsa.damageType!!.toUpperCase()),
                        Element.valueOf(jsa.element!!.toUpperCase()),
                        jsa.damage,
                        jsa.name!!,
                        jsa.MPcost,
                        jsa.areaDamage,
                        jsa.canChangeStatusEffect,
                        IndividualStatistics.StatusEffect.valueOf(jsa.statusEffect!!.toUpperCase()),
                        jsa.probabilityToChangeStatusEffect,
                        jsa.modifiedStats!!.changesStats(),
                        jsa.modifiedStats!!.PStr,
                        jsa.modifiedStats!!.PDef,
                        jsa.modifiedStats!!.MStr,
                        jsa.modifiedStats!!.MDef,
                        jsa.modifiedStats!!.Speed,
                        jsa.healedStats!!.curesStats(),
                        jsa.healedStats!!.HP,
                        jsa.healedStats!!.MP
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
        fun getInstance(jsonAbilitiesResources: ArrayMap<Element, String>): AbilityService {
            if (instance == null) {
                instance = AbilityService(jsonAbilitiesResources)
            }
            return instance
        }

        /**
         * Best practice: Use only once, when providing [IAbilityService] to
         * [GuardiansServiceLocator], afterwards always retrieve it from there.
         *
         * @param jsonFilePaths Files that contain Abilities in Json format
         * @return
         */
        @Synchronized
        fun getInstanceFromFile(jsonFilePaths: ArrayMap<Element, String>): AbilityService {
            val jsonResources = ArrayMap<Element, String>()
            for (key in jsonFilePaths.keys()) {
                val handleJson = Gdx.files.internal(jsonFilePaths.get(key))
                val jsonString = handleJson.readString()
                jsonResources.put(key, jsonString)
            }

            return getInstance(jsonResources)
        }
    }
}