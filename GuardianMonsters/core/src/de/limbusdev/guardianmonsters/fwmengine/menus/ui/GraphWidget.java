package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.AbilityGraph;

/**
 * Created by georg on 27.02.17.
 */

public class GraphWidget extends Group {

    private AbilityGraph graph;
    private Skin skin;

    private ArrayMap<Integer,NodeWidget> nodeWidgets;
    private ArrayMap<AbilityGraph.Vertex,Array<EdgeWidget>> edgeWidgets;


    public GraphWidget(AbilityGraph graph, Skin skin) {
        this.graph = graph;
        this.skin = skin;

        // Create EdgeWidgets from Graph Edges
        edgeWidgets = new ArrayMap<>();
        for(AbilityGraph.Edge edge : graph.getEdges()) {
            EdgeWidget ew = new EdgeWidget(edge);
            ew.setPosition(ew.pivot.x*32, ew.pivot.y*32);
            addActor(ew);

            if(!edgeWidgets.containsKey(edge.from)) edgeWidgets.put(edge.from,new Array<EdgeWidget>());
            if(!edgeWidgets.containsKey(edge.to))   edgeWidgets.put(edge.to,  new Array<EdgeWidget>());
            edgeWidgets.get(edge.from).add(ew);
            edgeWidgets.get(edge.to).add(ew);
        }

        nodeWidgets = new ArrayMap<>();
        for(AbilityGraph.Vertex v : graph.getVertices().values()) {
            NodeWidget nw = new NodeWidget(skin,v, AbilityGraph.NodeType.EMPTY);
            nw.setPosition(nw.node.x*32,nw.node.y*32);
            nodeWidgets.put(v.ID,nw);
            addActor(nw);
        }



        enableEdgesAt(graph.getVertices().get(0));
    }

    public void enableEdgesAt(AbilityGraph.Vertex v) {
        for(EdgeWidget ew : edgeWidgets.get(v)) {
            ew.changeStatus(NodeStatus.ACTIVATED);
        }
    }

    public class NodeWidget extends ImageButton {
        private AbilityGraph.Vertex node;
        private ArrayMap<AbilityGraph.NodeType,ArrayMap<NodeStatus,ImageButtonStyle>> styles;
        private AbilityGraph.NodeType type;

        public NodeWidget(Skin skin, AbilityGraph.Vertex node, AbilityGraph.NodeType type) {
            super(skin,"board-disabled");
            this.type = type;
            // Set Node ImageButton Styles
            styles = new ArrayMap<>();
            styles.put(AbilityGraph.NodeType.EMPTY,new ArrayMap<NodeStatus, ImageButtonStyle>());
            styles.put(AbilityGraph.NodeType.ABILITY,new ArrayMap<NodeStatus, ImageButtonStyle>());
            styles.put(AbilityGraph.NodeType.EQUIPMENT,new ArrayMap<NodeStatus, ImageButtonStyle>());

            styles.get(AbilityGraph.NodeType.EMPTY).put(NodeStatus.DISABLED,  skin.get("board-disabled",  ImageButton.ImageButtonStyle.class));
            styles.get(AbilityGraph.NodeType.EMPTY).put(NodeStatus.ENABLED,   skin.get("board-enabled",   ImageButton.ImageButtonStyle.class));
            styles.get(AbilityGraph.NodeType.EMPTY).put(NodeStatus.ACTIVATED, skin.get("board-activated", ImageButton.ImageButtonStyle.class));

            styles.get(AbilityGraph.NodeType.ABILITY).put(NodeStatus.DISABLED,  skin.get("board-ability-disabled",  ImageButton.ImageButtonStyle.class));
            styles.get(AbilityGraph.NodeType.ABILITY).put(NodeStatus.ENABLED,   skin.get("board-ability-enabled",   ImageButton.ImageButtonStyle.class));
            styles.get(AbilityGraph.NodeType.ABILITY).put(NodeStatus.ACTIVATED, skin.get("board-ability-activated", ImageButton.ImageButtonStyle.class));

            styles.get(AbilityGraph.NodeType.EQUIPMENT).put(NodeStatus.DISABLED,  skin.get("board-equip-disabled",  ImageButton.ImageButtonStyle.class));
            styles.get(AbilityGraph.NodeType.EQUIPMENT).put(NodeStatus.ENABLED,   skin.get("board-equip-enabled",   ImageButton.ImageButtonStyle.class));
            styles.get(AbilityGraph.NodeType.EQUIPMENT).put(NodeStatus.ACTIVATED, skin.get("board-equip-activated", ImageButton.ImageButtonStyle.class));


            setStyle(styles.get(type).get(NodeStatus.DISABLED));
            this.node = node;
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

    public enum NodeStatus {
        DISABLED, ENABLED, ACTIVATED,
    }
}
