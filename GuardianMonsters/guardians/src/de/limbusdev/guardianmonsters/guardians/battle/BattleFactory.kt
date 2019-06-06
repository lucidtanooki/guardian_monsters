package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.Team


/**
 * @author Georg Eckert
 */
object BattleFactory
{
    fun createOpponentTeam
    (
            availableGuardianProbabilities: ArrayMap<Int, Float>,
            teamSizeProbabilities: Array<Float>,
            minLevel: Int,
            maxLevel: Int
    ) : Team
    {
        val oneMonsterProb = 1 - teamSizeProbabilities.get(1) + teamSizeProbabilities.get(2)

        // Number of Monsters
        val numMonsters: Int = when
        {
            MathUtils.randomBoolean(oneMonsterProb)                                      -> 1
            MathUtils.randomBoolean(teamSizeProbabilities.get(1) / (1 - oneMonsterProb)) -> 2
            else                                                                         -> 3
        }

        val team = Team(availableGuardianProbabilities.size, numMonsters, numMonsters)

        val factory = GuardiansServiceLocator.guardianFactory

        for (j in 0 until numMonsters)
        {
            val level = MathUtils.random(minLevel, maxLevel)
            team.plus(factory.createGuardian(decideWhichMonster(availableGuardianProbabilities), level))
        }
        return team
    }

    private fun decideWhichMonster(availableGuardianProbabilities: ArrayMap<Int, Float>): Int
    {
        val p = MathUtils.random().toDouble()
        var cumulativeProbability = 0.0
        for (key in availableGuardianProbabilities.keys())
        {
            cumulativeProbability += availableGuardianProbabilities[key].toDouble()
            if (p <= cumulativeProbability) { return key }
        }
        return availableGuardianProbabilities.firstKey()
    }
}
