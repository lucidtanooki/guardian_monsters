package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.StatusPentagonWidget
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.GuardianStatusWidget
import de.limbusdev.guardianmonsters.ui.widgets.TeamCircleWidget
import de.limbusdev.utils.logDebug
import de.limbusdev.utils.logInfo
import ktx.style.get

/**
 * @author Georg Eckert 2017
 */

class TeamSubMenu
(
        skin: Skin = Services.UI().inventorySkin,
        private val team: Team
)
    : AInventorySubMenu(), Observer
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "TeamSubMenu" }

    private lateinit var statPent           : StatusPentagonWidget
    private lateinit var monsterStats       : GuardianStatusWidget
    private lateinit var circleWidget       : TeamCircleWidget

    private lateinit var monsterImg         : Image
    private lateinit var blackOverlay       : Image

    private lateinit var joinToggleButton   : ImageButton   // activates a team position for battle
    private lateinit var swapButton         : ImageButton   // activates swap mode
    private lateinit var lockedStyle        : ImageButton.ImageButtonStyle
    private lateinit var defaultStyle       : ImageButton.ImageButtonStyle

    private lateinit var monsterChoice      : Group

    // ................................................................................... Callbacks
    private val onCircleWidgetChoice        : (Int) -> Unit // what happens, when circle is touched
    private val onCircleWidgetSwap          : (Int) -> Unit // what happens, when circle is touched in swap mode


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        onCircleWidgetChoice = { position ->

            showGuardianInformation(position)
            propagateSelectedGuardian(position) // propagation calls showGuardianInformation(...)
        }

        onCircleWidgetSwap = { position ->

            if (circleWidget.oldPosition != position)
            {
                val currentGuardian    = team[circleWidget.oldPosition]
                val guardianToSwapWith = team[position]

                team.swap(circleWidget.oldPosition, position)

                circleWidget.initialize(team)
                showGuardianInformation(position)
                propagateSelectedGuardian(position)
            }

            circleWidget.setHandler(onCircleWidgetChoice)
            blackOverlay.remove()
        }

        layout(skin)
        circleWidget.initialize(team)

        swapButton.replaceOnClick {

            circleWidget.remove()
            circleWidget.setHandler(onCircleWidgetSwap)
            addActor(blackOverlay)
            addActor(circleWidget)
        }

        joinToggleButton.replaceOnClick {

            joinToggleButton.toggle()
            team.activeTeamSize = when(joinToggleButton.isChecked)
            {
                true  -> team.activeTeamSize + 1
                false -> team.activeTeamSize - 1
            }
            logInfo(TAG) { "Now active in combat: ${team.activeTeamSize}" }
            updateJoinBattleHighlights()
        }

        monsterStats.initialize(team[0])

        showGuardianInformation(0)

        setDebug(InventoryDebugger.SCENE2D_DEBUG, true)
    }

    private fun updateJoinBattleHighlights()
    {
        for(i in 0..6) { circleWidget.unhighlight(i) }
        for(i in 0 until team.activeTeamSize) { circleWidget.highlight(i) }
    }

    override fun layout(skin: Skin)
    {
        circleWidget = TeamCircleWidget(skin, onCircleWidgetChoice)

        monsterChoice = makeGroup(width = 140f, height = Constant.HEIGHTf - 36)

        val monsterChoiceBg = makeImage(skin["menu-col-bg"], ImgPosition(2f, 2f), monsterChoice)

        circleWidget.setPosition(1f, 40f, Align.bottomLeft)
        monsterChoice.addActor(circleWidget)

        blackOverlay = makeImage(skin["black-a80"], ImgLayout(Constant.WIDTHf, Constant.HEIGHTf))
        swapButton = makeImageButton(skin["button-switch"], PositionXYA(8f, 8f), monsterChoice)

        joinToggleButton = makeImageButton(skin["button-check"], PositionXYA(140-8f, 8f, Align.bottomRight))

        defaultStyle = joinToggleButton.style
        lockedStyle = ImageButton.ImageButtonStyle().apply { checked = skin["button-check-down-locked"] }

        val monsterView = makeGroup(140f, Constant.HEIGHTf - 36, (140+2)*2f, 0f)
        val monsterViewBg = makeImage(skin["menu-col-bg"], ImgPosition(2f, 2f), monsterView)
        monsterImg = Image()
        monsterImg.setup(ImgLayout(128f, 128f, 6f, 202f, Align.topLeft), monsterView)

        monsterStats = GuardianStatusWidget()
        monsterStats.setPosition(140f + 2, 0f, Align.bottomLeft)

        statPent = StatusPentagonWidget(skin)
        statPent.setPosition(20f + 2, 4f, Align.bottomLeft)
        monsterView.addActor(statPent)

        addActor(monsterChoice)
        addActor(monsterStats)
        addActor(monsterView)

        updateJoinBattleHighlights()
    }

    private fun showGuardianInformation(teamPosition: Int)
    {
        logDebug(TAG) { "$teamPosition" } // is called twice, due to propagateSelectedGuardian(...)
        val guardian = team[teamPosition]

        // Update Stats Widget, Image and Stats Pentagram
        monsterStats.initialize(guardian)
        monsterImg.setRegion(Services.Media().getMonsterSprite(guardian.speciesID, guardian.currentForm))
        statPent.initialize(guardian)

        // Reset Join Battle Button
        joinToggleButton.remove()

        // Only team positions 0..2 can be activated
        if (teamPosition in 0..2)
        {
            monsterChoice.addActor(joinToggleButton)

            when
            {
                // Position 0 can never be disabled
                teamPosition == 0 ->
                {
                    joinToggleButton.isChecked = true
                    joinToggleButton.style = lockedStyle
                    joinToggleButton.touchable = Touchable.disabled
                }
                // Only the last active Guardian can be disabled
                teamPosition == team.activeTeamSize - 1 ->
                {
                    joinToggleButton.isChecked = true
                    joinToggleButton.style = defaultStyle
                    joinToggleButton.touchable = Touchable.enabled
                }
                // The Guardian after the last active one can be enabled, too
                teamPosition == team.activeTeamSize ->
                {
                    joinToggleButton.isChecked = false
                    joinToggleButton.style = defaultStyle
                    joinToggleButton.touchable = Touchable.enabled
                }
                // Active Guardians before the last active one cannot be disabled
                teamPosition < team.activeTeamSize ->
                {
                    joinToggleButton.isChecked = true
                    joinToggleButton.style = lockedStyle
                    joinToggleButton.touchable = Touchable.disabled
                }
                // Guardians not next to an enabled one cannot be activated
                else ->
                {
                    joinToggleButton.remove()
                }
            }
        }
    }

    override fun refresh() {}

    override fun syncSelectedGuardian(teamPosition: Int)
    {
        // Update widgets
        circleWidget.initialize(team, teamPosition)
        showGuardianInformation(teamPosition)

        team.values().forEach { it.deleteObserver(this) }

        team[teamPosition].addObserver(this)
    }

    override fun update(observable: Observable, o: Any?)
    {
        if (observable is AGuardian)
        {
            showGuardianInformation(team.getPosition(observable))
        }
    }
}
