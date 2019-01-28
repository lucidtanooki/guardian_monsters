package de.limbusdev.guardianmonsters.guardians.items.equipment


import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * Equipment extends the Stats of a monster in the following way:
 *
 * HP   .. by factor (1 + addsHP/100)
 * MP   .. by factor (1 + addsMP/100)
 * PStr .. by adding addsPStr
 * PDef .. by adding addsPDef
 * MStr .. by adding addsMStr
 * MDef .. by adding addsMDef
 * Speed.. by adding addsSpeed
 * EXP  .. by factor (1 + addsEXP/100)
 *
 * @author Georg Eckert
 */

abstract class Equipment
(
        name: String,
        val bodyPart: BodyPart,
        val addsPStr: Int,
        val addsPDef: Int,
        val addsMStr: Int,
        val addsMDef: Int,
        val addsSpeed: Int,
        val addsHP: Int,
        val addsMP: Int,
        val addsEXP: Int
)
    : Item(name, Item.Category.EQUIPMENT)
{
    /**
     * Equips Guardian with this Equipment and returns the previously equipped item.
     * @param m Guardian to give equipment to
     * @return  previously worn equipment, null if nothing was worn yet
     */
    fun equip(m: AGuardian): Equipment?
    {
        return m.individualStatistics.giveEquipment(this)
    }

    /**
     * Checks if the given monster has already learnt the ability for being able to wear equipment
     * at this body part.
     * @param guardian
     * @return  if the given monster has learnt the needed ability yet
     */
    open fun equipable(guardian: AGuardian): Boolean
    {
        return guardian.abilityGraph.hasLearntEquipment(this.bodyPart)
    }
}
