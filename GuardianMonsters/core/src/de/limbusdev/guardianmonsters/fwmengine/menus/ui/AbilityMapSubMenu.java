package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities.AbilityDetailWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities.GraphWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.TeamMemberSwitcher;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.LogoWithCounter;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.ScrollableWidget;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.Constant;


/**
 * AblityMapSubMenu contains the AbilityGraph and makes it possible to further develop the active
 * monster.
 *
 * @author Georg Eckert 2017
 */

public class AbilityMapSubMenu extends AInventorySubMenu implements Observer,
    GraphWidget.Controller, TeamMemberSwitcher.Controller, AbilityDetailWidget.Controller {

    private ArrayMap<Integer, Monster> team;
    private GraphWidget graphWidget;
    private AbilityDetailWidget details;
    private TeamMemberSwitcher switcher;
    LogoWithCounter remainingLevels;

    // ................................................................................. CONSTRUCTOR
    public AbilityMapSubMenu(Skin skin, ArrayMap<Integer, Monster> team) {
        super(skin);
        this.team = team;

        for (Monster m : this.team.values()) {
            m.addObserver(this);
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
        remainingLevels.counter.setText(Integer.toString(this.team.get(0).getAbilityLevels()));

        details.init(this.team.get(0), 0);

    }

    /**
     * Takes the currently chosen monster and refreshes the display of remaining levels
     */
    @Override
    public void refresh() {
        Monster activeMonster = team.get(switcher.getCurrentlyChosen());
        remainingLevels.counter.setText(Integer.toString(activeMonster.getAbilityLevels()));
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Monster) {
            Monster m = (Monster) o;
            if (m.equals(team.get(switcher.getCurrentlyChosen()))) {
                refresh();
            }
        }
    }


    // ........................................................................... INTERFACE METHODS
    @Override
    public void onNodeClicked(int nodeID) {
        Monster monster = team.get(switcher.getCurrentlyChosen());
        details.init(monster, nodeID);
    }

    @Override
    public void onChanged(int position) {
        graphWidget.init(team.get(position));
        refresh();
    }

    @Override
    public void onLearn(int nodeID) {
        Monster m = team.get(switcher.getCurrentlyChosen());
        m.consumeAbilityLevel();
        m.abilityGraph.activateNode(nodeID);
        graphWidget.refreshStatus(m);
        details.init(m, nodeID);
    }

}
