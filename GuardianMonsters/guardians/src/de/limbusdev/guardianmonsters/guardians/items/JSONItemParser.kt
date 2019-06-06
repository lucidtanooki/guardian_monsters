package de.limbusdev.guardianmonsters.guardians.items

import com.badlogic.gdx.utils.JsonValue

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem

/**
 * JSONItemParser
 *
 * @author Georg Eckert 2019
 */

object JSONItemParser
{
    /** Takes a JsonValue and parses it to the described item. */
    fun parseJsonItem(jsonItem: JsonValue): Item
    {
        var item: Item

        try
        {
            item = when (jsonItem.getString("category"))
            {
                "medicine"      -> parseMedicine(jsonItem)
                "equipment"     -> parseEquipmentItem(jsonItem)
                "key"           -> parseKeyItem(jsonItem)
                "chakracrystal" -> parseChakraCrystal(jsonItem)
                else            -> MedicalItem("Water", 0, AMedicalItem.Type.HP_CURE)
            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            item = MedicalItem("Water", 0, AMedicalItem.Type.HP_CURE)
        }

        return item
    }

    /** Takes a JsonValue and parses it to the described medical item. */
    private fun parseMedicine(jsonItem: JsonValue): Item
    {
        return when (jsonItem.getString("type"))
        {
            "HPcure" -> parseHPCuringItem(jsonItem)
            "MPcure" -> parseMPCuringItem(jsonItem)
            "revive" -> parseRevivingItem(jsonItem)
            else     -> MedicalItem("Water", 0, AMedicalItem.Type.HP_CURE)
        }
    }

    /** Takes a JsonValue and parses it to the described chakra crystal item. */
    private fun parseChakraCrystal(jsonItem: JsonValue): Item
    {
        return ChakraCrystalItem(

                jsonItem.getString("nameID"),
                jsonItem.getString("element")
        )
    }

    /** Takes a JsonValue and parses it to an HP curing medical item. */
    private fun parseHPCuringItem(jsonItem: JsonValue): Item
    {
        return MedicalItem(

                jsonItem.getString("nameID"),
                jsonItem.getInt("value"),
                AMedicalItem.Type.HP_CURE
        )
    }

    /** Takes a JsonValue and parses it to an MP curing medical item. */
    private fun parseMPCuringItem(jsonItem: JsonValue): Item
    {
        return MedicalItem(

                jsonItem.getString("nameID"),
                jsonItem.getInt("value"),
                AMedicalItem.Type.MP_CURE
        )
    }

    /** Takes a JsonValue and parses it to an rviving medical item. */
    private fun parseRevivingItem(jsonItem: JsonValue): Item
    {
        return MedicalItem(

                jsonItem.getString("nameID"),
                jsonItem.getInt("value"),
                AMedicalItem.Type.REVIVE
        )
    }

    /** Takes a JsonValue and parses it to a key item. */
    private fun parseKeyItem(jsonItem: JsonValue): Item
    {
        return KeyItem(jsonItem.getString("nameID"))
    }

    /** Takes a JsonValue and parses it to an equipment item. */
    private fun parseEquipmentItem(jsonItem: JsonValue): Item
    {
        val bodyPart = BodyPart.valueOf(jsonItem.getString("body-part", "hands").toUpperCase())

        val nameID  = jsonItem.getString("nameID", "claws-wood")
        val pStr    = jsonItem.getInt("addsPStr", 0)
        val pDef    = jsonItem.getInt("addsPDef", 0)
        val mStr    = jsonItem.getInt("addsMStr", 0)
        val mDef    = jsonItem.getInt("addsMDef", 0)
        val speed   = jsonItem.getInt("addsSpeed", 0)
        val hp      = jsonItem.getInt("addsHP", 0)
        val mp      = jsonItem.getInt("addsMP", 0)
        val exp     = jsonItem.getInt("addsEXP", 0)
        val type    = jsonItem.getString("type").toUpperCase()

        return when (bodyPart)
        {
            BodyPart.HEAD -> HeadEquipment(nameID, HeadEquipment.Type.valueOf(type),
                                           pStr, pDef, mStr, mDef, speed, hp, mp, exp)
            BodyPart.BODY -> BodyEquipment(nameID, BodyEquipment.Type.valueOf(type),
                                           pStr, pDef, mStr, mDef, speed, hp, mp, exp)
            BodyPart.FEET -> FootEquipment(nameID, FootEquipment.Type.valueOf(type),
                                           pStr, pDef, mStr, mDef, speed, hp, mp, exp)
            else /*HAND*/ -> HandEquipment(nameID, HandEquipment.Type.valueOf(type),
                                           pStr, pDef, mStr, mDef, speed, hp, mp, exp)
        }
    }
}
