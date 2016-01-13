package org.limbusdev.monsterworld.model;

import org.limbusdev.monsterworld.enums.AttackType;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 06.12.15.
 */
public class Attack {
    /* ............................................................................ ATTRIBUTES .. */
    public AttackType attackType;
    public int damage;
    public String name;
    /* ........................................................................... CONSTRUCTOR .. */

    public Attack(AttackType attackType, int damage, String name) {
        this.attackType = attackType;
        this.damage = damage;
        if(GlobalSettings.DEBUGGING_ON) this.damage = 100; // TODO
        this.name = name;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
