package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
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
    private TeamCircleWidget circleWidget;
    private Label fieldDescription, remainingLevels;
    private Button learn;
    private GraphWidget graphWidget;


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
                Monster monster = team.get(circleWidget.getCurrentPosition());
                MonsterStatusInformation msi = MonsterInfo.getInstance().getStatusInfos().get(monster.ID);
                fieldDescription.setText("Empty");
                if(msi.attackAbilityGraphIds.containsKey(nodeID)) {
                    fieldDescription.setText(Services.getL18N().l18n(BundleAssets.ATTACKS).get(msi.attackAbilityGraphIds.get(nodeID).name));
                }
                if(msi.equipmentAbilityGraphIds.containsKey(nodeID)) {
                    fieldDescription.setText(msi.equipmentAbilityGraphIds.get(nodeID).toString());
                }
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

        TeamCircleWidget.ClickHandler clh = new TeamCircleWidget.ClickHandler() {
            @Override
            public void onTeamMemberButton(int position) {
                graphWidget.init(team.get(position));
            }
        };

        Group teamWidget = new Group();
        teamWidget.setSize(143,200);
        Label circleBg = new Label("", skin, "list-item");
        circleBg.setSize(143,200);
        circleBg.setPosition(0,0,Align.bottomLeft);
        circleWidget = new TeamCircleWidget(skin, teamMonsters, clh);
        circleWidget.setPosition(1,48,Align.bottomLeft);
        teamWidget.addActor(circleBg);
        teamWidget.addActor(circleWidget);
        teamWidget.setPosition(2,2,Align.bottomLeft);
        addActor(teamWidget);

        fieldDescription = new Label("Test", skin, "default");
        fieldDescription.setSize(120,32);
        fieldDescription.setPosition(3,7,Align.bottomLeft);
        fieldDescription.setAlignment(Align.topLeft,Align.topLeft);
        teamWidget.addActor(fieldDescription);

        learn = new ImageButton(skin, "button-learn");
        learn.setPosition(140-33,7,Align.bottomLeft);
        teamWidget.addActor(learn);

        Image lvlImg = new Image(skin.getDrawable("stats-symbol-exp"));
        lvlImg.setSize(16,16);
        lvlImg.setPosition(4,44,Align.bottomLeft);
        teamWidget.addActor(lvlImg);

        remainingLevels = new Label("0", skin, "default");
        remainingLevels.setPosition(21,44,Align.bottomLeft);
        teamWidget.addActor(remainingLevels);

    }
}
