package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.monsters.Monster;

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

    public int getRandomPosition() {
        return MathUtils.random(0,size-1);
    }

    public Monster getRandomMember() {
        return get(getRandomPosition());
    }
}
