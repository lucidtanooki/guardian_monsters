package de.limbusdev.guardianmonsters.guardians.items

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment

/**
 * IItemService
 *
 * @author Georg Eckert 2019
 */

interface IItemService : GuardiansServiceLocator.Service
{
    fun getItem(name: String): Item
    fun getEquipment(name: String): Equipment
}
