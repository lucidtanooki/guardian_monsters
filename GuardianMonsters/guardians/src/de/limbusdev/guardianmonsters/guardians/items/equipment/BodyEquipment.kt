package de.limbusdev.guardianmonsters.guardians.items.equipment


import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * BodyEquipment
 *
 * @author Georg Eckert 2017
 */

class BodyEquipment(

        name: String,
        val type: Type,
        PStr: Int,
        PDef: Int,
        MStr: Int,
        MDef: Int,
        Speed: Int,
        HP: Int,
        MP: Int,
        Exp: Int

) : Equipment(name, BodyPart.BODY, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp)
{
    override fun equipable(m: AGuardian): Boolean
    {
        return if(super.equipable(m))
        {
            m.speciesDescription.bodyType == type
        }
        else
        {
            false
        }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Inner Classes
    enum class Type
    {
        ARMOR, BARDING, SHIELD, BREASTPLATE
    }
}