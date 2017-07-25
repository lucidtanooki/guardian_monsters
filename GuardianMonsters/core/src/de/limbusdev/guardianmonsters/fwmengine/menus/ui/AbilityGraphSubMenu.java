package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities.AbilityDetailWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities.GraphWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.TeamMemberSwitcher;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.LogoWithCounter;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.ScrollableWidget;
import de.limbusdev.guardianmonsters.fwmengine.metamorphosis.MetamorphosisScreen;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.services.Services;


/**
 * AblityMapSubMenu contains the AbilityGraph and makes it possible to further develop the active
 * monster.
 *
 * @author Georg Eckert 2017
 */

public class AbilityGraphSubMenu extends AInventorySubMenu implements Listener<Guardian>,
    GraphWidget.Controller, TeamMemberSwitcher.Callbacks, AbilityDetailWidget.Callbacks {

    private ArrayMap<Integer, Guardian> team;
    private GraphWidget graphWidget;
    private AbilityDetailWidget details;
    private TeamMemberSwitcher switcher;
    LogoWithCounter remainingLevels;

    // ................................................................................. CONSTRUCTOR
    public AbilityGraphSubMenu(Skin skin, ArrayMap<Integer, Guardian> team) {
        super(skin);
        this.team = team;

        for (Guardian m : this.team.values()) {
            m.add(this);
        }

        graphWidget = new GraphWidget(skin, this);
        graphWidget.init(this.team.get(0));

        ScrollableWidget scrollWidget = new ScrollableWidget(Constant.WIDTH, Constant.HEIGHT - 36, 1200, 600, graphWidget, skin);
        scrollWidget.setPosition(0, 0, Align.bottomLeft);
        addActor(scrollWidget);

        switcher = new TeamMemberSwitcher(skin, this.team, this);
        switcher.setPosition(2, 202, Align.topLeft);
        addActor(switcher);

        details = new AbilityDetailWidget(skin, this);
        details.setPosition(Constant.WIDTH - 2, 2, Align.bottomRight);
        addActor(details);

        remainingLevels = new LogoWithCounter(skin, "label-bg-sandstone", "stats-symbol-exp");
        remainingLevels.setPosition(Constant.WIDTH - 2, 67, Align.bottomRight);
        addActor(remainingLevels);
        remainingLevels.counter.setText(Integer.toString(this.team.get(0).stat.getAbilityLevels()));

        details.init(this.team.get(0), 0, false);

    }

    /**
     * Takes the currently chosen monster and refreshes the display of remaining levels
     */
    @Override
    public void refresh() {
        Guardian activeGuardian = team.get(switcher.getCurrentlyChosen());
        remainingLevels.counter.setText(Integer.toString(activeGuardian.stat.getAbilityLevels()));
    }



    // ........................................................................... INTERFACE METHODS
    @Override
    public void onNodeClicked(int nodeID) {
        Guardian guardian = team.get(switcher.getCurrentlyChosen());
        details.init(guardian, nodeID, false);
    }

    @Override
    public void onChanged(int position) {
        graphWidget.init(team.get(position));
        refresh();
    }

    @Override
    public void onLearn(int nodeID) {
        Guardian m = team.get(switcher.getCurrentlyChosen());
        m.stat.consumeAbilityLevel();
        m.abilityGraph.activateNode(nodeID);
        details.init(m, nodeID, false);
        if(m.abilityGraph.metamorphsAt(nodeID)) {
            Services.getScreenManager().pushScreen(new MetamorphosisScreen(m.ID, m.ID+1));
        }
    }

    @Override
    public void receive(Signal<Guardian> signal, Guardian guardian) {
        if(guardian.equals(team.get(switcher.getCurrentlyChosen()))) {
            refresh();
        }
    }
}
