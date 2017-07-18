package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * BattleResult
 * Collects all information about a battle. This includes EXP gained by killing an opponents
 * monster, items, left by opponents and so on.
 *
 * @author Georg Eckert 2017
 */

public class BattleResult
{
    private ArrayMap<Guardian, Integer> gainedEXP;
    private Array<Item> droppedItems;

    public BattleResult(Team team, Array<Item> droppedItems) {
        this.gainedEXP = new ArrayMap<>();
        for(Guardian guardian : team.values()) {
            gainedEXP.put(guardian,0);
        }
        this.droppedItems = droppedItems;

    }

    public void gainEXP(Guardian guardian, int EXP) {
        gainedEXP.put(guardian, gainedEXP.get(guardian) + EXP);
    }

    public boolean applyGainedEXP(Guardian guardian) {
        boolean levelUp = guardian.stat.earnEXP(gainedEXP.get(guardian));
        return levelUp;
    }

    /**
     * Applies the earned EXP to all monsters and returns an array of all monsters that reached the
     * next level.
     * @return  {@link Array} of {@link Guardian}s that reached a new level
     */
    public Array<Guardian> applyGainedEXPtoAll() {
        Array<Guardian> leveledUpMonsters = new Array<>();
        for(Guardian guardian : gainedEXP.keys()) {
            boolean lvlUp = applyGainedEXP(guardian);
            if(lvlUp) {
                leveledUpMonsters.add(guardian);
            }
        }
        return leveledUpMonsters;
    }

    public int getGainedEXP(Guardian guardian) {
        return gainedEXP.get(guardian);
    }
}
