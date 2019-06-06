package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue

import java.util.Locale

import de.limbusdev.guardianmonsters.guardians.Constant
import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.abilities.Node
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment
import de.limbusdev.utils.extensions.set
import kotlin.String.Companion

/**
 * JSONGuardianParser
 *
 * Takes the JSON representation of a Guardians species description and parses it to create a
 * [SpeciesDescription] object.
 *
 * Every Guardian can have several [SpeciesDescription.MetaForm]s. Every MetaForm of a
 * Guardian is contained in it's SpeciesDescription. When metamorphing, a Guardian doesn't
 * transform to another species. It only reaches a new MetaForm, so the SpeciesID stays the
 *
 * Required JSON format:
 *
 * {
 *     "id": <ID of guardian>,
 *     "metamorphosisNodes": [
 *          <position of node 1>,
 *          <position of node 2>,
 *          ...
 *     ],
 *     "abilities": [
 *          {
 *              "abilityID":  <ID of ability>,
 *              "element":    <name of ability element>,
 *              "abilityPos": <position in Ability Graph>
 *          },
 *          {<ability 2>},
 *          {<ability 3>},
 *          ...
 *     ],
 *     "basestats": {
 *          "hp":    <value>,
 *          "mp":    <value>,
 *          "speed": <value>,
 *          "pstr":  <value>,
 *          "pdef":  <value>,
 *          "mstr":  <value>,
 *          "mdef":  <value>
 *     },
 *     "equipment-compatibility": {
 *          "head":  <head equipment type>,
 *          "hands": <hands equipment type>,
 *          "body":  <body equipment type>,
 *          "feet":  <feet equipment type>
 *     },
 *     "ability-graph-equip": {
 *          "head":  <node position>,
 *          "hands": <node position>,
 *          "body":  <node position>,
 *          "feet":  <node position>
 *     }
 *     "metaForms": [
 *          {
 *              "form":     <meta level>,
 *              "nameID":   <nameID of this meta level>,
 *              "elements": [<element 1>, <element 2>, ...]
 *          },
 *          {<meta form 2>},
 *          {<meta form 3>},
 *          ...
 *     ]
 * }
 *
 * @author Georg Eckert 2019
 */
object JSONGuardianParser
{
    /**
     * Helper class for easier JSON parsing
     */
    private class JSONGuardianSpeciesDescription
    {
        internal var id                     : Int = 0
        internal var metamorphosisNodes     : IntArray? = null
        internal var abilities              : Array<JSONGuardianAbility>? = null
        internal var basestats              : JSONGuardianBaseStats? = null
        internal var equipmentCompatibility : JSONGuardianEquipmentCompatibility? = null
        internal var abilityGraphEquip      : JSONGuardianGraphEquip? = null
        internal var metaForms              : Array<JSONGuardianMetaForm>? = null

        internal class JSONGuardianAbility
        {
            var abilityID   : Int = 0
            var element     : String? = null
            var abilityPos  : Int = 0
        }

        internal class JSONGuardianBaseStats
        {
            var hp      : Int = 0
            var mp      : Int = 0
            var pstr    : Int = 0
            var pdef    : Int = 0
            var mstr    : Int = 0
            var mdef    : Int = 0
            var speed   : Int = 0
        }

        internal class JSONGuardianEquipmentCompatibility
        {
            var head  : String? = null
            var hands : String? = null
            var body  : String? = null
            var feet  : String? = null
        }

        internal class JSONGuardianGraphEquip
        {
            var head  : Int = 0
            var hands : Int = 0
            var body  : Int = 0
            var feet  : Int = 0
        }

        internal class JSONGuardianMetaForm
        {
            var form     : Int = 0
            var nameID   : String? = null
            var elements : Array<String>? = null
        }

        override fun toString(): String
        {
            return """
                --------------------\n
                Species ID: $id\n
                Meta forms: ${metamorphosisNodes!!.size}\n
                ${abilities!!.size} abilities\n
                Stats: HP: ${basestats!!.hp} MP: ${basestats!!.mp}\n
                Equipment:\n
                \tHands: ${equipmentCompatibility!!.hands} (Node: ${abilityGraphEquip!!.hands})\n
                --------------------""".trimIndent()
        }
    }

    fun parseGuardianList(jsonString: String): JsonValue
    {
        val json = Json()
        val rootElement = JsonReader().parse(jsonString)
        return rootElement.get("guardians")
    }

