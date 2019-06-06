package de.limbusdev.guardianmonsters.guardians.items

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.utils.extensions.f

class ChakraCrystalItem(name: String, element: String) : Item(name, Category.CHAKRACRYSTAL)
{
    // .................................................................................. Properties
    private var element: Element = Element.NONE

    // ................................................................................ Constructors
    init
    {
        try
        {
            this.element = Element.valueOf(element.toUpperCase())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            this.element = Element.NONE
        }
    }

    // ..................................................................................... Methods
    /**
     * calculates the chance to ban a guardian with this item
     * @param guardian
     * @return
     */
    fun chance(guardian: AGuardian): Float
    {
        var chance = 0f
        val elementFactor: Float = if (guardian.isOfElement(element)) { 2f }
                                   else                               { 1f }

        chance =  1f - guardian.stats.hpFraction
        chance /= elementFactor / 100f / 2f
        chance -= guardian.stats.level / 100f

        if (chance > 1f) { chance = 1f }

        return chance
    }
}
