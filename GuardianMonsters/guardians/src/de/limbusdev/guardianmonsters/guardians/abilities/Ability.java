package de.limbusdev.guardianmonsters.guardians.abilities;


import de.limbusdev.guardianmonsters.guardians.Element;

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
    public static class aID {

        public final int ID;
        public final Element element;

        public aID(int ID, Element element)
        {
            this.ID = ID;
            this.element = element;
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


    /* ........................................................................... CONSTRUCTOR .. */
    /**
     * For Serialization only!
     */
    public Ability()
    {}

    /**
     * For physcial Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     */
    public Ability(int ID, DamageType damageType, Element element, int damage, String name) {
        this(ID, damageType, element, damage, name, 0);
    }


    /**
     * For magical Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param MPcost
     */
    public Ability(int ID, DamageType damageType, Element element, int damage, String name, int MPcost) {
        this.ID = ID;
        this.element = element;
        this.damageType = damageType;
        this.damage = damage;
        this.name = name;
        this.MPcost = MPcost;
    }

    @Override
    public boolean equals(Object other) {
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
