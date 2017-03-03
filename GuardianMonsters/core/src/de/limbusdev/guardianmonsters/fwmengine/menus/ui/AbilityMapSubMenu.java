package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.*;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityMapSubMenu extends AInventorySubMenu implements Observer {

    private ArrayMap<Integer, Monster> team;
    private GraphWidget graphWidget;
    private AbilityDetailWidget details;
    private TeamMemberSwitcher switcher;

    private Label remainingLvls;


    public AbilityMapSubMenu(Skin skin, final ArrayMap<Integer,Monster> teamMonsters) {
        super(skin);
        this.team = teamMonsters;
        for(Monster m : team.values()) {
            m.addObserver(this);
        }

        // Initial Setup
        Group container = new Group();
        container.setSize(1200,600);

        GraphWidget.CallbackHandler callbacks = new GraphWidget.CallbackHandler() {
            @Override
            public void onNodeClicked(int nodeID) {
                Monster monster = team.get(switcher.getCurrentlyChosen());
                details.init(monster,nodeID);
            }
        };

        graphWidget = new GraphWidget(skin, callbacks);
        graphWidget.setPosition(300,150,Align.bottomLeft);
        graphWidget.init(team.get(0));

        container.addActor(graphWidget);

        ScrollPane scrollPane = new ScrollPane(container,skin);
        scrollPane.setSize(GS.WIDTH,204);
        scrollPane.setPosition(0,0,Align.bottomLeft);
        scrollPane.setScrollBarPositions(true, true);
        scrollPane.layout();
        scrollPane.setScrollPercentX(.5f);
        scrollPane.setScrollPercentY(.5f);
        addActor(scrollPane);

        TeamMemberSwitcher.CallbackHandler handler = new TeamMemberSwitcher.CallbackHandler() {
            @Override
            public void onChanged(int position) {
                graphWidget.init(team.get(position));
                refresh();
            }
        };

        switcher = new TeamMemberSwitcher(skin, team, handler);
        switcher.setPosition(2,202,Align.topLeft);
        addActor(switcher);

        AbilityDetailWidget.CallbackHandler learnCallbacks = new AbilityDetailWidget.CallbackHandler() {
            @Override
            public void onLearn(int nodeID) {
                Monster m = team.get(switcher.getCurrentlyChosen());
                m.consumeAbilityLevel();
                m.abilityGraph.activateNode(nodeID);
                graphWidget.refreshStatus(m);
                details.init(m,nodeID);
            }
        };

        details = new AbilityDetailWidget(skin, learnCallbacks);
        details.setPosition(GS.WIDTH-2,2,Align.bottomRight);
        addActor(details);

        Group remLvlGrp = new Group();
        remLvlGrp.setSize(64,27);
        remLvlGrp.setPosition(4,6,Align.bottomLeft);
        Container remainingLvlsCont = new Container(remLvlGrp);
        remainingLvlsCont.setSize(64,27);
        remainingLvlsCont.setBackground(skin.getDrawable("label-bg-sandstone"));
        remainingLvlsCont.setPosition(GS.WIDTH-2,68,Align.bottomRight);
        Image lvls = new Image(skin.getDrawable("stats-symbol-exp"));
        lvls.setSize(16,16);
        lvls.setPosition(4,5,Align.bottomLeft);
        remainingLvls = new Label(Integer.toString(team.get(0).getAbilityLevels()), skin, "default");
        remainingLvls.setPosition(22,6,Align.bottomLeft);
        remLvlGrp.addActor(lvls);
        remLvlGrp.addActor(remainingLvls);
        addActor(remainingLvlsCont);

        details.init(team.get(0), 0);


    }

    public void refresh() {
        remainingLvls.setText(Integer.toString(team.get(switcher.getCurrentlyChosen()).getAbilityLevels()));
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof Monster) {
            Monster m = (Monster)o;
            if(m.equals(team.get(switcher.getCurrentlyChosen()))) {
                refresh();
            }
        }
    }
}
