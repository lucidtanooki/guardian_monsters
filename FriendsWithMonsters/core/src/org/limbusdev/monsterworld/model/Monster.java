package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.AttackType;

/**
 * Created by georg on 12.12.15.
 */
public class Monster {
    /* ............................................................................ ATTRIBUTES .. */
    public int level;
    public int exp;
    public int physStrength;
    public int HP;
    public int magicStrength;
    public int MP;

    public Array<Attack> attacks;
    /* ........................................................................... CONSTRUCTOR .. */
    public Monster() {
        this.level = 1;
        this.exp = 0;
        this.physStrength = 10;
        this.HP = 30;
        this.magicStrength = 5;
        this.MP = 5;

        this.attacks = new Array<Attack>();
        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Kick"));
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
