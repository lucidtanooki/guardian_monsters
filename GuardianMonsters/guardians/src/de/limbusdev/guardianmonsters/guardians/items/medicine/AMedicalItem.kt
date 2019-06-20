package de.limbusdev.guardianmonsters.guardians.items.medicine


import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

/**
 * AMedicalItem
 *
 * @author Georg Eckert 2017
 */

abstract class AMedicalItem(name: String) : Item(name, Category.MEDICINE)
{
    // ............................................................................... Inner Classes
    enum class Type { REVIVE, HP_CURE, MP_CURE, STATUS_CURE }

    abstract fun apply(guardian: AGuardian)

    abstract fun applicable(guardian: AGuardian): Boolean

    abstract val type: Type
}
