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

    public AbilityGraph abilityGraph;

    // ................................................................................. CONSTRUCTOR

    public Monster(int ID) {
        this.INSTANCE_ID = INSTANCECOUNTER;
        INSTANCECOUNTER++;

        this.ID = ID;
        this.nickname = "";

        // Retrieve monster data from DataBase
        data = MonsterDB.getData(ID);
        Array<Element> elements = data.elements;

        // Initialize Ability Graph
        abilityGraph = new AbilityGraph(data);
        abilityGraph.activateNode(0);
        abilityGraph.setActiveAbility(0,0);

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

    @Override
    public String toString() {
        String out = "";
        out += MonsterDB.getLocalNameById(ID) + " Level: " + stat.getLevel();
        return out;
    }


    // ............................................................................... STAT LISTENER

    @Override
    public void receive(Signal<Stat> signal, Stat object) {
        dispatch(this);
    }
}