    fun parseGuardian(element: JsonValue): SpeciesDescription
    {
        val json = Json()
        val speciesDescription: SpeciesDescription
        val spec = json.fromJson(JSONGuardianSpeciesDescription::class.java, element.toString())

        // ......................................................................................... name & id
        val speciesID = spec.id

        // ......................................................................................... metamorphosis
        val metamorphosisNodes = parseMetamorphosisNodes(spec)

        // ......................................................................................... abilities
        val attacks = parseAbilities(spec)

        // ......................................................................................... equipment
        val equipmentGraph = parseEquipmentGraph(spec)

        // ......................................................................................... stats

        val stat = parseBaseStats(spec)

        val head = HeadEquipment.Type.valueOf(spec.equipmentCompatibility!!.head!!.toUpperCase())
        val body = BodyEquipment.Type.valueOf(spec.equipmentCompatibility!!.body!!.toUpperCase())
        val hand = HandEquipment.Type.valueOf(spec.equipmentCompatibility!!.hands!!.toUpperCase())
        val feet = FootEquipment.Type.valueOf(spec.equipmentCompatibility!!.feet!!.toUpperCase())

        // ......................................................................................... meta forms
        val metaForms = ArrayMap<Int, SpeciesDescription.MetaForm>()

        for (form in spec.metaForms!!)
        {
            val elements = Array<Element>()
            form.elements!!.forEach { jsonElement -> elements.add(Element.valueOf(jsonElement.toUpperCase()))}
            val metaForm = SpeciesDescription.MetaForm(form.form, form.nameID!!, elements)
            metaForms[form.form] = metaForm
        }


        // ......................................................................................... construction
        speciesDescription = SpeciesDescription(

                speciesID,
                stat,
                attacks,
                equipmentGraph,
                metamorphosisNodes,
                head, body, hand, feet,
                metaForms
        )

        if (Constant.PRINT_PARSED_GUARDIAN)
        {
            println("Parsed JSON Guardian Data:\n")
            println(speciesDescription.prettyPrint())
        }

        return speciesDescription
    }


    // ............................................................................................. JSON Element Parsers
    /**
     * Parses the the [Node]s which allow a
     * guardian to metamorph.
     */
    private fun parseMetamorphosisNodes(spec: JSONGuardianSpeciesDescription): Array<Int>
    {
        val metamorphosisNodes = Array<Int>()
        spec.metamorphosisNodes!!.forEach { node -> metamorphosisNodes.add(node) }
        return metamorphosisNodes
    }

    /**
     * Parses the [Ability]s of a Guardian.
     */
    private fun parseAbilities(spec: JSONGuardianSpeciesDescription): ArrayMap<Int, Ability.aID>
    {
        val abilities = ArrayMap<Int, Ability.aID>()

        for (jsonAbility in spec.abilities!!)
        {
            val element = Element.valueOf(jsonAbility.element!!.toUpperCase())
            abilities.put(jsonAbility.abilityPos, Ability.aID(jsonAbility.abilityID, element))
        }

        return abilities
    }

    /**
     * Parses the [Equipment] nodes of
     * a Guardian.
     */
    private fun parseEquipmentGraph(spec: JSONGuardianSpeciesDescription): ArrayMap<Int, BodyPart>
    {
        val equipmentGraph = ArrayMap<Int, BodyPart>()

        equipmentGraph.put(spec.abilityGraphEquip!!.body, BodyPart.BODY)
        equipmentGraph.put(spec.abilityGraphEquip!!.hands, BodyPart.HANDS)
        equipmentGraph.put(spec.abilityGraphEquip!!.head, BodyPart.HEAD)
        equipmentGraph.put(spec.abilityGraphEquip!!.feet, BodyPart.FEET)

        return equipmentGraph
    }

    /**
     * Parses the [CommonStatistics] of a Guardian.
     */
    private fun parseBaseStats(spec: JSONGuardianSpeciesDescription): CommonStatistics
    {
        return CommonStatistics(

                spec.basestats!!.hp,
                spec.basestats!!.mp,
                spec.basestats!!.pstr,
                spec.basestats!!.pdef,
                spec.basestats!!.mstr,
                spec.basestats!!.mdef,
                spec.basestats!!.speed
        )
    }
}
