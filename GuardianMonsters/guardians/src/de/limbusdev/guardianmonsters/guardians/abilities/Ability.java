package de.limbusdev.guardianmonsters.guardians.abilities;


import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;

/**
 * @author Georg Eckert 2017
 */
public class Ability
{
    /**
     * An aID provides a unique key for an ability. This way there is one central point of ability
     * management and Guardians only have to keep an aID instance to fetch the ability instance from
     * there.
     */
    public static class aID
    {
        public final int ID;
        public final Element element;

        public aID(int ID, Element element)
        {
            this.ID = ID;
            this.element = element;
        }

        @Override
        public boolean equals(Object o)
        {
            if(!(o instanceof aID)) return false;
            aID other = (aID) o;
            if(this.ID == other.ID && this.element == other.element) return true;
            else return false;
        }
    }

    public enum DamageType
    {
        MAGICAL, PHYSICAL
    }

    /* ............................................................................ ATTRIBUTES .. */
    public int ID;
    public String name;

    public int damage;
    public int MPcost;
    public DamageType damageType;
    public Element element;
    public boolean areaDamage;
    public boolean canChangeStatusEffect;
    public IndividualStatistics.StatusEffect statusEffect;
    public int probabilityToChangeStatusEffect;

    /* ........................................................................... CONSTRUCTOR .. */
    /**
     * For Serialization only!
     */
    public Ability()
    {}

    public Ability(int ID, DamageType damageType, Element element, int damage, String name)
    {
        this(ID, damageType, element, damage, name, 0, false, false, IndividualStatistics.StatusEffect.HEALTHY, 0);
    }

    public Ability(int ID, DamageType damageType, Element element, int damage, String name, int MPcost)
    {
        this(ID, damageType, element, damage, name, MPcost, false, false, IndividualStatistics.StatusEffect.HEALTHY, 0);
    }


    /**
     * For physcial Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param areaDamage
     * @param canChangeStatusEffect
     * @param statusEffect
     * @param probabilityToChangeStatusEffect
     */
    public Ability(
        int ID, DamageType damageType, Element element, int damage, String name, boolean areaDamage,
        boolean canChangeStatusEffect, IndividualStatistics.StatusEffect statusEffect, int probabilityToChangeStatusEffect)
    {
        this(ID, damageType, element, damage, name, 0, areaDamage, canChangeStatusEffect, statusEffect, probabilityToChangeStatusEffect);
    }


    /**
     * For magical Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param MPcost
     * @param areaDamage
     * @param canChangeStatusEffect
     * @param statusEffect
     * @param probabilityToChangeStatusEffect
     */
    public Ability(int ID, DamageType damageType, Element element, int damage, String name, int MPcost, boolean areaDamage,
                   boolean canChangeStatusEffect, IndividualStatistics.StatusEffect statusEffect, int probabilityToChangeStatusEffect)
    {
        this.ID = ID;
        this.element = element;
        this.damageType = damageType;
        this.damage = damage;
        this.name = name;
        this.MPcost = MPcost;
        this.areaDamage = areaDamage;
        this.canChangeStatusEffect = canChangeStatusEffect;
        this.statusEffect = statusEffect;
        this.probabilityToChangeStatusEffect = probabilityToChangeStatusEffect;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Ability) {
            Ability otherAbility = (Ability) other;
            if (otherAbility.ID == this.ID && otherAbility.element == this.element) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return "Ability " + ID + " of Element " + element + ": " + name;
    }
}
