package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import java.util.Comparator;

import de.limbusdev.guardianmonsters.guardians.monsters.Monster;

/**
 * Created by georg on 15.08.16.
 */
public class MonsterSpeedComparator implements Comparator<Monster> {
    @Override
    public int compare(Monster o1, Monster o2) {
        return o1.stat.getSpeed() - o2.stat.getSpeed();
    }
}
