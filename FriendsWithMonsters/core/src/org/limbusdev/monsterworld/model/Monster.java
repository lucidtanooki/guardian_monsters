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
    public long recovTime;      // Time until monster can attack again

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
        this.recovTime = 5000;

        this.attacks = new Array<Attack>();
        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Kick"));
    }
    /* ............................................................................... METHODS .. */
    public void getEXP(int exp) {
        System.out.println("Got " + exp + " EXP");
        this.exp += exp;
        int oldLevel = level;
        level = this.exp / 100 + 1;
        if(oldLevel < level) System.out.println("Reached level " + level);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
