package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.model.MonsterStatusInformation;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityMapSubMenu extends AInventorySubMenu {

    private ArrayMap<Integer, Monster> team;
    private AbilityGraph graph;
    private GraphWidget graphWidget;
    private AbilityDetailWidget details;
    private TeamMemberSwitcher switcher;

    private Label remainingLvls;


    public AbilityMapSubMenu(Skin skin, ArrayMap<Integer,Monster> teamMonsters) {
        super(skin);
        this.team = teamMonsters;

        // Initial Setup
        Group container = new Group();
        container.setSize(1200,600);

        graph = new AbilityGraph();

        GraphWidget.CallbackHandler callbacks = new GraphWidget.CallbackHandler() {
            @Override
            public void onNodeClicked(int nodeID) {
                Monster monster = team.get(switcher.getCurrentlyChosen());
                MonsterStatusInformation msi = MonsterInfo.getInstance().getStatusInfos().get(monster.ID);
                details.init(msi.attackAbilityGraphIds.get(nodeID), monster.abilityNodeStatus.get(nodeID));
            }
        };

        graphWidget = new GraphWidget(graph, skin, callbacks);
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
            }
        };

        switcher = new TeamMemberSwitcher(skin, team, handler);
        switcher.setPosition(2,202,Align.topLeft);
        addActor(switcher);

        details = new AbilityDetailWidget(skin);
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
        remainingLvls = new Label("0", skin, "default");
        remainingLvls.setPosition(22,6,Align.bottomLeft);
        remLvlGrp.addActor(lvls);
        remLvlGrp.addActor(remainingLvls);
        addActor(remainingLvlsCont);

        if(MonsterInfo.getInstance().getStatusInfos().get(team.get(0).ID).attackAbilityGraphIds.containsKey(0)) {
            details.init(MonsterInfo.getInstance().getStatusInfos().get(team.get(0).ID).attackAbilityGraphIds.get(0), team.get(0).abilityNodeStatus.get(0));
        }

    }
}
