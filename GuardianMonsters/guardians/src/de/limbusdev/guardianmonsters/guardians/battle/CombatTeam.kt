package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.utils.extensions.set

/**
 * CombatTeam
 *
 * @author Georg Eckert 2017
 */

class CombatTeam : ArrayMap<Int, AGuardian>
{
    // ................................................................................ Constructors
    constructor() : super() {}

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
    constructor(team: Team) : super()
    {
        val teamSize = Math.min(team.maximumTeamSize, team.activeTeamSize)
        var i = 0
        while (i < team.size && i < teamSize)
        {
            this[i] = team[i]
            i++
        }
    }


    // ..................................................................................... Methods
    fun exchange(position: Int, substitute: AGuardian): AGuardian
    {
        val replaced = this[position]
        this[position] = substitute
        return replaced
    }


    // ........................................................................... Getters & Setters
    private fun getRandomFitPosition(): Int
    {
        val fitPositions = Array<Int>()
        for (key in keys())
        {
            val guardian = this[key]
            if (guardian.individualStatistics.isFit) { fitPositions.add(key) }
        }
        return fitPositions[MathUtils.random(0, fitPositions.size - 1)]
    }

    /**
     * Returns a random fit monster of this combat team
     * @return
     */
    fun getRandomFitMember(): AGuardian = this[getRandomFitPosition()]

    /**
     * Returns the current position of the given monster on the battle field
     * @param guardian
     * @return battle field position
     */
    fun getFieldPosition(guardian: AGuardian): Int
    {
        if (!containsValue(guardian, false))
        {
            throw IllegalArgumentException("Monster $guardian is not in this CombatTeam")
        }
        return getKey(guardian, false)
    }

    fun isMember(guardian: AGuardian): Boolean = containsValue(guardian, false)

    /**
     * Whether the whole combat team is defeated
     * @return
     */
    fun isKO(): Boolean
    {
        var ko = true
        values().forEach { guardian -> ko = ko && guardian.individualStatistics.isKO }
        return ko
    }

    fun countFitMembers(): Int
    {
        var counter = 0
        for (guardian in values())
        {
            if (guardian.individualStatistics.isFit) { counter++ }
        }
        return counter
    }
}
