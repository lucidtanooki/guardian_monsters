package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * CombatTeam
 *
 * @author Georg Eckert 2017
 */

public class CombatTeam extends ArrayMap<Integer,Monster>
{
    /**
     * Returns a team of monsters for battle. Team size is determined by the maximum possible
     * team size and the chosen active team size.
     *
     * Even defeated monsters are added. They can be revived during battle. If all monsters of the
     * active team get defeated during battle, even if there are other monsters available, the
     * player is game over, because he can't call them anymore.
     *
     * Therefore players must watch out, that the active team never gets defeated completely.
     *
     * Only players can revive members of the combat party. Enemies can't.
     *
     * @return
     */
    public CombatTeam(Team team)
    {
        super();
        int teamSize = Math.min(team.getMaximumTeamSize(), team.getActiveTeamSize());
        for(int i=0; i < this.size && i < teamSize; i++) {
            put(i,get(i));
        }
    }

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
