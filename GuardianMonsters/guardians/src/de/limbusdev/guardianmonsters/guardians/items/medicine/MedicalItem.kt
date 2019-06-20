package de.limbusdev.guardianmonsters.guardians.items.medicine

import com.badlogic.gdx.math.MathUtils

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * Medicine
 *
 * @author Georg Eckert 2017
 */

/**
 * Implementation of @link{AMedicalItem}, do not use this outside of this module, always use
 * AMedicalItem.
 * @param name
 * @param value
 * @param type
 */
class MedicalItem
(
        name                : String = "bread",
        val value           : Int = 100,
        override val type   : Type = Type.HP_CURE
)
    : AMedicalItem(name)
{
    override fun apply(guardian: AGuardian)
    {
        when (type)
        {
            Type.REVIVE  -> guardian.stats.healHP(MathUtils.round(guardian.stats.hpMax * value / 100f))
            Type.HP_CURE -> guardian.stats.healHP(value)
            Type.MP_CURE -> guardian.stats.healMP(value)
            else -> {}
        }
    }

    override fun applicable(guardian: AGuardian): Boolean
    {
        return when (type)
        {
            Type.REVIVE  -> guardian.stats.isKO
            Type.HP_CURE -> guardian.stats.isFit && guardian.stats.hp < guardian.stats.hpMax
            Type.MP_CURE -> guardian.stats.mp < guardian.stats.mPmax
            else         -> false
        }
    }
}