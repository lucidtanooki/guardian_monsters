package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.AttackType;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by georg on 12.12.15.
 */
public class Monster extends Observable {
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
    public Array<Observer> observers;
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
        this.observers = new Array<Observer>();
    }
    /* ............................................................................... METHODS .. */

    /**
     *
     * @param exp
     * @return  true if reached next level
     */
    public boolean getEXP(int exp) {
        boolean ans = false;
        System.out.println("Got " + exp + " EXP");
        this.exp += exp;
        if(this.exp >= expAvailableInThisLevel()) {
            this.exp -= expAvailableInThisLevel();
            level++;
            System.out.println("Reached Level " + level);
            ans = true;
        }
        System.out.println("EXP: " + this.exp);

        return ans;
    }

    public int expAvailableInThisLevel() {
        return level*100;
    }

    /* ..................................................................... GETTERS & SETTERS .. */

    /**
     * Returns the percentage of EXP collected in this level
     */
    public int getExpPerc() {
        return MathUtils.round(exp/1.f/expAvailableInThisLevel()*100);
    }
}
