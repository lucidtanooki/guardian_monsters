package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Ability;
import de.limbusdev.guardianmonsters.model.Equipment;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;
import de.limbusdev.guardianmonsters.model.MonsterStatusInformation;

/**
 * Created by georg on 27.02.17.
 */

public class GraphWidget extends Group implements Observer {

    private AbilityGraph graph;
    private Skin skin;

    private ButtonGroup nodeGroup;
    private ArrayMap<Integer,NodeWidget> nodeWidgets;
    private ArrayMap<AbilityGraph.Vertex,Array<EdgeWidget>> edgeWidgets;
    private ArrayMap<AbilityGraph.NodeType,ArrayMap<NodeStatus,ImageButton.ImageButtonStyle>> styles;

    private CallbackHandler callbacks;

    private Monster currentMonster;


    public GraphWidget(AbilityGraph graph, Skin skin, CallbackHandler callbacks) {
        super();
        this.graph = graph;
        this.skin = skin;
        this.callbacks = callbacks;
        setupNodeStyles();

        setSize(600,300);

        // Create EdgeWidgets from Graph Edges
        edgeWidgets = new ArrayMap<>();

        // Create NodeWidgets from Graph Nodes
        nodeGroup = new ButtonGroup();
        nodeGroup.setMinCheckCount(1);
        nodeGroup.setMaxCheckCount(1);
        nodeWidgets = new ArrayMap<>();

    }

