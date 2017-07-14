package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.Monster;

/**
 * CombatTeam
 *
 * @author Georg Eckert 2017
 */

public class CombatTeam extends ArrayMap<Integer,Monster> {
    public CombatTeam() {
        super();
    }

    /**
     * Returns the current position of the given monster on the battle field
     * @param monster
     * @return battle field position
     */
    public int getFieldPosition(Monster monster) {
        if(!containsValue(monster,false)) {
            throw new IllegalArgumentException("Monster " + monster.toString() + " is not in this CombatTeam");
        }
        return getKey(monster,false);
    }

    public boolean isMember(Monster monster) {
        return containsValue(monster,false);
    }

    public Monster exchange(int position, Monster substitute) {
        Monster replaced = get(position);
        put(position, substitute);
        return replaced;
    }

    public int getRandomFitPosition() {
        Array<Integer> fitPositions = new Array<>();
        for(int key : keys()) {
            Monster monster = get(key);
            if(monster.stat.isFit()) {
                fitPositions.add(key);
            }
        }
        return fitPositions.get(MathUtils.random(0,fitPositions.size - 1));
    }

    /**
     * Returns a random fit monster of this combat team
     * @return
     */
    public Monster getRandomFitMember() {
        return get(getRandomFitPosition());
    }

    /**
     * Wether the whole combat team is defeated
     * @return
     */
    public boolean isKO() {
        boolean ko = true;
        for(Monster monster : values()) {
            ko = ko && monster.stat.isKO();
        }
        return ko;
    }
}
