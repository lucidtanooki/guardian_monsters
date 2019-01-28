package de.limbusdev.guardianmonsters.guardians.items.equipment

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * HeadEquipment
 *
 * @author Georg Eckert 2017
 */

class HeadEquipment
(
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
)
    : Equipment(name, BodyPart.HEAD, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp)
{
    enum class Type
    {
        HELMET, BRIDLE, MASK, HEADBAND
    }

    override fun equipable(m: AGuardian): Boolean
    {
        return if(super.equipable(m))
        {
            m.speciesDescription.headType == type
        }
        else
        {
            false
        }
    }
}