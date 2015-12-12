package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.AttackType;

/**
 * Created by georg on 12.12.15.
 */
public class Monster {
    /* ............................................................................ ATTRIBUTES .. */
    public int ID;
    public int level;
    public int exp;
    public int physStrength;
    public int HPfull, HP;
    public int magicStrength;
    public int MPfull, MP;

    public Array<Attack> attacks;
    /* ........................................................................... CONSTRUCTOR .. */
    public Monster() {
        this.ID = 1;
        this.level = 1;
        this.exp = 0;
        this.physStrength = 10;
        this.HP = HPfull = 30;
        this.magicStrength = 5;
        this.MP = MPfull = 5;

        this.attacks = new Array<Attack>();
        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Kick"));
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
