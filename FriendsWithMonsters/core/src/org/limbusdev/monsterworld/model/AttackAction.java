package org.limbusdev.monsterworld.model;

/**
 * Created by georg on 19.12.15.
 */
public class AttackAction {
    /* ............................................................................ ATTRIBUTES .. */
    public int target;
    public Attack attack;
    /* ........................................................................... CONSTRUCTOR .. */

    public AttackAction(int target, Attack attack) {
        this.target = target;
        this.attack = attack;
    }

/* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
