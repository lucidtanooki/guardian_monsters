package de.limbusdev.guardianmonsters.guardians.items;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;

/**
 * IItemService
 *
 * @author Georg Eckert 2017
 */

public interface IItemService extends GuardiansServiceLocator.Service
{
    Item getItem(String name);
    Equipment getEquipment(String name);
}
