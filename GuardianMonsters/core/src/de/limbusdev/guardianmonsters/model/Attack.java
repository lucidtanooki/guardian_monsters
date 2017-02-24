package de.limbusdev.guardianmonsters.model;


import de.limbusdev.guardianmonsters.enums.AnimationType;
import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.enums.SFXType;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 06.12.15.
 */
public class Attack {
    /* ............................................................................ ATTRIBUTES .. */
    public final int ID;
    public AttackType attackType;
    public Element element;
    public int damage;
    public String name;
    public SFXType sfxType;
    public int sfxIndex;
    public AnimationType animationType;
    public int MPcost;
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * For attacks that cost no MP
     * @param attackType
     * @param element
     * @param damage
     * @param name
     * @param sfxType
     * @param sfxIndex
     * @param animType
     */
    public Attack(int ID, AttackType attackType, Element element, int damage, String name, SFXType sfxType,
                  int sfxIndex, AnimationType animType) {
        this(ID,attackType,element,damage,name,sfxType,sfxIndex,animType,0);
    }


    /**
     * For attacks that cost MP
     * @param attackType
     * @param element
     * @param damage
     * @param name
     * @param sfxType
     * @param sfxIndex
     * @param animType
     * @param MPcost
     */
    public Attack(int ID, AttackType attackType, Element element, int damage, String name, SFXType sfxType,
                  int sfxIndex, AnimationType animType, int MPcost) {
        this.ID = ID;
        this.element = element;
        this.attackType = attackType;
        this.damage = damage;
        this.name = name;
        this.sfxIndex = sfxIndex;
        this.sfxType = sfxType;
        this.animationType = animType;
        this.MPcost = MPcost;
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
