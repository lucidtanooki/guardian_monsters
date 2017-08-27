package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * CombatTeam
 *
 * @author Georg Eckert 2017
 */

public class CombatTeam extends ArrayMap<Integer,AGuardian>
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
     * @param guardian
     * @return battle field position
     */
    public int getFieldPosition(AGuardian guardian) {
        if(!containsValue(guardian,false)) {
            throw new IllegalArgumentException("Monster " + guardian.toString() + " is not in this CombatTeam");
        }
        return getKey(guardian,false);
    }

    public boolean isMember(AGuardian guardian) {
        return containsValue(guardian,false);
    }

    public AGuardian exchange(int position, AGuardian substitute) {
        AGuardian replaced = get(position);
        put(position, substitute);
        return replaced;
    }

    public int getRandomFitPosition() {
        Array<Integer> fitPositions = new Array<>();
        for(int key : keys()) {
            AGuardian guardian = get(key);
            if(guardian.getStatistics().isFit()) {
                fitPositions.add(key);
            }
        }
        return fitPositions.get(MathUtils.random(0,fitPositions.size - 1));
    }

    /**
     * Returns a random fit monster of this combat team
     * @return
     */
    public AGuardian getRandomFitMember() {
        return get(getRandomFitPosition());
    }

    /**
     * Wether the whole combat team is defeated
     * @return
     */
    public boolean isKO() {
        boolean ko = true;
        for(AGuardian guardian : values()) {
            ko = ko && guardian.getStatistics().isKO();
        }
        return ko;
    }
}
