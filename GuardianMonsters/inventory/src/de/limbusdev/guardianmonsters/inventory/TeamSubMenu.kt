package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.de.limbusdev.guardianmonsters.inventory.ui.widgets.team.BattleParticipationToggleButton
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.StatusStarGlyphWidget
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.GuardianStatusWidget
import de.limbusdev.guardianmonsters.ui.widgets.TeamCircleWidget
import de.limbusdev.utils.logDebug
import de.limbusdev.utils.logInfo
import ktx.style.get

/**
 * TeamSubMenu shows his team to the player. It allows organizing team order and which positions
 * join the fight. It shows Guardian details, like their [Equipment] and individual statistics.
 *
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

    private lateinit var starGlyph          : StatusStarGlyphWidget
    private lateinit var monsterStats       : GuardianStatusWidget
    private lateinit var circleWidget       : TeamCircleWidget

    private lateinit var monsterImg         : Image
    private lateinit var blackOverlay       : Image

    private lateinit var joinToggleButton   : BattleParticipationToggleButton // activates a team position for battle
    private lateinit var swapButton         : ImageButton   // activates swap mode
    private lateinit var lockedStyle        : ImageButton.ImageButtonStyle
    private lateinit var defaultStyle       : ImageButton.ImageButtonStyle

    private lateinit var monsterChoice      : Group

    // ................................................................................... Callbacks
    private lateinit var onCircleWidgetChoice: (Int) -> Unit // what happens, when circle is touched
    private lateinit var onCircleWidgetSwap  : (Int) -> Unit // what happens, when circle is touched in swap mode


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        layout(skin)
        setupCallbacks()

        circleWidget.initialize(team)
        monsterStats.initialize(team[0])
        showGuardianInformation(0)

        setDebug(InventoryDebugger.SCENE2D_DEBUG, true)
    }


    // --------------------------------------------------------------------------------------------- LAYOUT
    override fun layout(skin: Skin)
    {
        circleWidget = TeamCircleWidget(skin)

        monsterChoice = makeGroup(width = 140f, height = Constant.HEIGHTf - 36)

        val monsterChoiceBg = makeImage(skin["menu-col-bg"], ImgPosition(2f, 2f), monsterChoice)

        circleWidget.setPosition(1f, 40f, Align.bottomLeft)
        monsterChoice.addActor(circleWidget)

        blackOverlay = makeImage(skin["black-a80"], ImgLayout(Constant.WIDTHf, Constant.HEIGHTf))
        swapButton = makeImageButton(skin["button-switch"], PositionXYA(8f, 8f), monsterChoice)

        joinToggleButton = BattleParticipationToggleButton(skin, PositionXYA(140-8f, 8f, Align.bottomRight))

        defaultStyle = joinToggleButton.style
        lockedStyle = ImageButton.ImageButtonStyle().apply { checked = skin["button-check-down-locked"] }

        val monsterView = makeGroup(140f, Constant.HEIGHTf - 36, (140+2)*2f, 0f)
        val monsterViewBg = makeImage(skin["menu-col-bg"], ImgPosition(2f, 2f), monsterView)
        monsterImg = Image()
        monsterImg.setup(ImgLayout(128f, 128f, 6f, 202f, Align.topLeft), monsterView)

        monsterStats = GuardianStatusWidget()
        monsterStats.setPosition(140f + 2, 0f, Align.bottomLeft)

        starGlyph = StatusStarGlyphWidget(skin)
        starGlyph.setPosition(20f + 2, 4f, Align.bottomLeft)
        monsterView.addActor(starGlyph)

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
        starGlyph.initialize(guardian)

        // Reset Join Battle Button
        joinToggleButton.remove()

        // Only team positions 0..2 can be activated
        joinToggleButton.autoEnableOrDisable(teamPosition, team.activeTeamSize, monsterChoice)
    }


    // --------------------------------------------------------------------------------------------- SYNCHRONISATION
    override fun refresh() {}

    /** Guardian which are activated to join the battle, are highlighted with a red circle. */
    private fun updateJoinBattleHighlights()
    {
        for(i in 0..6) { circleWidget.unhighlight(i) }
        for(i in 0 until team.activeTeamSize) { circleWidget.highlight(i) }
    }

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


    // --------------------------------------------------------------------------------------------- CALLBACKS
    private fun setupCallbacks()
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

        circleWidget.setHandler(onCircleWidgetChoice)
    }
}
