package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.geometry.IntVec2
import ktx.actors.then


/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by Georg Eckert 2016
 */
class BattleStatusOverviewWidget(skin: Skin) : BattleWidget()
{
    private val monsterStateWidgets : ArrayMap<Side, Array<MonsterStateWidget>>

    init
    {
        monsterStateWidgets = ArrayMap()
        monsterStateWidgets[Side.LEFT]  = Array()
        monsterStateWidgets[Side.RIGHT] = Array()

        // Hero Team ###############################################################################
        var msw = MonsterStateWidget(skin, true)
        msw.setPosition(IndPos.statWPos1left.xf, IndPos.statWPos1left.yf, Align.topLeft)
        monsterStateWidgets[Side.LEFT].add(msw)
        msw = MonsterStateWidget(skin, true)
        msw.setPosition(IndPos.statWPos2left.xf, IndPos.statWPos2left.yf, Align.topLeft)
        monsterStateWidgets[Side.LEFT].add(msw)
        msw = MonsterStateWidget(skin, true)
        msw.setPosition(IndPos.statWPos3left.xf, IndPos.statWPos3left.yf, Align.topLeft)
        monsterStateWidgets[Side.LEFT].add(msw)

        // Opponent Team ###########################################################################
        msw = MonsterStateWidget(skin, false)
        msw.setPosition(IndPos.statWPos1right.xf, IndPos.statWPos1right.yf, Align.topRight)
        monsterStateWidgets[Side.RIGHT].add(msw)
        msw = MonsterStateWidget(skin, false)
        msw.setPosition(IndPos.statWPos2right.xf, IndPos.statWPos2right.yf, Align.topRight)
        monsterStateWidgets[Side.RIGHT].add(msw)
        msw = MonsterStateWidget(skin, false)
        msw.setPosition(IndPos.statWPos3right.xf, IndPos.statWPos3right.yf, Align.topRight)
        monsterStateWidgets[Side.RIGHT].add(msw)

        for (w in monsterStateWidgets[Side.LEFT])  { addActor(w) }
        for (w in monsterStateWidgets[Side.RIGHT]) { addActor(w) }

        setDebug(Constant.DEBUGGING_ON, true)

    }

    private fun addStatusWidgetsForTeam(team: CombatTeam, side: Side)
    {
        // Clear Actions
        for (w in monsterStateWidgets[side]) {
            w.clearActions()
            w.remove()
            w.isVisible = true
            w.color = Color.WHITE
        }

        // Initialize UI
        for (key in team.keys()) {
            monsterStateWidgets[side].get(key).initialize(team.get(key))
            addActor(monsterStateWidgets[side].get(key))
        }
    }

    fun init(battleSystem: BattleSystem)
    {
        addStatusWidgetsForTeam(battleSystem.queue.combatTeamLeft, Side.LEFT)
        addStatusWidgetsForTeam(battleSystem.queue.combatTeamRight, Side.RIGHT)
    }

    fun updateStatusWidgetToSubstitute(pos: Int, side: Side, guardian: AGuardian)
    {
        val w = monsterStateWidgets[side].get(pos)
        w.clearActions()
        w.addAction(

                Actions.fadeOut(1f)                     then
                Actions.run { w.initialize(guardian) }  then
                Actions.fadeIn(1f)
        )
    }


    /**
     * Possible Indicator coordinates
     */
    private object IndPos
    {
        val statWPos1left = IntVec2(56 + 24, 360 - 24)
        val statWPos2left = IntVec2(56, 360 - 48)
        val statWPos3left = IntVec2(56 + 48, 360)
        val statWPos1right = IntVec2(640 - 8 - 24, statWPos1left.y)
        val statWPos2right = IntVec2(640 - 8, statWPos2left.y)
        val statWPos3right = IntVec2(640 - 8 - 48, statWPos3left.y)
    }
}
