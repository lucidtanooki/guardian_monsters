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
    public Equipment weapon;
    public Equipment helmet;
    public Equipment armor;
    public Equipment shoes;


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
        this.HPfull = base.baseHP;
        this.HP = base.baseHP/2;
        this.mStr = mStrFull = base.baseMagStrength;
        this.MP = MPfull = base.baseMP;
        this.pDefFull = pDef = base.basePhysDefense;
        this.mDefFull = mDef = base.baseMagDefense;
        this.Speed = this.SpeedFull = base.baseSpeed;

        // INIT
        this.attacks = new Array<>();

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

    public void healHP(int value) {
        this.HP += value;
        if(this.HP > this.HPfull) this.HP = this.HPfull;
        setChanged();
        notifyObservers();
    }

    public void healMP(int value) {
        this.MP += value;
        if(this.MP > this.MPfull) this.MP = this.MPfull;
        setChanged();
        notifyObservers();
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

    public int getExtendedHPfull() {
        int hp=getHPfull();
        int hpAddFactor=0;
        if(weapon != null)  hpAddFactor += weapon.getAddsHP();
        if(armor != null)   hpAddFactor += armor.getAddsHP();
        if(helmet != null)  hpAddFactor += helmet.getAddsHP();
        if(shoes != null)   hpAddFactor += shoes.getAddsHP();
        hp *= (100f+hpAddFactor)/100f;
        return hp;
    }

    public int getExtendedMPfull() {
        int mp=getMPfull();
        int mpAddFactor=0;
        if(weapon != null)  mpAddFactor += weapon.getAddsMP();
        if(armor != null)   mpAddFactor += armor.getAddsMP();
        if(helmet != null)  mpAddFactor += helmet.getAddsMP();
        if(shoes != null)   mpAddFactor += shoes.getAddsMP();
        mp *= (100f+mpAddFactor)/100f;
        return mp;
    }

    public EquipmentPotential getEquipmentPotential(Equipment eq) {
        EquipmentPotential pot;

        Equipment currentEquipment;
        switch(eq.getEquipmentType()) {
            case HELMET:
                currentEquipment = helmet;
                break;
            case ARMOR:
                currentEquipment = armor;
                break;
            case SHOES:
                currentEquipment = shoes;
                break;
            default:
                currentEquipment = weapon;
                break;
        }

        if(currentEquipment == null) {
            pot = new EquipmentPotential(
                eq.getAddsHP(),
                eq.getAddsMP(),
                eq.getAddsSpeed(),
                eq.getAddsEXP(),
                eq.getAddsPStr(),
                eq.getAddsPDef(),
                eq.getAddsMStr(),
                eq.getAddsMDef()
            );
        } else {
            pot = new EquipmentPotential(
                eq.getAddsHP()      - currentEquipment.getAddsHP(),
                eq.getAddsMP()      - currentEquipment.getAddsMP(),
                eq.getAddsSpeed()   - currentEquipment.getAddsSpeed(),
                eq.getAddsEXP()     - currentEquipment.getAddsEXP(),
                eq.getAddsPStr()    - currentEquipment.getAddsPStr(),
                eq.getAddsPDef()    - currentEquipment.getAddsPDef(),
                eq.getAddsMStr()    - currentEquipment.getAddsMDef(),
                eq.getAddsMDef()    - currentEquipment.getAddsMDef()
            );
        }

        return pot;
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

    public class EquipmentPotential {
        public int hp, mp, speed, exp, pstr, pdef, mstr, mdef;

        public EquipmentPotential(int hp, int mp, int speed, int exp, int pstr, int pdef, int mstr, int mdef) {
            this.hp = hp;
            this.mp = mp;
            this.speed = speed;
            this.exp = exp;
            this.pstr = pstr;
            this.pdef = pdef;
            this.mstr = mstr;
            this.mdef = mdef;
        }
    }

    /**
     * Returns a replaced equipment, if there is any
     * @return replaced equipment
     */
    public Item equip(Equipment equipment) {
        Equipment replacedEq;
        switch(equipment.getEquipmentType()) {
            case ARMOR:
                replacedEq = armor;
                armor = equipment;
                break;
            case HELMET:
                replacedEq = helmet;
                helmet = equipment;
                break;
            case SHOES:
                replacedEq = shoes;
                shoes = equipment;
                break;
            default:
                replacedEq = weapon;
                weapon = equipment;
                break;
        }
        return replacedEq;
    }
}
