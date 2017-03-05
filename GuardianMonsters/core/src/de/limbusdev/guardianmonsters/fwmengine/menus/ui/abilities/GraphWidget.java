package de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities;

import com.badlogic.gdx.graphics.g2d.Animation;
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

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.AnimatedImage;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Created by georg on 27.02.17.
 */

public class GraphWidget extends Group implements Observer {

    private AbilityGraph graph;
    private Skin skin;

    private ButtonGroup nodeGroup;
    private ArrayMap<Integer,NodeWidget> nodeWidgets;
    private ArrayMap<AbilityGraph.Node,Array<EdgeWidget>> edgeWidgets;
    private ArrayMap<AbilityGraph.NodeType,ArrayMap<NodeStatus,ImageButton.ImageButtonStyle>> styles;

    private Controller callbacks;

    private Monster currentMonster;

    private AnimatedImage nodeActivationAnimation;


    public GraphWidget(Skin skin, Controller callbacks) {
        super();
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

        // Animation
        Animation anim = new Animation(.12f,skin.getRegions("node-activation-animation"));
        nodeActivationAnimation = new AnimatedImage(anim);
        nodeActivationAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void init(Monster monster) {
        clear();
        currentMonster = monster;
        this.graph = monster.abilityGraph;
        monster.addObserver(this);

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
        for(AbilityGraph.Node v : graph.getNodes().values()) {
            final int nodeID = v.ID;
            NodeWidget nw = new NodeWidget(skin, v, monster.abilityGraph.nodeTypeAt(nodeID));
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

        enableEdgesAt(graph.getNodes().get(0));

    }

    public void refreshStatus(Monster monster) {
        for(int i : monster.abilityGraph.nodeActive.keys()) {
            if(monster.abilityGraph.nodeActive.get(i)) {
                nodeWidgets.get(i).changeStatus(NodeStatus.ACTIVE);
                enableEdgesAt(graph.getNodes().get(i));
                enableNeighborNodes(graph.getNodes().get(i));
            }
        }
    }



    // ..................................................................................... METHODS

    public void enableEdgesAt(AbilityGraph.Node v) {
        for(EdgeWidget ew : edgeWidgets.get(v)) {
            ew.changeStatus(NodeStatus.ACTIVE);
        }
    }

    public void enableNeighborNodes(AbilityGraph.Node v) {
        for(AbilityGraph.Edge e : graph.getEdges()) {
            if(e.from == v || e.to == v) {
                AbilityGraph.Node nodeToBeEnabled = e.from == v ? e.to : e.from;
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
        private AbilityGraph.Node node;
        private AbilityGraph.NodeType type;
        public NodeStatus status;
        private IntVec2 offset;

        public NodeWidget(Skin skin, AbilityGraph.Node node, AbilityGraph.NodeType type) {
            super(skin,"board-" + type.toString().toLowerCase() + "-disabled");
            this.type = type;
            this.node = node;
            status = NodeStatus.DISABLED;
            switch(type) {
                case ABILITY:
                case METAMORPHOSIS:
                case EQUIPMENT: offset = new IntVec2(-16,-16); break;
                default: offset = new IntVec2(-8,-8); break;
            }
        }

        private void playActivationAnimation() {
            if(offset != null) {
                nodeActivationAnimation.setPosition(-32 - offset.x, -32 - offset.y, Align.bottomLeft);
                this.addActor(nodeActivationAnimation);
            }
        }

        public void changeStatus(NodeStatus status) {
            if(NodeStatus.ACTIVE == status && status != this.status) {
                playActivationAnimation();
            }
            this.setStyle(styles.get(type).get(status));
            this.status = status;
        }

        @Override
        public void setPosition(float x, float y, int alignment) {
            super.setPosition(x+offset.x, y+offset.y, alignment);
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x+offset.x, y+offset.y);
        }
    }

    public class EdgeWidget extends Group {
        private ArrayMap<AbilityGraph.Orientation,String> edgeImgsDisabled;
        private AbilityGraph.Edge edge;
        private Array<Image> images;
        public AbilityGraph.Node pivot;

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

    public interface Controller {
        /**
         * Defines what happens, when a node in the Graph is clicked
         * @param nodeID    ID of the clicked graph node
         */
        void onNodeClicked(int nodeID);
    }
}
