package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import java.util.Comparator;

import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * Created by georg on 15.08.16.
 */
public class MonsterSpeedComparator implements Comparator<Guardian> {
    @Override
    public int compare(Guardian o1, Guardian o2) {
        return o1.stat.getSpeed() - o2.stat.getSpeed();
    }
}
