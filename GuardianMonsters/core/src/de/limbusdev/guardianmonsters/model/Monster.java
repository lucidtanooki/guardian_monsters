package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.enums.SFXType;

/**
 * Created by georg on 12.12.15.
 */
public class Monster extends Observable {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Attack> attacks;
    public static int INSTANCECOUNTER=0;
    public int INSTANCE_ID;
    public int evolution;


    // -------------------------------------------------------------------------------------- STATUS
    public int ID;
    public int level;
    private int exp;
    public int physStrength;
    private int HPfull, HP;
    public int magicStrength;
    private int MPfull, MP;

    public int physDefFull, physDef;
    public int magicDefFull, magicDef;

    public long recovTime;      // Time until monster can attack again


    // ------------------------------------------------------------------------------- BATTLE STATUS
    public boolean ready;
    public boolean KO;
    public boolean attackingRightNow;   // whether monster is involved in battle right now
    public boolean waitingInQueue;
    public long    waitingSince;
    public long    attackStarted;       // Time when attack started
    public int     battleFieldPosition;

    public Attack nextAttack;
    public int    nextTarget;


    /* ........................................................................... CONSTRUCTOR .. */

    public Monster() {
        super();
        this.INSTANCE_ID =INSTANCECOUNTER;
        INSTANCECOUNTER++;
        // STATUS
        this.ID = 1;
        this.level = 1;
        this.exp = 0;
        this.physStrength = 10;
        this.HP = HPfull = 30;
        this.magicStrength = 5;
        this.MP = MPfull = 5;
        this.physDefFull = physDef = 10;
        this.magicDefFull = magicDef = 10;
        this.recovTime = 3000;

        // BATTLE
        this.ready = false;
        this.KO = false;
        this.attackingRightNow = false;
        this.waitingInQueue = false;
        this.waitingSince = 0;
        this.attackStarted = 0;
        this.nextTarget = 0;
        this.battleFieldPosition = 0;

        // INIT
        this.attacks = new Array<Attack>();
        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Kick", SFXType.HIT, 0));
    }
    /* ............................................................................... METHODS .. */

    /**
     *
     * @param exp
     * @return  true if reached next level
     */
    public boolean receiveEXP(int exp) {
        boolean ans = false;
        System.out.println("Got " + exp + " EXP");
        this.exp += exp;

        // Increase Level
        if(this.exp >= expAvailableInThisLevel()) {
            this.exp -= expAvailableInThisLevel();
            level++;
            System.out.println("Reached Level " + level);
            ans = true;
            this.physStrength+=1;
            this.HPfull+=2;
            this.MPfull+=1;
            this.magicStrength+=1;
            this.physDefFull+=1;
            this.magicDefFull+=1;
        }
        System.out.println("EXP: " + this.exp);

        this.setChanged();
        this.notifyObservers();

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
        this.ready = false;
        this.waitingInQueue = true;
    }

    /**
     * Call this method as soon as this monster gets removed from the queue
     */
    public void finishAttack() {
        this.waitingSince = TimeUtils.millis();
        waitingInQueue = false;
        attackingRightNow = false;
    }

    /**
     * Call this method when monster reaches first slot in Queue
     */
    public void startAttack() {
        this.attackStarted = TimeUtils.millis();
        this.attackingRightNow = true;
    }

    /**
     * Call regularly to check whether the monster has recovered
     */
    public void update() {
        if(!waitingInQueue && TimeUtils.timeSinceMillis(waitingSince) > recovTime) ready = true;
    }

    /**
     * Call this method when a battle starts
     */
    public void initBattle(int position) {
        this.ready = false;
        this.waitingSince = TimeUtils.millis();
        this.battleFieldPosition = position;
        waitingInQueue = false;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof  Monster)) return false;
        if(((Monster)o).INSTANCE_ID == this.INSTANCE_ID) return true;
        else return false;
    }

    /* ..................................................................... GETTERS & SETTERS .. */

    /**
     * Returns the percentage of EXP collected in this level
     */
    public int getExpPerc() {
        return MathUtils.round(exp/1.f/expAvailableInThisLevel()*100);
    }

    public int getHPPerc() {
        return MathUtils.round(100f*HP/HPfull);
    }

    public int getMPPerc() {
        return MathUtils.round(100f*MP/MPfull);
    }

    public int getNextAttackDuration() {
        return 1000;
    }

    public int getLevel() {
        return level;
    }

    public void increaseLevel(int level) {
        ++this.level;
        this.setChanged();
        this.notifyObservers();
    }

    public int getExp() {
        return exp;
    }

    public int getHPfull() {
        return HPfull;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
        this.setChanged();
        this.notifyObservers();
    }

    public int getMPfull() {
        return MPfull;
    }

    public int getMP() {
        return MP;
    }

    public void setMP(int MP) {
        this.MP = MP;
        this.setChanged();
        this.notifyObservers();
    }
}
