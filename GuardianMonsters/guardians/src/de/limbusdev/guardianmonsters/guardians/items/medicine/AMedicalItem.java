package de.limbusdev.guardianmonsters.guardians.items.medicine;


import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * AMedicalItem
 *
 * @author Georg Eckert 2017
 */

public abstract class AMedicalItem extends Item
{
    public AMedicalItem(String name)
    {
        super(name, Category.MEDICINE);
    }

    public abstract void apply(AGuardian guardian);

    public abstract boolean applicable(AGuardian guardian);
}
