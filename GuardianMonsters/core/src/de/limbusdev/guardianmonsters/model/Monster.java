package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * Created by Georg Eckert on 12.12.15.
 */
public class Monster extends Observable {
    // ...................................................................................... STATIC
    public static int INSTANCECOUNTER=0;

    // .................................................................................. ATTRIBUTES

    public int INSTANCE_ID;
    public Array<Element> elements;
    public Equipment hands;
    public Equipment head;
    public Equipment body;
    public Equipment feet;
    public String    nickname;

    // -------------------------------------------------------------------------------------- STATUS
    public int ID;
    public int level;
    private int exp;
    public int pStr, pStrFull;
    private int HPfull, HP;
    public int mStr, mStrFull;
    private int MPfull, MP;
    private int SpeedFull;
    private int abilityLevels;
    private ArrayMap<Integer,Ability> activeAbilities;

    public int getSpeed() {
        return Speed;
    }

    public int getSpeedFull() {
        return SpeedFull;
    }

    public AbilityGraph abilityGraph;

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
        BaseStat base = MonsterDB.singleton().getStatusInfos().get(ID).baseStat;
        this.pStr = pStrFull = base.basePhysStrength;
        this.HPfull = base.baseHP;
        this.HP = base.baseHP;
        this.mStr = mStrFull = base.baseMagStrength;
        this.MP = MPfull = base.baseMP;
        this.pDefFull = pDef = base.basePhysDefense;
        this.mDefFull = mDef = base.baseMagDefense;
        this.Speed = this.SpeedFull = base.baseSpeed;
        this.nickname = "";
        this.abilityLevels = 30 ;

        this.elements = MonsterDB.singleton().getStatusInfos().get(ID).elements;

        abilityGraph = new AbilityGraph();
        abilityGraph.init(MonsterDB.singleton().getData(ID));

        for(int i = 0; i<level; i++) {
            abilityGraph.activateNode(i);
        }

        activeAbilities = new ArrayMap<>();
        for(int i=0; i<7; i++) {
            activeAbilities.put(i,null);
        }
        int counter = 0;
        for(Ability a : abilityGraph.learntAbilities.values()) {
            activeAbilities.put(counter, a);
            counter++;
        }

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
            this.abilityLevels++;
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

    /**
     * Returns HP by taking {@link Equipment} effects into account
     * @return
     */
    public int getExtendedHPfull() {
        int hp=getHPfull();
        int hpAddFactor=0;
        if(hands != null)  hpAddFactor += hands.getAddsHP();
        if(body != null)   hpAddFactor += body.getAddsHP();
        if(head != null)  hpAddFactor += head.getAddsHP();
        if(feet != null)   hpAddFactor += feet.getAddsHP();
        hp *= (100f+hpAddFactor)/100f;
        return hp;
    }

    /**
     * Returns MP by taking {@link Equipment} effects into account
     * @return
     */
    public int getExtendedMPfull() {
        int mp=getMPfull();
        int mpAddFactor=0;
        if(hands != null)  mpAddFactor += hands.getAddsMP();
        if(body != null)   mpAddFactor += body.getAddsMP();
        if(head != null)  mpAddFactor += head.getAddsMP();
        if(feet != null)   mpAddFactor += feet.getAddsMP();
        mp *= (100f+mpAddFactor)/100f;
        return mp;
    }

    /**
     * Calculates the {@link EquipmentPotential} of a given {@link Equipment} for this {@link Monster}
     * @param eq
     * @return
     */
    public EquipmentPotential getEquipmentPotential(Equipment eq) {
        EquipmentPotential pot;

        Equipment currentEquipment;
        switch(eq.getEquipmentType()) {
            case HEAD:
                currentEquipment = head;
                break;
            case BODY:
                currentEquipment = body;
                break;
            case FEET:
                currentEquipment = feet;
                break;
            default:
                currentEquipment = hands;
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

    /**
     * Reduces currently remaining MP, e.g., when using an {@link Ability}, which consumes MP
     * @param cost
     */
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

    /**
     * Returns the currently remaining MP, is reset to MPfull, when resting or sleeping
     * @return
     */
    public int getMP() {
        return MP;
    }

    public void setMP(int MP) {
        this.MP = MP;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Changes the current physical defense value,
     * should be resetted after battle
     * @param pDef
     */
    public void setpDef(int pDef) {
        this.pDef = pDef;
    }

    /**
     * Changes the current magical defense value,
     * should be resetted after battle
     * @param mDef
     */
    public void setmDef(int mDef) {
        this.mDef = mDef;
    }

    /**
     * Holds positive and negative values to show how much a given {@link Equipment} would improve
     * the various monster status values
     */
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
            case BODY:
                replacedEq = body;
                body = equipment;
                break;
            case HEAD:
                replacedEq = head;
                head = equipment;
                break;
            case FEET:
                replacedEq = feet;
                feet = equipment;
                break;
            default:
                replacedEq = hands;
                hands = equipment;
                break;
        }
        return replacedEq;
    }

    /**
     * returns the number of levels, which can be used to activate nodes in the {@link AbilityGraph}
     * @return
     */
    public int getAbilityLevels() {
        return abilityLevels;
    }

    /**
     * Reduces the number of available ability levels by 1,
     * usually, when a field in the ability board is activated
     */
    public void consumeAbilityLevel() {
        this.abilityLevels--;
        setChanged();
        notifyObservers();
    }

    /**
     * returns the ability placed at the given slot
     * @param abilitySlot   slot for in battle ability usage
     * @return              ability which resides there
     */
    public Ability getActiveAbility(int abilitySlot) {
        return activeAbilities.get(abilitySlot);
    }

    /**
     * Puts an ability into one of seven slots, available in battle
     * @param slot                  where the ability should be placed in battle
     * @param learntAbilityNumber   number of ability to be placed there
     */
    public void setActiveAbility(int slot, int learntAbilityNumber) {
        Ability abilityToLearn = abilityGraph.learntAbilities.get(learntAbilityNumber);
        if(abilityToLearn == null) return;

        for(int key : activeAbilities.keys()) {
            Ability abilityAtThisSlot = activeAbilities.get(key);

            if(abilityAtThisSlot != null) {
                if (abilityAtThisSlot.equals(abilityToLearn)) {
                    activeAbilities.put(key, null);
                }
            }
        }
        activeAbilities.put(slot, abilityGraph.learntAbilities.get(learntAbilityNumber));
    }
}
