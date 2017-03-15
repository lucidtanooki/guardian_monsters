package de.limbusdev.guardianmonsters.model.monsters;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.model.MonsterDB;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.abilities.AbilityGraph;

/**
 * Monster is the basic entity for the {@link BattleSystem}
 *
 * @author Georg Eckert 2015
 */
public class Monster extends Signal<Monster> implements Listener<Stat> {
    // ...................................................................................... STATIC
    public static int INSTANCECOUNTER=0;

    // .................................................................................. ATTRIBUTES
    public int INSTANCE_ID;
    public int ID;

    public MonsterData data;
    public Stat stat;
    public String nickname;

    private ArrayMap<Integer, Ability> activeAbilities;
    public AbilityGraph abilityGraph;

    // ................................................................................. CONSTRUCTOR

    public Monster(int ID) {
        this.INSTANCE_ID = INSTANCECOUNTER;
        INSTANCECOUNTER++;

        this.ID = ID;
        this.nickname = "";

        // Retrieve monster data from DataBase
        data = MonsterDB.singleton().getData(ID);
        Array<Element> elements = data.elements;

        // Initialize Ability Graph
        abilityGraph = new AbilityGraph(data);

        for(int i = 0; i<1; i++) {
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

        // Copy base stats over and register monster as listener at it's stats
        this.stat = new Stat(1, data.baseStat);
        this.stat.add(this);

    }

    /**
     * For Serialization only
     */
    public Monster() {
        this.INSTANCE_ID = INSTANCECOUNTER;
        INSTANCECOUNTER++;
    }


    /* ............................................................................... METHODS .. */


    public boolean equals(Object monster) {
        if(!(monster instanceof  Monster)) return false;
        else return equalsMonster((Monster) monster);
    }

    /**
     * Checks wether two monster objects define the same monster
     * @param otherMonster
     * @return
     */
    public boolean equalsMonster(Monster otherMonster) {
        if(INSTANCE_ID == otherMonster.INSTANCE_ID) return true;
        else return false;
    }


    /* ..................................................................... GETTERS & SETTERS .. */



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

    @Override
    public String toString() {
        String out = "";
        out += MonsterDB.singleton().getNameById(ID) + " Level: " + stat.getLevel();
        return out;
    }


    // ............................................................................... STAT LISTENER

    @Override
    public void receive(Signal<Stat> signal, Stat object) {
        dispatch(this);
    }
}
