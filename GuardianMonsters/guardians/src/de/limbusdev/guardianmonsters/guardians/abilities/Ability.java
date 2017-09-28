package de.limbusdev.guardianmonsters.guardians.abilities;


import de.limbusdev.guardianmonsters.guardians.Element;

/**
 * @author Georg Eckert 2017
 */
public class Ability
{

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

    public String sfxType;
    public int sfxIndex;
    public String animationType;


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
     * @param sfxType
     * @param sfxIndex
     * @param animType
     */
    public Ability(int ID, DamageType damageType, Element element, int damage, String name,
                   String sfxType, int sfxIndex, String animType) {
        this(ID, damageType, element, damage, name, sfxType, sfxIndex, animType, 0);
    }


    /**
     * For magical Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param sfxType
     * @param sfxIndex
     * @param animType
     * @param MPcost
     */
    public Ability(int ID, DamageType damageType, Element element, int damage, String name,
                   String sfxType, int sfxIndex, String animType, int MPcost) {
        this.ID = ID;
        this.element = element;
        this.damageType = damageType;
        this.damage = damage;
        this.name = name;
        this.sfxIndex = sfxIndex;
        this.sfxType = sfxType;
        this.animationType = animType;
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
