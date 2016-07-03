package org.limbusdev.monsterworld.model;

import org.limbusdev.monsterworld.enums.AttackType;
import org.limbusdev.monsterworld.enums.SFXType;
import org.limbusdev.monsterworld.utils.GlobPref;

/**
 * Created by georg on 06.12.15.
 */
public class Attack {
    /* ............................................................................ ATTRIBUTES .. */
    public AttackType attackType;
    public int damage;
    public String name;
    public SFXType sfxType;
    public int sfxIndex;
    /* ........................................................................... CONSTRUCTOR .. */

    public Attack(AttackType attackType, int damage, String name, SFXType sfxType, int sfxIndex) {
        this.attackType = attackType;
        this.damage = damage;
        if(GlobPref.DEBUGGING_ON) this.damage = 10; // TODO
        this.name = name;
        this.sfxIndex = sfxIndex;
        this.sfxType = sfxType;
    }

    public Attack() {
        // FOR JSON ONLY
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
