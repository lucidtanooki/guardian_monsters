package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.battle.BattleQueue
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.Callback


/**
 * @author Georg Eckert 2017
 */
class MonsterMenuWidget
(
        skin: Skin,
        callbacks: (Int) -> Unit
)
    : SevenButtonsWidget(skin, callbacks, order)
{
    // ............................................................................ Companion Object
    companion object
    {
        private const val TAG : String = "MonsterMenuWidget"
        private val order : IntArray = intArrayOf(0, 1, 2, 3, 4, 5, 6)
    }


    // .............................................................................. Initialization
    fun init(battleSystem: BattleSystem, side: Side)
    {
        val queue = battleSystem.queue

        val team = when(side)
        {
            Side.LEFT  -> queue.left
            Side.RIGHT -> queue.right
        }

        val combatTeam = when(side)
        {
            Side.LEFT  -> queue.combatTeamLeft
            Side.RIGHT -> queue.combatTeamRight
        }

        // Set all buttons inactive
        for (i in buttons.keys()) { disableButton(i) }

        for (key in team.keys())
        {
            if (key > 6) break
            val m = team[key]
            val bwi = BattleHUDTextButton(Services.getL18N().getLocalizedGuardianName(m), skin, key, Element.AIR)

            replaceButton(bwi, key)

            if (m.individualStatistics.isFit && !combatTeam.containsValue(m, false))
            {
                enableButton(key)
            }
        }
    }
}
