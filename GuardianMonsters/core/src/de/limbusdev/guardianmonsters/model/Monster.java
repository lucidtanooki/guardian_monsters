package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


import java.util.Map;
import java.util.Observable;

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
    private int SpeedFull;

    public int getSpeed() {
        return Speed;
    }

    public int getSpeedFull() {
        return SpeedFull;
    }

    private int Speed;

    public int physDefFull, physDef;
    public int magicDefFull, magicDef;


    /* ........................................................................... CONSTRUCTOR .. */

    public Monster(int ID) {
        super();
        this.INSTANCE_ID =INSTANCECOUNTER;
        INSTANCECOUNTER++;
        // STATUS
        this.ID = ID;
        this.level = 1;
        this.exp = 0;
        this.physStrength = 10;
        this.HP = HPfull = 30;
        this.magicStrength = 5;
        this.MP = MPfull = 5;
        this.physDefFull = physDef = 10;
        this.magicDefFull = magicDef = 10;
        this.Speed = this.SpeedFull = 10;

        // INIT
        this.attacks = new Array<Attack>();

        for(ObjectMap.Entry<Integer,Attack> e : MonsterInformation.getInstance().statusInfos.get(ID).learnableAttacks)
            attacks.add(e.value);


        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Scratch", SFXType.HIT, 0));
        attacks.add(new Attack(AttackType.PHYSICAL, 10, "Kick", SFXType.CUT, 0));
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

    public void update() {
        // TODO
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
