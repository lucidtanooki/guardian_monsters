package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.utils.extensions.set

/**
 * BattleResult
 * Collects all information about a battle. This includes EXP gained by killing an opponents
 * monster, items, left by opponents and so on.
 *
 * @author Georg Eckert 2017
 */
class BattleResult(team: Team, droppedItems: Array<Item>)
{
    // .................................................................................. Properties
    private val gainedEXP    : ArrayMap<AGuardian, Int> = ArrayMap()
    private val droppedItems : Array<Item>

    
    // ................................................................................ Constructors
    init
    {
        team.values().forEach { guardian -> gainedEXP[guardian] = 0 }
        this.droppedItems = droppedItems
    }


    // ..................................................................................... Methods
    fun gainEXP(guardian: AGuardian, EXP: Int)
    {
        gainedEXP[guardian] = gainedEXP[guardian] + EXP
    }

    private fun applyGainedEXP(guardian: AGuardian): Boolean
    {
        val indiStats = guardian.individualStatistics
        indiStats.earnEXP(gainedEXP[guardian])
        return indiStats.didLevelUp()
    }

    /**
     * Applies the earned EXP to all monsters and returns an array of all monsters that reached the
     * next level.
     * @return  [Array] of [Guardian]s that reached a new level
     */
    fun applyGainedEXPtoAll(): Array<AGuardian>
    {
        val leveledUpMonsters = Array<AGuardian>()

        for(guardian in gainedEXP.keys())
        {
            val didLvlUp = applyGainedEXP(guardian)
            if(didLvlUp) { leveledUpMonsters.add(guardian) }
        }

        return leveledUpMonsters
    }

    fun getGainedEXP(guardian: AGuardian): Int = gainedEXP[guardian]
}
