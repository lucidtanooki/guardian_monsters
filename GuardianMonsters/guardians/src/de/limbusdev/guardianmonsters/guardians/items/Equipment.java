package de.limbusdev.guardianmonsters.guardians.items;


import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * Equipment extends the {@link Stat}s of a monster in the following way:
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
    @Override
    public void apply(Guardian m)
    {

    }

    @Override
    public boolean applicable(Guardian guardian)
    {
        return (guardian.abilityGraph.hasLearntEquipment(this.bodyPart));
    }

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
}
