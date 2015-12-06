package org.limbusdev.monsterworld.model;

import org.limbusdev.monsterworld.enums.AttackType;

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
        this.name = name;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
