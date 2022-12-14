package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.battle.BattleDebugger

import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.geometry.IntVec2
import ktx.actors.then


/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the initialize() method
 * Created by Georg Eckert 2016
 */
class BattleStatusOverviewWidget() : BattleWidget()
{
    // .................................................................................. Properties
    private val monsterStateWidgets : ArrayMap<Side, Array<MonsterStateWidget>> = ArrayMap()


    // ................................................................................ Constructors
    init
    {
        monsterStateWidgets[Side.LEFT]  = Array()
        monsterStateWidgets[Side.RIGHT] = Array()

        // Hero Team
        monsterStateWidgets[Side.LEFT].add(setupGuardianStateWidget(true, IndPos.statWPos1left, Side.LEFT))
        monsterStateWidgets[Side.LEFT].add(setupGuardianStateWidget(true, IndPos.statWPos2left, Side.LEFT))
        monsterStateWidgets[Side.LEFT].add(setupGuardianStateWidget(true, IndPos.statWPos3left, Side.LEFT))

        // Opponent Team
        monsterStateWidgets[Side.RIGHT].add(setupGuardianStateWidget(true, IndPos.statWPos1right, Side.RIGHT))
        monsterStateWidgets[Side.RIGHT].add(setupGuardianStateWidget(true, IndPos.statWPos2right, Side.RIGHT))
        monsterStateWidgets[Side.RIGHT].add(setupGuardianStateWidget(true, IndPos.statWPos3right, Side.RIGHT))

        for (w in monsterStateWidgets[Side.LEFT])  { addActor(w) }
        for (w in monsterStateWidgets[Side.RIGHT]) { addActor(w) }

        setDebug(BattleDebugger.SCENE2D_DEBUG, true)
    }

    private fun setupGuardianStateWidget(showEXP: Boolean, position: IntVec2, side: Side) : MonsterStateWidget
    {
        val align = when(side) { Side.LEFT -> Align.topLeft; Side.RIGHT -> Align.topRight }
        val widget = MonsterStateWidget(showEXP)
        widget.setPosition(position.xf, position.yf, align)
        return widget
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

    fun initialize(battleSystem: BattleSystem)
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
