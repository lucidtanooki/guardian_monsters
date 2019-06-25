package de.limbusdev.guardianmonsters.guardians.items.equipment


import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * HandEquipment
 *
 * @author Georg Eckert 2017
 */
class HandEquipment
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
    : Equipment(name, BodyPart.HANDS, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp)
{
    enum class Type
    {
        SWORD, PATA, BRACELET, CLAWS
    }

    override fun canBeEquipped(guardian: AGuardian): Boolean
    {
        return super.canBeEquipped(guardian) && guardian.speciesDescription.handType == type
    }
}