package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * Created by Georg Eckert on 12.12.15.
 */
public class Monster {
    // ...................................................................................... STATIC
    public static int INSTANCECOUNTER=0;

    // .................................................................................. ATTRIBUTES

    public int INSTANCE_ID;
    public final Stat stat;

    public String nickname;

    // -------------------------------------------------------------------------------------- STATUS
    public int ID;

    private ArrayMap<Integer,Ability> activeAbilities;
    public AbilityGraph abilityGraph;

    /* ........................................................................... CONSTRUCTOR .. */

    public Monster(int ID) {
        super();
        this.INSTANCE_ID = INSTANCECOUNTER;
        INSTANCECOUNTER++;
        // STATUS
        this.ID = ID;
        BaseStat base = MonsterDB.singleton().getStatusInfos().get(ID).baseStat;
        this.nickname = "";

        Array<Element> elements = MonsterDB.singleton().getStatusInfos().get(ID).elements;

        abilityGraph = new AbilityGraph();
        abilityGraph.init(MonsterDB.singleton().getData(ID));

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

        this.stat = new Stat(1, base, elements, this);

    }
    /* ............................................................................... METHODS .. */


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
