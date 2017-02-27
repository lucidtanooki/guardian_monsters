package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

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
import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.model.MonsterStatusInformation;
import de.limbusdev.guardianmonsters.utils.GS;

import static de.limbusdev.guardianmonsters.model.AbilityGraph.HORIZONTAL;
import static de.limbusdev.guardianmonsters.model.AbilityGraph.UPLEFT;
import static de.limbusdev.guardianmonsters.model.AbilityGraph.UPRIGHT;
import static de.limbusdev.guardianmonsters.model.AbilityGraph.VERTICAL;


/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityMapSubMenu extends AInventorySubMenu {

    private enum NodeStatus {
        DISABLED, ENABLED, ACTIVATED,
    }

    private ButtonGroup fieldGroup;
    private Group connections, circles;
    private ArrayMap<Integer,ImageButton> nodeButtons;
    private ArrayMap<AbilityGraph.Edge,Image> edgeImgs;
    private ArrayMap<Integer, Monster> team;
    private AbilityGraph graph;
    private TeamCircleWidget circleWidget;
    private Label fieldDescription, remainingLevels;
    private Button learn;
    private ImageButton.ImageButtonStyle disabled, enabled, activated;
    private ImageButton.ImageButtonStyle disabledAbility, enabledAbility, activatedAbility;
    private String[] edgeImgsDisabled = {"graph-horizontal","graph-vertical","graph-upleft","graph-upright"};

    public AbilityMapSubMenu(Skin skin, ArrayMap<Integer,Monster> teamMonsters) {
        super(skin);
        this.team = teamMonsters;
        this.disabled  = skin.get("board-disabled",  ImageButton.ImageButtonStyle.class);
        this.enabled   = skin.get("board-enabled",   ImageButton.ImageButtonStyle.class);
        this.activated = skin.get("board-activated", ImageButton.ImageButtonStyle.class);
        this.disabledAbility  = skin.get("board-ability-disabled",  ImageButton.ImageButtonStyle.class);
        this.enabledAbility   = skin.get("board-ability-enabled",   ImageButton.ImageButtonStyle.class);
        this.activatedAbility = skin.get("board-ability-activated", ImageButton.ImageButtonStyle.class);

        // Initial Setup
        Group container = new Group();
        container.setSize(1200,600);
        connections = new Group();
        connections.setSize(600,300);
        connections.setPosition(300,150,Align.bottomLeft);
        circles = new Group();
        circles.setSize(600,300);
        circles.setPosition(300,150,Align.bottomLeft);

        edgeImgs = new ArrayMap<>();
        graph = new AbilityGraph();

        fieldGroup = new ButtonGroup();
        fieldGroup.setMinCheckCount(1);
        fieldGroup.setMaxCheckCount(1);
        nodeButtons = new ArrayMap<>();

        container.addActor(connections);
        container.addActor(circles);

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
                initBoard(team.get(position));
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

        initBoard(team.get(0));

        //if(GS.DEBUGGING_ON) setDebug(true, true);

    }

    private void addConnection(Skin skin, AbilityGraph.Edge edge) {
        Image bar = null;
        String imgName = edgeImgsDisabled[edge.orientation];

        int align;
        switch(edge.orientation) {
            case HORIZONTAL:    align = Align.left; break;
            case VERTICAL:      align = Align.bottom; break;
            case UPLEFT:        align = Align.bottomRight; break;
            default:            align = Align.bottomLeft; break;
        }

        // Sort from down left to up right
        AbilityGraph.Vertex from, to;
        if(edge.orientation == HORIZONTAL || edge.orientation == UPRIGHT) {
            if(edge.from.x < edge.to.x) {
                from = edge.from;
                to = edge.from;
            } else {
                from = edge.to;
                to = edge.from;
            }
        } else {
            if(edge.from.y < edge.to.y) {
                from = edge.from;
                to = edge.from;
            } else {
                from = edge.to;
                to = edge.from;
            }
        }

        if(edge.orientation == HORIZONTAL) {
            int fx, tx;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;

            for(int x = fx; x < tx; x++) {
                bar = new Image(skin.getDrawable(imgName));
                bar.setPosition(600/2+x*32, 300/2-16+from.y*32, Align.bottomLeft);
                connections.addActor(bar);
            }
        }

        if(edge.orientation == VERTICAL) {
            int fy, ty;
            fy = from.y < to.y ? from.y : to.y;
            ty = from.y < to.y ? to.y : from.y;

            for(int y = fy; y < ty; y++) {
                bar = new Image(skin.getDrawable(imgName));
                bar.setPosition(600/2+from.x*32-16, 300/2+y*32, Align.bottomLeft);
                connections.addActor(bar);
            }
        }

        if(edge.orientation == UPRIGHT) {
            int fx, tx, fy, ty;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;
            fy = from.y < to.y ? from.y : to.y;
            ty = from.y < to.y ? to.y : from.y;

            int x=fx;
            for(int y = fy; y < ty; y++) {
                bar = new Image(skin.getDrawable(imgName));
                bar.setPosition(600/2+x*32-4, 300/2+y*32-4, Align.bottomLeft);
                connections.addActor(bar);
                x++;
            }
        }

        if(edge.orientation == UPLEFT) {

            int fx, tx, fy, ty;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;
            fy = from.y > to.y ? from.y : to.y;
            ty = from.y > to.y ? to.y : from.y;

            int x = fx;
            for(int y = fy; y > ty; y--) {
                bar = new Image(skin.getDrawable(imgName));
                bar.setPosition(600/2+x*32-4, 300/2+y*32-32-4, Align.bottomLeft);
                connections.addActor(bar);
                x++;
            }
        }

        if(bar != null) {
            edgeImgs.put(edge,bar);
        }
    }

    /**
     * Changes the style of the given node, according to its role and status
     * @param ID
     */
    private void changeNodeStyle(int ID, NodeStatus status) {
        Monster monster = team.get(circleWidget.getCurrentPosition());
        MonsterStatusInformation msi = MonsterInfo.getInstance().getStatusInfos().get(monster.ID);

        ImageButton.ImageButtonStyle ibs;
        switch(status) {
            case ACTIVATED:
                ibs = msi.learnableAttacks.containsKey(ID) ? activatedAbility : activated;
                break;
            case ENABLED:
                ibs = msi.learnableAttacks.containsKey(ID) ? enabledAbility : enabled;
                break;
            default: // DISABLED
                ibs = msi.learnableAttacks.containsKey(ID) ? disabledAbility : disabled;
                break;
        }

        nodeButtons.get(ID).setStyle(ibs);
    }

    private void enableNeighboringNodes(int ID) {
        for(AbilityGraph.Edge e : graph.getEdges()) {
            int idToEnable;
            if(e.from.ID == ID || e.to.ID == ID) {
                idToEnable = (ID == e.from.ID) ? e.to.ID : e.from.ID;
                if(!team.get(0).activatedAbilityNodes.contains(idToEnable)) {
                    changeNodeStyle(idToEnable, NodeStatus.ENABLED);
                }

                String imgName = edgeImgsDisabled[e.orientation] + "-active";
                edgeImgs.get(e).setDrawable(getSkin().getDrawable(imgName));
            }
        }
    }

    private void refreshDisplay(Monster monster) {
        for(int node : monster.activatedAbilityNodes) {
            changeNodeStyle(node, NodeStatus.ACTIVATED);
            enableNeighboringNodes(node);
        }
    }

    private void initBoard(Monster monster) {

        nodeButtons.clear();
        fieldGroup.clear();
        circles.clear();
        connections.clear();

        Skin skin = getSkin();

        for(AbilityGraph.Edge edge : graph.getEdges()) {
            addConnection(skin, edge);
        }

        for(AbilityGraph.Vertex v : graph.getVertices().values()) {
            // Add Circle
            ImageButton circle;
            if(MonsterInfo.getInstance().getStatusInfos().get(monster.ID).learnableAttacks.containsKey(v.ID)) {
                circle = new ImageButton(disabledAbility);
            } else {
                circle = new ImageButton(disabled);
            }
            circle.setPosition(600/2-circle.getWidth()/2+32*v.x,300/2-circle.getHeight()/2+32*v.y, Align.bottomLeft);
            circles.addActor(circle);
            nodeButtons.put(v.ID, circle);
            fieldGroup.add(circle);
        }
        nodeButtons.get(0).setChecked(true);

        refreshDisplay(monster);

    }
}
