package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Observable;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * Created by georg on 12.12.15.
 */
public class Monster extends Observable {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Attack> attacks;
    public static int INSTANCECOUNTER=0;
    public int INSTANCE_ID;
    public int evolution;
    public Array<Element> elements;


    // -------------------------------------------------------------------------------------- STATUS
    public int ID;
    public int level;
    private int exp;
    public int pStr, pStrFull;
    private int HPfull, HP;
    public int mStr, mStrFull;
    private int MPfull, MP;
    private int SpeedFull;

    public int getSpeed() {
        return Speed;
    }

    public int getSpeedFull() {
        return SpeedFull;
    }

    private int Speed;

    public int pDefFull, pDef;
    public int mDefFull, mDef;


    /* ........................................................................... CONSTRUCTOR .. */

    public Monster(int ID) {
        super();
        this.INSTANCE_ID =INSTANCECOUNTER;
        INSTANCECOUNTER++;
        // STATUS
        this.ID = ID;
        this.level = 1;
        this.exp = 0;
        BaseStat base = MonsterInformation.getInstance().statusInfos.get(ID).baseStat;
        this.pStr = pStrFull = base.basePhysStrength;
        this.HP = HPfull = base.baseHP;
        this.mStr = mStrFull = base.baseMagStrength;
        this.MP = MPfull = base.baseMP;
        this.pDefFull = pDef = base.basePhysDefense;
        this.mDefFull = mDef = base.baseMagDefense;
        this.Speed = this.SpeedFull = base.baseSpeed;

        // INIT
        this.attacks = new Array<Attack>();

        for(ObjectMap.Entry<Integer,Attack> e : MonsterInformation.getInstance().statusInfos.get(ID).learnableAttacks) {
            if(e.key <= level)
                attacks.add(e.value);
        }

        this.elements = MonsterInformation.getInstance().statusInfos.get(ID).elements;

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
            this.pStr +=1;
            this.HPfull+=2;
            this.MPfull+=1;
            this.mStr +=1;
            this.pDefFull +=1;
            this.mDefFull +=1;
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
        if(HP < 0) {
            this.HP = 0;
        } else if(HP > this.HPfull) {
            this.HP = HPfull;
        } else {
            this.HP = HP;
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void consumeMP(int cost) {
        if(cost > MP) {
            System.err.println("This attack consumed more MP that the monster had.");
            MP = 0;
        } else if(MP - cost > MPfull) {
            MP = MPfull;
        } else {
            MP -= cost;
        }
        System.out.println("Monster consumed " + cost + " MP and has " + getMPPerc() + "% (" + MP + ") left.");
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

    public void setpDef(int pDef) {
        this.pDef = pDef;
    }

    public void setmDef(int mDef) {
        this.mDef = mDef;
    }
}
