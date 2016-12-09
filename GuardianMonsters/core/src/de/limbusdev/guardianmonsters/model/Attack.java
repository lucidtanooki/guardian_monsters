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
    public AttackType attackType;
    public Element element;
    public int damage;
    public String name;
    public SFXType sfxType;
    public int sfxIndex;
    public AnimationType animationType;
    /* ........................................................................... CONSTRUCTOR .. */

    public Attack(AttackType attackType, Element element, int damage, String name, SFXType sfxType,
                  int sfxIndex, AnimationType animType) {
        this.element = element;
        this.attackType = attackType;
        this.damage = damage;
        this.name = name;
        this.sfxIndex = sfxIndex;
        this.sfxType = sfxType;
        this.animationType = animType;
    }

    public Attack() {
        // FOR JSON ONLY
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
