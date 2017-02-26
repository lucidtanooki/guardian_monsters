package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.utils.GS;

import static de.limbusdev.guardianmonsters.model.AbilityGraph.UPLEFT;
import static de.limbusdev.guardianmonsters.model.AbilityGraph.UPRIGHT;
import static de.limbusdev.guardianmonsters.model.AbilityGraph.VERTICAL;


/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityMapSubMenu extends AInventorySubMenu {

    private Group connections, circles;
    private ArrayMap<Integer,ImageButton> nodeButtons;
    private ArrayMap<AbilityGraph.Edge,Image> edgeImgs;
    private ArrayMap<Integer, Monster> team;
    private AbilityGraph graph;

    public AbilityMapSubMenu(Skin skin, ArrayMap<Integer,Monster> team) {
        super(skin);
        this.team = team;
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
        graph.getEdges();

        for(AbilityGraph.Edge e : graph.getEdges()) {
            AbilityGraph.Vertex curr = e.from;
            AbilityGraph.Vertex next = e.to;

            addConnection(skin, curr, next, e);
        }

        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.setMinCheckCount(1);
        btnGrp.setMaxCheckCount(1);
        nodeButtons = new ArrayMap<>();
        for(AbilityGraph.Vertex v : graph.getVertices().values()) {
            // Add Circle
            ImageButton circle;
            if(MonsterInfo.getInstance().getStatusInfos().get(team.get(0).ID).learnableAttacks.containsKey(v.ID)) {
                circle = new ImageButton(skin,"button-board-node-ability-disabled");
            } else {
                circle = new ImageButton(skin,"button-board-node-disabled");
            }
            circle.setPosition(600/2-8+32*v.x,300/2-8+32*v.y, Align.bottomLeft);
            circles.addActor(circle);
            nodeButtons.put(v.ID, circle);
            btnGrp.add(circle);
        }
        nodeButtons.get(0).setChecked(true);

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

        refreshDisplay();

        //if(GS.DEBUGGING_ON) setDebug(true, true);

    }

    private void addConnection(Skin skin, AbilityGraph.Vertex from, AbilityGraph.Vertex to, AbilityGraph.Edge edge) {
        Image bar = null;
        if(from.x != to.x && from.y == to.y) {
            // Horizontal
            int fx, tx;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;

            for(int x = fx; x < tx; x++) {
                bar = new Image(skin.getDrawable("ability-connection-hor"));
                bar.setPosition(600/2+x*32, 300/2-16+from.y*32, Align.bottomLeft);
                connections.addActor(bar);
            }
        }

        if(from.y != to.y && from.x == to.x) {
            // Vertical
            int fy, ty;
            fy = from.y < to.y ? from.y : to.y;
            ty = from.y < to.y ? to.y : from.y;

            for(int y = fy; y < ty; y++) {
                bar = new Image(skin.getDrawable("ability-connection-vert"));
                bar.setPosition(600/2+from.x*32-16, 300/2+y*32, Align.bottomLeft);
                connections.addActor(bar);
            }
        }

        if(from.y != to.y && from.x != to.x &&
            (from.x < to.x && from.y < to.y) ||
            (from.x > to.x && from.y > to.y)) {

            // Diagonal - Up-Right
            int fx, tx, fy, ty;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;
            fy = from.y < to.y ? from.y : to.y;
            ty = from.y < to.y ? to.y : from.y;

            int x=fx;
            for(int y = fy; y < ty; y++) {
                bar = new Image(skin.getDrawable("ability-connection-diag-ur-dl"));
                bar.setPosition(600/2+x*32-4, 300/2+y*32-4, Align.bottomLeft);
                connections.addActor(bar);
                x++;
            }
        }

        if(from.y != to.y && from.x != to.x &&
            (from.x < to.x && from.y > to.y) ||
            (from.x > to.x && from.y < to.y)) {

            // Diagonal - Up-Right
            int fx, tx, fy, ty;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;
            fy = from.y > to.y ? from.y : to.y;
            ty = from.y > to.y ? to.y : from.y;

            int x = fx;
            for(int y = fy; y > ty; y--) {
                bar = new Image(skin.getDrawable("ability-connection-diag-ul-dr"));
                bar.setPosition(600/2+x*32-4, 300/2+y*32-32-4, Align.bottomLeft);
                connections.addActor(bar);
                x++;
            }
        }

        if(bar != null) {
            edgeImgs.put(edge,bar);
        }
    }

    private void activateNode(int ID) {
        if(MonsterInfo.getInstance().getStatusInfos().get(team.get(0).ID).learnableAttacks.containsKey(ID)) {
            nodeButtons.get(ID).setStyle(getSkin().get("button-board-node-ability-activated", ImageButton.ImageButtonStyle.class));
        } else {
            nodeButtons.get(ID).setStyle(getSkin().get("button-board-node-activated", ImageButton.ImageButtonStyle.class));
        }
    }

    private void enableNeighboringNodes(int ID) {
        for(AbilityGraph.Edge e : graph.getEdges()) {
            int idToEnable;
            if(e.from.ID == ID || e.to.ID == ID) {
                idToEnable = (ID == e.from.ID) ? e.to.ID : e.from.ID;
                if(!team.get(0).activatedAbilityNodes.contains(idToEnable)) {
                    if(MonsterInfo.getInstance().getStatusInfos().get(team.get(0).ID).learnableAttacks.containsKey(idToEnable)) {
                        nodeButtons.get(idToEnable).setStyle(getSkin().get("button-board-node-ability-enabled", ImageButton.ImageButtonStyle.class));
                    } else {
                        nodeButtons.get(idToEnable).setStyle(getSkin().get("button-board-node-enabled", ImageButton.ImageButtonStyle.class));
                    }
                }

                Drawable drawable;
                switch(e.orientation) {
                    case VERTICAL:
                        drawable = getSkin().getDrawable("ability-connection-vert-active");
                        break;
                    case UPLEFT:
                        drawable = getSkin().getDrawable("ability-connection-diag-ul-dr-active");
                        break;
                    case UPRIGHT:
                        drawable = getSkin().getDrawable("ability-connection-diag-ur-dl-active");
                        break;
                    default:
                        drawable = getSkin().getDrawable("ability-connection-hor-active");
                        break;
                }
                edgeImgs.get(e).setDrawable(drawable);
            }
        }
    }

    private void refreshDisplay() {
        for(int node : team.get(0).activatedAbilityNodes) {
            activateNode(node);
            enableNeighboringNodes(node);
        }
    }
}
