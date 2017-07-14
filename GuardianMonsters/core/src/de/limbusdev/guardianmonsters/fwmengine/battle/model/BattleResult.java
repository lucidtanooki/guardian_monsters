package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * BattleResult
 * Collects all information about a battle. This includes EXP gained by killing an opponents
 * monster, items, left by opponents and so on.
 *
 * @author Georg Eckert 2017
 */

public class BattleResult {

    private ArrayMap<Monster, Integer> gainedEXP;
    private Array<Item> droppedItems;

    public BattleResult(Team team, Array<Item> droppedItems) {
        this.gainedEXP = new ArrayMap<>();
        for(Monster monster : team.values()) {
            gainedEXP.put(monster,0);
        }
        this.droppedItems = droppedItems;

    }

    public void gainEXP(Monster monster, int EXP) {
        gainedEXP.put(monster, gainedEXP.get(monster) + EXP);
    }

    public boolean applyGainedEXP(Monster monster) {
        boolean levelUp = monster.stat.earnEXP(gainedEXP.get(monster));
        return levelUp;
    }

    /**
     * Applies the earned EXP to all monsters and returns an array of all monsters that reached the
     * next level.
     * @return  {@link Array} of {@link Monster}s that reached a new level
     */
    public Array<Monster> applyGainedEXPtoAll() {
        Array<Monster> leveledUpMonsters = new Array<>();
        for(Monster monster : gainedEXP.keys()) {
            boolean lvlUp = applyGainedEXP(monster);
            if(lvlUp) {
                leveledUpMonsters.add(monster);
            }
        }
        return leveledUpMonsters;
    }

    public int getGainedEXP(Monster monster) {
        return gainedEXP.get(monster);
    }
}
