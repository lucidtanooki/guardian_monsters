package de.limbusdev.guardianmonsters.model.abilities;


import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AnimationType;
import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.media.SFXType;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * @author Georg Eckert 2017
 */
public class Ability {

    public enum DamageType {
        MAGICAL, PHYSICAL
    }

    /* ............................................................................ ATTRIBUTES .. */
    public int ID;
    public String name;

    public int damage;
    public int MPcost;
    public DamageType damageType;
    public Element element;

    public SFXType sfxType;
    public int sfxIndex;
    public AnimationType animationType;


    /* ........................................................................... CONSTRUCTOR .. */
    /**
     * For Serialization only!
     */
    public Ability() {
    }

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
                   SFXType sfxType, int sfxIndex, AnimationType animType) {
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
                   SFXType sfxType, int sfxIndex, AnimationType animType, int MPcost) {
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

    public String getLocalName() {
        return Services.getL18N().Abilities().get(name);
    }



}
