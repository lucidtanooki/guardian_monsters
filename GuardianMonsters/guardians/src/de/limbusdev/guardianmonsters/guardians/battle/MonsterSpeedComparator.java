package de.limbusdev.guardianmonsters.guardians.battle;

import java.util.Comparator;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * Created by georg on 15.08.16.
 */
public class MonsterSpeedComparator implements Comparator<AGuardian> {
    @Override
    public int compare(AGuardian o1, AGuardian o2) {
        return o1.getIndividualStatistics().getSpeed() - o2.getIndividualStatistics().getSpeed();
    }
}
