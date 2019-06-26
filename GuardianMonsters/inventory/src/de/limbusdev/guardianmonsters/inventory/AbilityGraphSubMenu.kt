package de.limbusdev.guardianmonsters.inventory

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.AbilityDetailWidget
import de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.GraphWidget
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.TeamMemberSwitcher
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.metamorphosis.MetamorphosisScreen
import de.limbusdev.guardianmonsters.ui.widgets.LogoWithCounter
import de.limbusdev.guardianmonsters.ui.widgets.ScrollableWidget
import ktx.actors.txt


/**
 * AbilityGraphSubMenu contains the AbilityGraph and makes it possible to further develop the active
 * monster.
 *
 * @author Georg Eckert 2017
 */

class AbilityGraphSubMenu
(
        skin: Skin,
        private val team: Team
)
    : AInventorySubMenu(), Listener<Guardian>, GraphWidget.Controller, TeamMemberSwitcher.Callbacks, AbilityDetailWidget.Callbacks, Observer
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val graphWidget = GraphWidget(skin, this)
    private val details: AbilityDetailWidget
    private val switcher: TeamMemberSwitcher
    private var remainingLevels: LogoWithCounter


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        graphWidget.init(team[0])

        // Setup Scroll Widget
        val scrollWidget = ScrollableWidget(Constant.WIDTH, Constant.HEIGHT - 36, 1200, 600, graphWidget, skin)
        scrollWidget.setPosition(0f, 0f, Align.bottomLeft)

        // Setup Team Member Switcher
        switcher = TeamMemberSwitcher(skin, team, this)
        switcher.setPosition(2f, 202f, Align.topLeft)

        // Setup Ability Detail Widget
        details = AbilityDetailWidget(skin, this, "button-learn")
        details.setPosition(Constant.WIDTHf - 2, 2f, Align.bottomRight)
        details.init(team[0], 0, false)

        // Setup Remaining Levels Counter Widget
        remainingLevels = LogoWithCounter(skin, "label-bg-sandstone", "stats-symbol-exp")
        remainingLevels.setPosition(Constant.WIDTHf - 2, 67f, Align.bottomRight)
        remainingLevels.counter.txt = team[0].stats.abilityLevels.toString()

        // Put widgets in the correct order
        addActor(scrollWidget)
        addActor(switcher)
        addActor(details)
        addActor(remainingLevels)
    }

    override fun layout(skin: Skin) {}

    fun initialize(team: Team, teamPosition: Int)
    {
        team[teamPosition].addObserver(this)
        switcher.init(team[teamPosition], teamPosition)
        graphWidget.init(team[teamPosition])
        details.init(team[teamPosition], 0, false)
    }

    // ........................................................................... INTERFACE METHODS
    override fun onNodeClicked(nodeID: Int)
    {
        val guardian = team[switcher.currentlyChosen]
        details.init(guardian, nodeID, false)
    }

    override fun onChanged(position: Int)
    {
        team[switcher.currentlyChosen].deleteObserver(this)
        val selectedGuardian = team[position]
        graphWidget.init(selectedGuardian)
        propagateSelectedGuardian(position)
        remainingLevels.counter.txt = selectedGuardian.stats.abilityLevels.toString()
        selectedGuardian.addObserver(this)
    }

    override fun onLearn(nodeID: Int)
    {
        val guardian = team[switcher.currentlyChosen]
        val oldForm = guardian.currentForm  // remember old form, before it's changed with activateNode()
        guardian.stats.consumeAbilityLevel()
        guardian.abilityGraph.activateNode(nodeID)
        details.init(guardian, nodeID, false)

        if (guardian.abilityGraph.metamorphsAt(nodeID))
        {
            val screen = MetamorphosisScreen(guardian.speciesID, oldForm)
            Services.ScreenManager().pushScreen(screen)
        }
    }

    override fun syncSelectedGuardian(teamPosition: Int)
    {
        initialize(team, teamPosition)
    }

    override fun refresh()
    {
        val activeGuardian = team[switcher.currentlyChosen]
        remainingLevels.counter.txt = activeGuardian.stats.abilityLevels.toString()
    }

    override fun receive(signal: Signal<Guardian>, guardian: Guardian)
    {
        if (guardian == team[switcher.currentlyChosen])
        {
            refresh()
        }
    }

    override fun update(observable: Observable, o: Any?)
    {
        if (observable is AGuardian)
        {
            refresh()
        }
    }
}