    public void init(Monster monster) {
        clear();
        currentMonster = monster;
        monster.addObserver(this);

        MonsterStatusInformation msi = MonsterInfo.getInstance().getStatusInfos().get(monster.ID);
        ArrayMap<Integer,Equipment.EQUIPMENT_TYPE> equipments = msi.equipmentAbilityGraphIds;
        ArrayMap<Integer,Ability> abilities = msi.attackAbilityGraphIds;

        edgeWidgets.clear();
        for(AbilityGraph.Edge edge : graph.getEdges()) {
            EdgeWidget ew = new EdgeWidget(edge);
            ew.setPosition(ew.pivot.x*32+300, ew.pivot.y*32+150);
            addActor(ew);

            if(!edgeWidgets.containsKey(edge.from)) edgeWidgets.put(edge.from,new Array<EdgeWidget>());
            if(!edgeWidgets.containsKey(edge.to))   edgeWidgets.put(edge.to,  new Array<EdgeWidget>());
            edgeWidgets.get(edge.from).add(ew);
            edgeWidgets.get(edge.to).add(ew);
        }

        nodeWidgets.clear();
        nodeGroup.clear();
        for(AbilityGraph.Vertex v : graph.getVertices().values()) {
            NodeWidget nw;
            final int nodeID = v.ID;
            if(equipments.containsKey(v.ID) || abilities.containsKey(v.ID)) {
                if(equipments.containsKey(v.ID)) {
                    nw = new NodeWidget(skin, v, AbilityGraph.NodeType.EQUIPMENT);
                } else {
                    nw = new NodeWidget(skin, v, AbilityGraph.NodeType.ABILITY);
                }
            } else {
                nw = new NodeWidget(skin, v, AbilityGraph.NodeType.EMPTY);
            }
            nw.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onNodeClicked(nodeID);
                }
            });
            nw.setPosition(nw.node.x*32+300,nw.node.y*32+150);
            nodeWidgets.put(v.ID,nw);
            addActor(nw);
            nodeGroup.add(nw);
        }
        nodeWidgets.get(0).setChecked(true);
        refreshStatus(monster);

        enableEdgesAt(graph.getVertices().get(0));

    }

    public void refreshStatus(Monster monster) {
        for(int i : monster.abilityNodeStatus.keys()) {
            if(monster.abilityNodeStatus.get(i)) {
                nodeWidgets.get(i).changeStatus(NodeStatus.ACTIVE);
                enableEdgesAt(graph.getVertices().get(i));
                enableNeighborNodes(graph.getVertices().get(i));
            }
        }
    }



    // ..................................................................................... METHODS

    public void enableEdgesAt(AbilityGraph.Vertex v) {
        for(EdgeWidget ew : edgeWidgets.get(v)) {
            ew.changeStatus(NodeStatus.ACTIVE);
        }
    }

    public void enableNeighborNodes(AbilityGraph.Vertex v) {
        for(AbilityGraph.Edge e : graph.getEdges()) {
            if(e.from == v || e.to == v) {
                AbilityGraph.Vertex nodeToBeEnabled = e.from == v ? e.to : e.from;
                NodeWidget nw = nodeWidgets.get(nodeToBeEnabled.ID);
                if(!(nw.status == NodeStatus.ACTIVE)) nw.changeStatus(NodeStatus.ENABLED);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof Monster) {
            Monster m = (Monster)o;
            if(m.equals(currentMonster)) {
                refreshStatus(m);
            }
        }
    }

    public class NodeWidget extends ImageButton {
        private AbilityGraph.Vertex node;
        private AbilityGraph.NodeType type;
        public NodeStatus status;

        public NodeWidget(Skin skin, AbilityGraph.Vertex node, AbilityGraph.NodeType type) {
            super(skin,"board-" + type.toString().toLowerCase() + "-disabled");
            this.type = type;
            this.node = node;
            status = NodeStatus.DISABLED;
        }

        public void changeStatus(NodeStatus status) {
            this.setStyle(styles.get(type).get(status));
            this.status = status;
        }

        @Override
        public void setPosition(float x, float y, int alignment) {
            IntVec2 offset;
            switch(type) {
                case ABILITY:
                case EQUIPMENT: offset = new IntVec2(-16,-16); break;
                default: offset = new IntVec2(-8,-8); break;
            }
            super.setPosition(x+offset.x, y+offset.y, alignment);
        }

        @Override
        public void setPosition(float x, float y) {
            IntVec2 offset;
            switch(type) {
                case ABILITY:
                case EQUIPMENT: offset = new IntVec2(-16,-16); break;
                default: offset = new IntVec2(-8,-8); break;
            }
            super.setPosition(x+offset.x, y+offset.y);
        }
    }

    public class EdgeWidget extends Group {
        private ArrayMap<AbilityGraph.Orientation,String> edgeImgsDisabled;
        private AbilityGraph.Edge edge;
        private Array<Image> images;
        public AbilityGraph.Vertex pivot;

        public EdgeWidget(AbilityGraph.Edge edge) {
            super();
            // Set Edge Image Names
            edgeImgsDisabled = new ArrayMap<>();
            edgeImgsDisabled.put(AbilityGraph.Orientation.HORIZONTAL, "graph-horizontal");
            edgeImgsDisabled.put(AbilityGraph.Orientation.VERTICAL, "graph-vertical");
            edgeImgsDisabled.put(AbilityGraph.Orientation.UPRIGHT, "graph-upright");
            edgeImgsDisabled.put(AbilityGraph.Orientation.UPLEFT, "graph-upleft");
            this.edge = edge;
            images = new Array<>();

            switch(edge.orientation) {
                case VERTICAL: assembleVerticalEdge(edge); break;
                case UPLEFT: assembleUpLeftEdge(edge); break;
                case UPRIGHT: assembleUpRightEdge(edge); break;
                default: assembleHorizontalEdge(edge); break;
            }
        }

        private void assembleHorizontalEdge(AbilityGraph.Edge edge) {
            pivot = edge.from.x < edge.to.x ? edge.from : edge.to;
            for(int x = 0; x < edge.getXLength(); x++) {
                Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(AbilityGraph.Orientation.HORIZONTAL)));
                img.setPosition(x*32,-3, Align.bottomLeft);
                addActor(img);
                images.add(img);
            }
        }

        private void assembleVerticalEdge(AbilityGraph.Edge edge) {
            pivot = edge.from.y < edge.to.y ? edge.from : edge.to;
            for(int y = 0; y < edge.getYLength(); y++) {
                Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(AbilityGraph.Orientation.VERTICAL)));
                img.setPosition(-3,y*32, Align.bottomLeft);
                addActor(img);
                images.add(img);
            }
        }

        private void assembleUpLeftEdge(AbilityGraph.Edge edge) {
            pivot = edge.from.y < edge.to.y ? edge.from : edge.to;
            int x=0; int y=0;
            do {
                Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(AbilityGraph.Orientation.UPLEFT)));
                img.setPosition(x*32+4,y*32-4,Align.bottomRight);
                addActor(img);
                images.add(img);
                x--;y++;
            } while (y < edge.getYLength());
        }

        private void assembleUpRightEdge(AbilityGraph.Edge edge) {
            pivot = edge.from.y < edge.to.y ? edge.from : edge.to;
            int x=0; int y=0;
            do {
                Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(AbilityGraph.Orientation.UPRIGHT)));
                img.setPosition(x*32-4,y*32-4,Align.bottomLeft);
                addActor(img);
                images.add(img);
                x++;y++;
            } while (y < edge.getYLength());
        }

        public void changeStatus(NodeStatus status) {
            String ending = status == NodeStatus.DISABLED ? "" : "-active";
            for(Image i : images) {
                i.setDrawable(skin.getDrawable(edgeImgsDisabled.get(edge.orientation) + ending));
            }
        }


    }

    private void setupNodeStyles() {
        // Set Node ImageButton Styles
        styles = new ArrayMap<>();
        for(AbilityGraph.NodeType t : AbilityGraph.NodeType.values()) {
            styles.put(t,new ArrayMap<NodeStatus, ImageButton.ImageButtonStyle>());
            ArrayMap<NodeStatus, ImageButton.ImageButtonStyle> statusStyles = styles.get(t);

            for(NodeStatus s : NodeStatus.values()) {
                // Assembles Styles like    "board-" + "empty" +                    "-" + "disabled"
                statusStyles.put(s,skin.get("board-" + t.toString().toLowerCase() + "-" + s.toString().toLowerCase(),ImageButton.ImageButtonStyle.class));
            }
        }
    }

    public enum NodeStatus {
        DISABLED, ENABLED, ACTIVE,
    }

    public interface CallbackHandler {
        void onNodeClicked(int nodeID);
    }
}
