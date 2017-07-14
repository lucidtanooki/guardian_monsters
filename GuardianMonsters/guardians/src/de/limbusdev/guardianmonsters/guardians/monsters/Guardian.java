package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.MonsterDB;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;

/**
 * Monster is the basic entity for the BattleSystem
 *
 * @author Georg Eckert 2015
 */
public class Guardian extends Signal<Guardian> implements Listener<Stat>
{
    // ............................................................................................. STATIC
    public static int INSTANCECOUNTER=0;

    // ............................................................................................. ATTRIBUTES
    public final int INSTANCE_ID;
    public final int ID;

    public final MonsterData data;
    public final Stat stat;
    public final String nickname;

    public AbilityGraph abilityGraph;

    // ............................................................................................. CONSTRUCTOR

    public Guardian(int ID) {
        this.INSTANCE_ID = INSTANCECOUNTER;
        INSTANCECOUNTER++;

        this.ID = ID;
        this.nickname = "";

        // Retrieve monster data from DataBase
        data = MonsterDB.getData(ID);
        Array<Element> elements = data.getElements();

        // Initialize Ability Graph
        abilityGraph = new AbilityGraph(data);
        abilityGraph.activateNode(0);
        abilityGraph.setActiveAbility(0,0);

        int ancestors = MonsterDB.getNumberOfAncestors(ID);
        for(int i=0; i<ancestors; i++) {
            abilityGraph.activateNode(abilityGraph.metamorphosisNodes.get(i));
        }

        // Copy base stats over and register monster as listener at it's stats
        this.stat = new Stat(1, data.getBaseStat());
        this.stat.add(this);

    }

    /**
     * For Serialization Only!
     */
    public Guardian(int ID, String nickname, AbilityGraph abilityGraph, Stat stat) {
        this.INSTANCE_ID = INSTANCECOUNTER;
        INSTANCECOUNTER++;

        this.ID = ID;
        this.nickname = nickname;
        this.data = MonsterDB.getData(ID);
        this.stat = stat;
        this.abilityGraph = abilityGraph;
    }


    /* ............................................................................... METHODS .. */


    public boolean equals(Object monster) {
        if(!(monster instanceof Guardian)) return false;
        else return equalsMonster((Guardian) monster);
    }

    /**
     * Checks wether two monster objects define the same monster
     * @param otherGuardian
     * @return
     */
    public boolean equalsMonster(Guardian otherGuardian) {
        if(INSTANCE_ID == otherGuardian.INSTANCE_ID) return true;
        else return false;
    }


    /* ..................................................................... GETTERS & SETTERS .. */

    public String getName() {
        if(nickname.length() > 0) {
            return nickname;
        } else {
            return data.getNameID();
        }
    }

    @Override
    public String toString() {
        String out = "";
        out += data.getNameID() + " Level: " + stat.getLevel();
        return out;
    }


    // ............................................................................... STAT LISTENER

    @Override
    public void receive(Signal<Stat> signal, Stat object) {
        dispatch(this);
    }
}
