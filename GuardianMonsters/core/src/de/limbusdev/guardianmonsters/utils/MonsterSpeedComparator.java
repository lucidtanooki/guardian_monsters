package de.limbusdev.guardianmonsters.utils;

import java.util.Comparator;

import de.limbusdev.guardianmonsters.model.MonsterInBattle;

/**
 * Created by georg on 15.08.16.
 */
public class MonsterSpeedComparator implements Comparator<MonsterInBattle> {
    @Override
    public int compare(MonsterInBattle o1, MonsterInBattle o2) {
        return o1.monster.getSpeed() - o2.monster.getSpeed();
    }
}
