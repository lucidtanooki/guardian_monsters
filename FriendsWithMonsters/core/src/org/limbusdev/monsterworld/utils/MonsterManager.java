package org.limbusdev.monsterworld.utils;

import org.limbusdev.monsterworld.model.Monster;
import org.limbusdev.monsterworld.model.MonsterInformation;

/**
 * Handles events of monsters like level up, earning EXP, changing status and so on
 * Created by georg on 24.01.16.
 */
public class MonsterManager {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */

    /**
     * Earns a monster EXP, evolves it when necessary and learns it new attacks at specific levels
     * @param m
     * @param exp
     * @return
     */
    public static Monster earnEXP(Monster m, int exp) {
        boolean reachedNextLevel = m.getEXP(exp);
        if(reachedNextLevel) {
            if (MonsterInformation.getInstance().statusInfos.get(m.ID)
                    .learnableAttacks.containsKey(m.level))
                m.attacks.add(MonsterInformation.getInstance().statusInfos.get(m.ID)
                        .learnableAttacks.get(m.level));
            if(m.level >= MonsterInformation.getInstance().statusInfos.get(m.ID).evolvingAtLevel)
                m.evolution = MonsterInformation.getInstance().statusInfos.get(m.ID).evolution;
        }

        return m;
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
