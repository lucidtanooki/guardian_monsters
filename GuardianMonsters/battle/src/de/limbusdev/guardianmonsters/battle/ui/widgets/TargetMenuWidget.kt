package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.utils.ArrayMap

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.services.Services


/**
 * @author Georg Eckert 2019
 */

class TargetMenuWidget(callbacks: (Int) -> Unit) : SevenButtonsWidget(callbacks, order), Observer
{
    // ............................................................................ Companion Object
    companion object
    {
        private const val TAG : String = "TargetMenuWidget"
        private val order : IntArray = intArrayOf(0, 2, 1, 3, 6, 5, 4)
    }


    // .................................................................................. Properties
    private var leftTeam    : CombatTeam = CombatTeam()
    private var rightTeam   : CombatTeam = CombatTeam()
    private var areaMode    : Boolean = false

    private val teamSlotOffsetLeft = 0
    private val teamSlotOffsetRight = 4
    private val leftSlots : IntRange = 0..2
    private val rightSlots : IntRange = 4..6


    // .............................................................................. Initialization
    fun initialize(battleSystem: BattleSystem, areaMode: Boolean = false)
    {
        this.areaMode = areaMode

        // Set all buttons inactive
        for (i in buttons.keys())
        {
            disableButton(i)
            setButtonText(4, "")
            setButtonStyle(i, Element.NONE)
        }

        val queue = battleSystem.queue
        addMonstersToMenu(queue.combatTeamLeft,  Side.LEFT)
        addMonstersToMenu(queue.combatTeamRight, Side.RIGHT)

        if (areaMode)
        {
            setButtonText(3, Services.getL18N().Battle().get("battle_choose_area"))
            setButtonStyle(0, Element.ARTHROPODA)
            setButtonStyle(1, Element.ARTHROPODA)
            setButtonStyle(2, Element.ARTHROPODA)
            setButtonStyle(4, Element.FIRE)
            setButtonStyle(5, Element.FIRE)
            setButtonStyle(6, Element.FIRE)
        }
    }

    private fun addMonstersToMenu(team: CombatTeam, side: Side)
    {
        val offset : Int = when(side)
        {
            Side.LEFT  -> { leftTeam  = team; teamSlotOffsetLeft  }
            Side.RIGHT -> { rightTeam = team; teamSlotOffsetRight }
        }

        for (key in team.keys())
        {
            val m = team.get(key)
            setButtonText(key + offset, Services.getL18N().getLocalizedGuardianName(m))
            enableButton(key + offset)

            // Add the TargetMenuWidget as a Listener
            m.addObserver(this)
        }
    }

    /**
     * Returns the Guardian on the given battle field position.
     * If index < 4, it will be from the left team, otherwise
     * from the right team.
     */
    fun getMonsterOfIndex(index: Int): AGuardian
    {
        return when (getSideByButtonIndex(index))
        {
            Side.LEFT  -> leftTeam[index - teamSlotOffsetLeft]
            Side.RIGHT -> rightTeam[index - teamSlotOffsetRight]
        }
    }

    fun getCombatTeamOfIndex(index: Int): ArrayMap<Int, AGuardian>
    {
        return when(getSideByButtonIndex(index))
        {
            Side.LEFT  -> leftTeam
            Side.RIGHT -> rightTeam
        }
    }

    fun disableSide(side: Side)
    {
        val indicesToBeDisabled = when(side)
        {
            Side.LEFT  -> leftSlots
            Side.RIGHT -> rightSlots
        }

        for(i in indicesToBeDisabled) { disableButton(i) }
    }

    fun getButtonPositionByFieldPosition(side: Side, fieldPosition: Int): Int
    {
        return when(side)
        {
            Side.LEFT  -> fieldPosition
            Side.RIGHT -> fieldPosition + teamSlotOffsetRight
        }
    }

    fun getSideByButtonIndex(index: Int): Side
    {
        return when(index)
        {
            in leftSlots  -> Side.LEFT
            in rightSlots -> Side.RIGHT
            else -> throw IndexOutOfBoundsException("Battle field center (index 3) is always vacant.")
        }
    }

    override fun update(o: Observable, arg: Any?)
    {
        val guardian = o as Guardian

        if (guardian.individualStatistics.isKO)
        {
            val position: Int
            val side: Side

            if (leftTeam.isMember(guardian))
            {
                side = Side.LEFT
                position = leftTeam.getFieldPosition(guardian)
            }
            else
            {
                side = Side.RIGHT
                position = rightTeam.getFieldPosition(guardian)
            }

            val buttonIndex = getButtonPositionByFieldPosition(side, position)
            disableButton(buttonIndex)
        }
    }
}
