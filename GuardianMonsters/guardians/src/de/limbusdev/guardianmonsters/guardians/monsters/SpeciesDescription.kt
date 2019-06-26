package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.items.equipment.*


/**
 * Species Description
 *
 * Holds a formal description of the given ID. This data follows the idea of a biological
 * classification, following the classical evolutionary classification systematic.
 *
 * Life > Domain > Kingdom > Phylum > Class > Order > Family > Genus > Species
 *
 * Guardians however are divided by only two hierarchical levels: Phylum > Species
 * Where this can be simplified to Element > Species
 *
 * The species can be identified exactly by the unique SpeciesID or simply ID.
 *
 * Instances of this class hold all attributes which are common for all Guardians of the same species.
 *
 * @author Georg Eckert 2016
 */
class SpeciesDescription
(
        val ID: Int = 0,
        val commonStatistics: CommonStatistics = CommonStatistics(),
        val abilityNodes: ArrayMap<Int, Ability.aID> = ArrayMap(),
        val equipmentNodes: ArrayMap<Int, BodyPart> = ArrayMap(),
        val metamorphosisNodes: Array<Int> = Array(),
        val headType: HeadEquipment.Type = HeadEquipment.Type.BRIDLE,
        val bodyType: BodyEquipment.Type = BodyEquipment.Type.ARMOR,
        val handType: HandEquipment.Type = HandEquipment.Type.BRACELET,
        val footType: FootEquipment.Type = FootEquipment.Type.HORSESHOE,
        val metaForms: ArrayMap<Int, MetaForm> = ArrayMap()
) {
    class MetaForm
    (
            internal val form: Int,
            internal val nameID: String,
            internal val elements: Array<Element>
    ){
        override fun equals(other: Any?): Boolean
        {
            if(this === other) return true
            if(javaClass != other?.javaClass) return false

            other as MetaForm

            if(form != other.form) return false
            if(nameID != other.nameID) return false
            if(elements != other.elements) return false

            return true
        }

        override fun hashCode(): Int
        {
            var result = form
            result = 31 * result + nameID.hashCode()
            result = 31 * result + elements.hashCode()
            return result
        }
    }

    fun getNameID(currentForm: Int) : String
    {
        return metaForms.get(currentForm).nameID
    }

    fun getSimpleName(currentForm: Int) = getNameID(currentForm).split("_")[2]

    fun getElements(currentForm: Int) = metaForms.get(currentForm).elements


    override fun toString() = "Species: $ID"

    fun prettyPrint(): String
    {
        var pretty = """
            +---- Guardian Species Description ----+
            | Species: $ID
            | Abilitiy-Nodes:

        """.trimIndent()

        for(key in abilityNodes.keys)
            pretty += "|\tNode $key:\t${abilityNodes.get(key).ID}_${abilityNodes.get(key).element}\n"

        pretty += "| Equipment-Nodes: \n"

        for(key in equipmentNodes.keys())
            pretty += "|\tNode $key:\t${equipmentNodes.get(key).toString()}\n"

        pretty += """
            | Equipment-Type:
            |   Body:	$bodyType
            |   Hands:	$handType
            |	Feet:	$footType
            |	Head:	$headType
            | Can metamorph: " + ${if(metaForms.size > 1) "no" else "yes"}
            | Metamorphs ${metaForms.size - 1} times
            +--------------------------------------+

        """.trimIndent()

        return pretty
    }
}
