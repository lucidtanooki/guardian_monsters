package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import org.limbusdev.monsterworld.enums.AttackType;
import org.limbusdev.monsterworld.enums.SFXType;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by georg on 12.12.15.
 */
public class Monster extends Observable {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Attack> attacks;
    public Array<Observer> observers;


    // -------------------------------------------------------------------------------------- STATUS
    public int ID;
    public int level;
    public int exp;
    public int physStrength;
    public int HPfull, HP;
    public int magicStrength;
    public int MPfull, MP;
    public long recovTime;      // Time until monster can attack again


    // ------------------------------------------------------------------------------- BATTLE STATUS
    public boolean ready;
    public boolean KO;
    public long    waitingSince;

    public Attack nextAttack;
    public int    nextTarget;


    /* ........................................................................... CONSTRUCTOR .. */

    public Monster() {
        // STATUS
        this.ID = 1;
        this.level = 1;
        this.exp = 0;
        this.physStrength = 10;
        this.HP = HPfull = 30;
        this.magicStrength = 5;
        this.MP = MPfull = 5;
        this.recovTime = 5000;

        // BATTLE
        this.ready = false;
        this.KO = false;
        this.waitingSince = 0;
        this.nextTarget = 0;

        // INIT
        this.attacks = new Array<Attack>();
        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Kick", SFXType.HIT, 0));
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

    /**
     * Call this method when this monster is added to a battle action waiting queue
     * @param targetPosition
     * @param attackIndex
     */
    public void prepareForAttack(int targetPosition, int attackIndex) {
        this.nextTarget = targetPosition;
        this.nextAttack = attacks.get(attackIndex);
    }

    /**
     * Call this method as soon as soon as this monster gets drawn from the queue
     */
    public void attack() {
        this.ready = false;
        this.waitingSince = TimeUtils.millis();
    }

    /**
     * Call regularly to check whether the monster has recovered
     */
    public void update() {
        if(TimeUtils.timeSinceMillis(waitingSince) > recovTime) ready = true;
    }

    /**
     * Call this method when a battle starts
     */
    public void initBattle() {
        this.ready = false;
        this.waitingSince = TimeUtils.millis();
    }

    /* ..................................................................... GETTERS & SETTERS .. */

    /**
     * Returns the percentage of EXP collected in this level
     */
    public int getExpPerc() {
        return MathUtils.round(exp/1.f/expAvailableInThisLevel()*100);
    }
}
