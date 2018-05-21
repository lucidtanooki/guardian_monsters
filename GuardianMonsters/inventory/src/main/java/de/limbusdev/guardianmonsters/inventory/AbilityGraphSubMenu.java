package main.java.de.limbusdev.guardianmonsters.inventory;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.AScreen;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.metamorphosis.MetamorphosisScreen;
import de.limbusdev.guardianmonsters.ui.widgets.LogoWithCounter;
import de.limbusdev.guardianmonsters.ui.widgets.ScrollableWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.AbilityDetailWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.GraphWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.team.TeamMemberSwitcher;


/**
 * AblityMapSubMenu contains the AbilityGraph and makes it possible to further develop the active
 * monster.
 *
 * @author Georg Eckert 2017
 */

public class AbilityGraphSubMenu extends AInventorySubMenu implements Listener<Guardian>,
    GraphWidget.Controller, TeamMemberSwitcher.Callbacks, AbilityDetailWidget.Callbacks,
        Observer {

    private ArrayMap<Integer, AGuardian> team;
    private GraphWidget graphWidget;
    private AbilityDetailWidget details;
    private TeamMemberSwitcher switcher;
    LogoWithCounter remainingLevels;

    // ................................................................................. CONSTRUCTOR
    public AbilityGraphSubMenu(Skin skin, ArrayMap<Integer, AGuardian> team) {

        super(skin);
        this.team = team;

        graphWidget = new GraphWidget(skin, this);
        graphWidget.init(this.team.get(0));

        ScrollableWidget scrollWidget = new ScrollableWidget(Constant.WIDTH, Constant.HEIGHT - 36, 1200, 600, graphWidget, skin);
        scrollWidget.setPosition(0, 0, Align.bottomLeft);
        addActor(scrollWidget);

        switcher = new TeamMemberSwitcher(skin, this.team, this);
        switcher.setPosition(2, 202, Align.topLeft);
        addActor(switcher);

        details = new AbilityDetailWidget(skin, this, "button-learn");
        details.setPosition(Constant.WIDTH - 2, 2, Align.bottomRight);
        addActor(details);

        remainingLevels = new LogoWithCounter(skin, "label-bg-sandstone", "stats-symbol-exp");
        remainingLevels.setPosition(Constant.WIDTH - 2, 67, Align.bottomRight);
        addActor(remainingLevels);
        remainingLevels.counter.setText(Integer.toString(this.team.get(0).getIndividualStatistics().getAbilityLevels()));

        details.init(this.team.get(0), 0, false);

    }

    @Override
    protected void layout(Skin skin) {

    }

    public void init(ArrayMap<Integer, AGuardian> team, int teamPosition) {

        team.get(teamPosition).addObserver(this);
        switcher.init(team.get(teamPosition), teamPosition);
        graphWidget.init(team.get(teamPosition));
        details.init(team.get(teamPosition), 0, false);
    }

    // ........................................................................... INTERFACE METHODS
    @Override
    public void onNodeClicked(int nodeID) {
        AGuardian guardian = team.get(switcher.getCurrentlyChosen());
        details.init(guardian, nodeID, false);
    }

    @Override
    public void onChanged(int position) {

        team.get(switcher.getCurrentlyChosen()).deleteObserver(this);
        AGuardian selectedGuardian = team.get(position);
        graphWidget.init(selectedGuardian);
        propagateSelectedGuardian(position);
        remainingLevels.counter.setText(Integer.toString(selectedGuardian.getIndividualStatistics().getAbilityLevels()));
        selectedGuardian.addObserver(this);
    }

    @Override
    public void onLearn(int nodeID) {

        AGuardian m = team.get(switcher.getCurrentlyChosen());
        m.getIndividualStatistics().consumeAbilityLevel();
        m.getAbilityGraph().activateNode(nodeID);
        details.init(m, nodeID, false);

        if(m.getAbilityGraph().metamorphsAt(nodeID)) {

            AScreen screen = new MetamorphosisScreen(
                    m.getSpeciesDescription().getID(),
                    m.getAbilityGraph().getCurrentForm());
            Services.getScreenManager().pushScreen(screen);
        }
    }

    @Override
    public void syncSelectedGuardian(int teamPosition) {

        init(team, teamPosition);
    }

    @Override
    public void refresh() {

        AGuardian activeGuardian = team.get(switcher.getCurrentlyChosen());
        remainingLevels.counter.setText(Integer.toString(activeGuardian.getIndividualStatistics().getAbilityLevels()));
    }

    @Override
    public void receive(Signal<Guardian> signal, Guardian guardian) {

        if(guardian.equals(team.get(switcher.getCurrentlyChosen()))) {

            refresh();
        }
    }

    @Override
    public void update(Observable observable, Object o) {

        if(observable instanceof AGuardian) {
            refresh();
        }
    }
}
