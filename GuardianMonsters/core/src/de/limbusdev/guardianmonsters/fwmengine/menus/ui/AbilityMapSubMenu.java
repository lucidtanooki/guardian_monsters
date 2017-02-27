package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.model.MonsterStatusInformation;
import de.limbusdev.guardianmonsters.utils.GS;



/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityMapSubMenu extends AInventorySubMenu {

    private Group connections, circles;
    private ArrayMap<Integer, Monster> team;
    private AbilityGraph graph;
    private TeamCircleWidget circleWidget;
    private Label fieldDescription, remainingLevels;
    private Button learn;


    public AbilityMapSubMenu(Skin skin, ArrayMap<Integer,Monster> teamMonsters) {
        super(skin);
        this.team = teamMonsters;



        // Initial Setup
        Group container = new Group();
        container.setSize(1200,600);
        connections = new Group();
        connections.setSize(600,300);
        connections.setPosition(300,150,Align.bottomLeft);
        circles = new Group();
        circles.setSize(600,300);
        circles.setPosition(300,150,Align.bottomLeft);

        graph = new AbilityGraph();

        GraphWidget gw = new GraphWidget(graph,skin);
        gw.setPosition(100,100,Align.bottomLeft);
        addActor(gw);


//        fieldGroup = new ButtonGroup();
//        fieldGroup.setMinCheckCount(1);
//        fieldGroup.setMaxCheckCount(1);
//        nodeButtons = new ArrayMap<>();
//
//        container.addActor(connections);
//        container.addActor(circles);
//
//        ScrollPane scrollPane = new ScrollPane(container,skin);
//        scrollPane.setSize(GS.WIDTH,204);
//        scrollPane.setPosition(0,0,Align.bottomLeft);
//        scrollPane.setScrollBarPositions(true, true);
//        scrollPane.layout();
//        scrollPane.setScrollPercentX(.5f);
//        scrollPane.setScrollPercentY(.5f);
//        addActor(scrollPane);
//
//        TeamCircleWidget.ClickHandler clh = new TeamCircleWidget.ClickHandler() {
//            @Override
//            public void onTeamMemberButton(int position) {
//                initBoard(team.get(position));
//            }
//        };

//        Group teamWidget = new Group();
//        teamWidget.setSize(143,200);
//        Label circleBg = new Label("", skin, "list-item");
//        circleBg.setSize(143,200);
//        circleBg.setPosition(0,0,Align.bottomLeft);
//        circleWidget = new TeamCircleWidget(skin, teamMonsters, clh);
//        circleWidget.setPosition(1,48,Align.bottomLeft);
//        teamWidget.addActor(circleBg);
//        teamWidget.addActor(circleWidget);
//        teamWidget.setPosition(2,2,Align.bottomLeft);
//        addActor(teamWidget);
//
//        fieldDescription = new Label("Test", skin, "default");
//        fieldDescription.setSize(120,32);
//        fieldDescription.setPosition(3,7,Align.bottomLeft);
//        fieldDescription.setAlignment(Align.topLeft,Align.topLeft);
//        teamWidget.addActor(fieldDescription);
//
//        learn = new ImageButton(skin, "button-learn");
//        learn.setPosition(140-33,7,Align.bottomLeft);
//        teamWidget.addActor(learn);
//
//        Image lvlImg = new Image(skin.getDrawable("stats-symbol-exp"));
//        lvlImg.setSize(16,16);
//        lvlImg.setPosition(4,44,Align.bottomLeft);
//        teamWidget.addActor(lvlImg);
//
//        remainingLevels = new Label("0", skin, "default");
//        remainingLevels.setPosition(21,44,Align.bottomLeft);
//        teamWidget.addActor(remainingLevels);
//
//        initBoard(team.get(0));

        //if(GS.DEBUGGING_ON) setDebug(true, true);

    }

//    private void addConnection(Skin skin, AbilityGraph.Edge edge) {
//        Image bar;
//        String imgName = edgeImgsDisabled.get(edge.orientation);
//
//        int align, lengthx, lengthy;
//        IntVec2 offset = new IntVec2(0,0);
//        switch(edge.orientation) {
//            case HORIZONTAL:    align = Align.left; break;
//            case VERTICAL:      align = Align.bottom; break;
//            case UPLEFT:        align = Align.bottomRight; offset = new IntVec2(2,-2); break;
//            default:            align = Align.bottomLeft; offset = new IntVec2(-2,-2); break;
//        }
//
//        // Sort from down left to up right
//        AbilityGraph.Vertex from, to;
//        if(edge.orientation == AbilityGraph.Orientation.HORIZONTAL || edge.orientation == AbilityGraph.Orientation.UPRIGHT) {
//            if(edge.from.x < edge.to.x) {
//                from = edge.from;
//                to = edge.to;
//            } else {
//                from = edge.to;
//                to = edge.from;
//            }
//        } else {
//            if(edge.from.y < edge.to.y) {
//                from = edge.from;
//                to = edge.to;
//            } else {
//                from = edge.to;
//                to = edge.from;
//            }
//        }
//        lengthx = Math.abs(from.x - to.x);
//        lengthy = Math.abs(from.y - to.y);
//
//        int x = 0;
//        int y = 0;
//        do {
//            bar = new Image(skin.getDrawable(imgName));
//            bar.setPosition((from.x+x*((edge.orientation == AbilityGraph.Orientation.UPLEFT ? -1 : 1)))*32+300+offset.x,(from.y+y)*32+150+offset.y,align);
//            connections.addActor(bar);
//            if(lengthx != 0) x++;
//            if(lengthy != 0) y++;
//        } while (x < lengthx || y < lengthy);
//
//        if(bar != null) {
//            edgeImgs.put(edge,bar);
//        }
//    }

    /**
     * Changes the style of the given node, according to its role and status
     */
//    private void changeNodeStyle(int ID, NodeStatus status) {
//        Monster monster = team.get(circleWidget.getCurrentPosition());
//        MonsterStatusInformation msi = MonsterInfo.getInstance().getStatusInfos().get(monster.ID);
//
//        ImageButton.ImageButtonStyle ibs;
//        switch(status) {
//            case ACTIVATED:
//                ibs = msi.learnableAttacks.containsKey(ID) ? activatedAbility : activated;
//                break;
//            case ENABLED:
//                ibs = msi.learnableAttacks.containsKey(ID) ? enabledAbility : enabled;
//                break;
//            default: // DISABLED
//                ibs = msi.learnableAttacks.containsKey(ID) ? disabledAbility : disabled;
//                break;
//        }
//
//        //nodeButtons.get(ID).setStyle(ibs);
//    }

//    private void enableNeighboringNodes(int ID) {
//        for(AbilityGraph.Edge e : graph.getEdges()) {
//            int idToEnable;
//            if(e.from.ID == ID || e.to.ID == ID) {
//                idToEnable = (ID == e.from.ID) ? e.to.ID : e.from.ID;
//                if(!team.get(0).activatedAbilityNodes.contains(idToEnable)) {
//                    changeNodeStyle(idToEnable, NodeStatus.ENABLED);
//                }
//
//                String imgName = edgeImgsDisabled.get(e.orientation) + "-active";
//                //edgeImgs.get(e).setDrawable(getSkin().getDrawable(imgName));
//            }
//        }
//    }
//
//    private void refreshDisplay(Monster monster) {
//        for(int node : monster.activatedAbilityNodes) {
//            changeNodeStyle(node, NodeStatus.ACTIVATED);
//            enableNeighboringNodes(node);
//        }
//    }

    private void initBoard(Monster monster) {

//        nodeButtons.clear();
//        fieldGroup.clear();
//        circles.clear();
//        connections.clear();
//
//        Skin skin = getSkin();
//
//        for(AbilityGraph.Edge edge : graph.getEdges()) {
//            //addConnection(skin, edge);
//        }
//
//        for(AbilityGraph.Vertex v : graph.getVertices().values()) {
//            // Add Circle
//            ImageButton circle;
//            if(MonsterInfo.getInstance().getStatusInfos().get(monster.ID).learnableAttacks.containsKey(v.ID)) {
//                circle = new ImageButton(disabledAbility);
//            } else {
//                circle = new ImageButton(disabled);
//            }
//            circle.setPosition(600/2-circle.getWidth()/2+32*v.x,300/2-circle.getHeight()/2+32*v.y, Align.bottomLeft);
//            circles.addActor(circle);
//            nodeButtons.put(v.ID, circle);
//            fieldGroup.add(circle);
//        }
//        nodeButtons.get(0).setChecked(true);

//        refreshDisplay(monster);

    }
}
