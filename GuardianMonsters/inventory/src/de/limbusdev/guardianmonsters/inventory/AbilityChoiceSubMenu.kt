package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.AbilityDetailWidget
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.TeamMemberSwitcher
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.HoneyComb7ButtonsWidget
import de.limbusdev.guardianmonsters.ui.widgets.HoneyComb7ButtonsWidget.ABILITY_ORDER
import de.limbusdev.utils.logInfo
import ktx.style.get

/**
 * AbilityChoiceSubMenu
 *
 * @author Georg Eckert 2017
 */

class AbilityChoiceSubMenu
(
        private val skin: Skin,
        private val team: Team
)
    : AInventorySubMenu(), TeamMemberSwitcher.Callbacks, AbilityDetailWidget.Callbacks, Callback.ButtonID, Observer
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "AbilityChoiceSubMenu" }

    private val switcher            : TeamMemberSwitcher
    private var abilityButtons      : ButtonGroup<TextButton> = ButtonGroup()
    private val details             : AbilityDetailWidget
    private val abilitySlotButtons  : HoneyComb7ButtonsWidget
    private val back                : ImageButton
    private var abilityButtonTable  : Table = Table()
    private var abilitySlotChoice   : Group = Group()

    private var currentlyChosenAbility: Int = 0


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        currentlyChosenAbility = 0

        switcher = TeamMemberSwitcher(skin, team, this)
        details = AbilityDetailWidget(skin, this, "button-switch")
        abilitySlotButtons = HoneyComb7ButtonsWidget(Services.UI().battleSkin, this, ABILITY_ORDER)

        back = ImageButton(skin, "button-back")
        back.replaceOnButtonClick { abilitySlotChoice.remove() }

        layout(skin)
        initialize(team[0], 0)
    }

    /**
     * Initializes the menu with the data of the given monster
     * @param guardian
     */
    private fun initialize(guardian: AGuardian, teamPosition: Int)
    {
        team[switcher.currentlyChosen].deleteObserver(this)

        switcher.init(guardian, teamPosition)

        refreshAbilitySlotButtons()

        if (guardian.abilityGraph.learntAbilities.size > 0)
        {
            currentlyChosenAbility = guardian.abilityGraph.learntAbilities.firstKey()
            showAbilityDetails()
        }

        guardian.addObserver(this)

        reloadHoneyCombWidget(guardian)

        abilitySlotButtons.setCallbacks { buttonID ->

            guardian.abilityGraph.setActiveAbility(buttonID, currentlyChosenAbility)
            reloadHoneyCombWidget(guardian)
        }
    }

    private fun reloadHoneyCombWidget(guardian: AGuardian)
    {
        // Initialize HoneyComb Button Widget
        for(i in 0..6) { abilitySlotButtons.setButtonText(i, "") }
        for(ability in guardian.abilityGraph.activeAbilities)
        {
            if(ability.value != null)
            {
                abilitySlotButtons.setButtonText(ability.key, GuardiansServiceLocator.abilities[ability.value])
            }
        }
    }


    // --------------------------------------------------------------------------------------------- CALLBACKS
    // ................................................................ TeamMemberSwitcher.Callbacks
    override fun onChanged(position: Int) = propagateSelectedGuardian(position)

    override fun refresh() {}

    override fun layout(skin: Skin)
    {
        abilityButtonTable.align(Align.topLeft)

        switcher.setPosition(2f, 202f, Align.topLeft)
        addActor(switcher)

        // Put ability buttons into a scrollable panel
        val scrollPane = makeScrollPane(abilityButtonTable, skin, Scene2DLayout(140f, 204f, 100f, 0f), false, true, this)

        // Exactly one button must be checked at all times
        abilityButtons.setMaxCheckCount(1)
        abilityButtons.setMaxCheckCount(1)

        details.setPosition(Constant.WIDTHf - 2, 2f, Align.bottomRight)
        addActor(details)

        abilitySlotChoice.setSize(Constant.WIDTHf, Constant.HEIGHTf)
        abilitySlotChoice.setPosition(0f, 0f, Align.bottomLeft)

        // Setup transparent black overlay
        val overlay = makeImage(skin["black-a80"], ImgLayout(Constant.WIDTHf, Constant.HEIGHTf), abilitySlotChoice)

        abilitySlotButtons.setPosition(0f, 32f, Align.bottomLeft)
        abilitySlotChoice.addActor(abilitySlotButtons)

        back.setPosition(Constant.WIDTHf, 4f, Align.bottomRight)
        abilitySlotChoice.addActor(back)
    }

    private fun showAbilityDetails()
    {
        val guardian = team[switcher.currentlyChosen]
        details.init(guardian, currentlyChosenAbility, true)
    }


    // ............................................................... AbilityDetailWidget.Callbacks
    override fun onLearn(nodeID: Int)
    {
        addActor(abilitySlotChoice)
        refreshAbilitySlotButtons()
    }

    private fun refreshAbilitySlotButtons()
    {
        logInfo(TAG) { "Refresh Buttons" }

        abilityButtons.clear()
        abilityButtonTable.clearChildren()

        val i18n = Services.I18N().Abilities()

        val guardian = team[switcher.currentlyChosen]

        for (key in guardian.abilityGraph.learntAbilities.keys())
        {
            val abilityID = guardian.abilityGraph.learntAbilities.get(key)

            if (abilityID != null)
            {
                val ability = GuardiansServiceLocator.abilities.getAbility(abilityID)
                val button = TextButton(i18n.get(ability.name), skin, "item-button-sandstone")

                button.replaceOnButtonClick {

                    currentlyChosenAbility = key
                    showAbilityDetails()
                }

                abilityButtonTable.row().spaceBottom(1f)
                abilityButtonTable.add(button).width(140f).height(32f)
                abilityButtons.add(button)

                if (key == 0) { button.isChecked = true }
            }
        }
    }

    // ................................................................ SevenButtonsWidget.Callbacks
    override fun onClick(buttonID: Int)
    {
        val guardian = team[switcher.currentlyChosen]
        guardian.abilityGraph.setActiveAbility(buttonID, currentlyChosenAbility)
        refreshAbilitySlotButtons()
    }

    override fun syncSelectedGuardian(teamPosition: Int)
    {
        initialize(team[teamPosition], teamPosition)
    }

    override fun update(observable: Observable, o: Any?)
    {
        refresh()
        refreshAbilitySlotButtons()
    }
}
