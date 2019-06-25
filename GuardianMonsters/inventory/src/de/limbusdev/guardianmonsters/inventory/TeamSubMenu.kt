package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.StatusPentagonWidget
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.GuardianStatusWidget
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener
import de.limbusdev.guardianmonsters.ui.widgets.TeamCircleWidget
import de.limbusdev.utils.logInfo
import ktx.style.get

/**
 * @author Georg Eckert 2017
 */

class TeamSubMenu(skin: Skin = Services.UI().inventorySkin, private val team: Team) : AInventorySubMenu(), Observer
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "TeamSubMenu" }

    private lateinit var statPent           : StatusPentagonWidget
    private lateinit var monsterStats       : GuardianStatusWidget
    private lateinit var monsterImg         : Image
    private lateinit var blackOverlay       : Image
    private lateinit var circleWidget       : TeamCircleWidget

    private lateinit var joinsBattleButton  : ImageButton
    private lateinit var swapButton         : ImageButton
    private lateinit var monsterChoice      : Group
    private lateinit var lockedButtonStyle  : ImageButton.ImageButtonStyle
    private lateinit var normalButtonStyle  : ImageButton.ImageButtonStyle

    // ................................................................................... Callbacks
    private val choiceHandler   : (Int) -> Unit
    private val swapHandler     : (Int) -> Unit


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init {

        choiceHandler = { position ->

            showGuardianInformation(position)
            propagateSelectedGuardian(position)
        }

        swapHandler = { position ->

            val oldPos = circleWidget.oldPosition

            if (position != oldPos)
            {
                val currentGuardian = team[oldPos]
                val guardianToSwapWith = team[position]

                team.swap(oldPos, position)

                circleWidget.initialize(team)
                showGuardianInformation(position)
                propagateSelectedGuardian(position)
            }

            circleWidget.setHandler(choiceHandler)
            blackOverlay.remove()
        }

        layout(skin)
        circleWidget.initialize(team)

        swapButton.replaceOnClick {

            circleWidget.remove()
            circleWidget.setHandler(swapHandler)
            addActor(blackOverlay)
            addActor(circleWidget)
        }

        joinsBattleButton.replaceOnClick {

            team.activeTeamSize = when(joinsBattleButton.isChecked)
            {
                true  -> team.activeTeamSize + 1
                false -> team.activeTeamSize - 1
            }
            logInfo(TAG) { "Now active in combat: ${team.activeTeamSize}" }
        }

        monsterStats.initialize(team[0])

        showGuardianInformation(0)

        setDebug(InventoryDebugger.SCENE2D_DEBUG, true)
    }

    override fun layout(skin: Skin)
    {
        circleWidget = TeamCircleWidget(skin, choiceHandler)

        monsterChoice = makeGroup(width = 140f, height = Constant.HEIGHTf - 36)

        val monsterChoiceBg = makeImage(skin["menu-col-bg"], ImgPosition(2f, 2f), monsterChoice)

        circleWidget.setPosition(1f, 40f, Align.bottomLeft)
        monsterChoice.addActor(circleWidget)

        blackOverlay = makeImage(skin["black-a80"], ImgLayout(Constant.WIDTHf, Constant.HEIGHTf))
        swapButton = makeImageButton(skin["button-switch"], PositionXYA(8f, 8f), monsterChoice)

        joinsBattleButton = makeImageButton(skin["button-check"], PositionXYA(140-8f, 8f, Align.bottomRight))

        normalButtonStyle = joinsBattleButton.style
        lockedButtonStyle = ImageButton.ImageButtonStyle().apply { checked = skin["button-check-down-locked"] }

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
    }

    private fun showGuardianInformation(teamPosition: Int)
    {
        monsterStats.initialize(team[teamPosition])

        val chosenGuardian = team[teamPosition]
        val guardianID = chosenGuardian.speciesID
        val guardianForm = chosenGuardian.abilityGraph.currentForm

        monsterImg.setRegion(Services.Media().getMonsterSprite(guardianID, guardianForm))
        statPent.init(team[teamPosition])
        joinsBattleButton.remove()
        joinsBattleButton.isChecked = false
        joinsBattleButton.style = normalButtonStyle

        // If the shown position belongs to the range given by activeInCombat & within 0-2
        if (teamPosition <= team.activeTeamSize && teamPosition < 3 && teamPosition < 3)
        {
            // TODO take max team size into account
            joinsBattleButton.touchable = Touchable.enabled
            monsterChoice.addActor(joinsBattleButton)

            // if shown monster is in the range of active monsters
            if (teamPosition < team.activeTeamSize)
            {
                joinsBattleButton.isChecked = true
            }
            // the shown monster is at position 0 or not the last active monster
            if (teamPosition == 0 || teamPosition < team.activeTeamSize - 1)
            {
                joinsBattleButton.touchable = Touchable.disabled
                joinsBattleButton.style = lockedButtonStyle
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
