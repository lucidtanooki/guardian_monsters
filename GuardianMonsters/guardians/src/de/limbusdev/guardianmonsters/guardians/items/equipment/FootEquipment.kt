package de.limbusdev.guardianmonsters.guardians.items.equipment

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * FootEquipment
 *
 * @author Georg Eckert 2017
 */

class FootEquipment
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
    : Equipment(name, BodyPart.FEET, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp)
{
    enum class Type
    {
        SHOES, SHINPROTECTION, HORSESHOE, KNEEPADS
    }

    override fun equipable(m: AGuardian): Boolean
    {
        return if(super.equipable(m))
        {
            m.speciesDescription.footType == type
        }
        else
        {
            false
        }
    }
}