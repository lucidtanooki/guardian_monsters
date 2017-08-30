package de.limbusdev.guardianmonsters.guardians.items.equipment;


import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * Equipment extends the Stats of a monster in the following way:
 *
 * HP   .. by factor (1 + addsHP/100)
 * MP   .. by factor (1 + addsMP/100)
 * PStr .. by adding addsPStr
 * PDef .. by adding addsPDef
 * MStr .. by adding addsMStr
 * MDef .. by adding addsMDef
 * Speed.. by adding addsSpeed
 * EXP  .. by factor (1 + addsEXP/100)
 *
 * @author Georg Eckert
 */

public abstract class Equipment extends Item
{
    public final int addsPStr, addsPDef, addsMStr, addsMDef, addsSpeed, addsHP, addsMP, addsEXP;
    public final BodyPart bodyPart;

    public Equipment(String name, BodyPart bodyPart, int addsPStr, int addsPDef, int addsMStr,
                     int addsMDef, int addsSpeed, int addsHP, int addsMP, int addsExp)
    {
        super(name, Category.EQUIPMENT);
        this.addsPStr = addsPStr;
        this.addsPDef = addsPDef;
        this.addsMStr = addsMStr;
        this.addsMDef = addsMDef;
        this.addsSpeed = addsSpeed;
        this.addsHP = addsHP;
        this.addsMP = addsMP;
        this.bodyPart = bodyPart;
        this.addsEXP = addsExp;
    }

    /**
     * Equips Guardian with this Equipment and returns the previously equipped item.
     * @param m Guardian to give equipment to
     * @return  previously worn equipment, null if nothing was worn yet
     */
    public Equipment equip(AGuardian m)
    {
        return m.getIndividualStatistics().giveEquipment(this);
    }

    /**
     * Checks if the given monster has already learnt the ability for being able to wear equipment
     * at this body part.
     * @param guardian
     * @return  if the given monster has learnt the needed ability yet
     */
    public boolean equipable(AGuardian guardian)
    {
        return (guardian.getAbilityGraph().hasLearntEquipment(this.bodyPart));
    }
}
