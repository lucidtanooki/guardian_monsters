package de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.abilities.Edge;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;
import de.limbusdev.guardianmonsters.guardians.abilities.Node;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * @author Georg Eckert 2017
 */

public class GraphWidget extends Group {

    private Controller callbacks;

    private IAbilityGraph graph;
    private Skin skin;

    private ButtonGroup nodeGroup;
    private ArrayMap<Integer, NodeWidget> nodeWidgets;
    private ArrayMap<Node,Array<EdgeWidget>> edgeWidgets;

    private AGuardian currentGuardian;

    public GraphWidget(Skin skin, Controller callbacks) {

        super();
        this.skin = skin;
        this.callbacks = callbacks;

        setSize(600,300);

        // Create NodeWidgets from Graph Nodes
        nodeGroup = new ButtonGroup();
        nodeGroup.setMinCheckCount(1);
        nodeGroup.setMaxCheckCount(1);
        nodeWidgets = new ArrayMap<>();

        // Create EdgeWidgets from Graph Edges
        edgeWidgets = new ArrayMap<>();
    }



    public void init(AGuardian guardian) {

        clear();
        currentGuardian = guardian;
        this.graph = guardian.getAbilityGraph();

        edgeWidgets.clear();
        for(Edge edge : graph.getEdges()) {
            addNewEdgeWidget(edge);
        }

        nodeWidgets.clear();
        nodeGroup.clear();
        for(Node node : graph.getNodes().values()) {
            addNewNodeWidget(node);
        }
        nodeWidgets.get(0).setChecked(true);
    }


    // ..................................................................................... METHODS
    private void addNewNodeWidget(Node node) {

        final int nodeID = node.getID();
        NodeWidget nw = new NodeWidget(skin, node);
        nw.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onNodeClicked(nodeID);
            }
        });
        nw.setPosition(nw.getNode().getX()*32+300,nw.getNode().getY()*32+150);
        nodeWidgets.put(node.getID(),nw);
        addActor(nw);
        nodeGroup.add(nw);
    }

    private void addNewEdgeWidget(Edge edge) {

        EdgeWidget ew = new EdgeWidget(skin, edge);
        ew.setPosition(ew.pivot.getX()*32+300, ew.pivot.getY()*32+150);
        addActor(ew);

        if(!edgeWidgets.containsKey(edge.getFrom())) {
            edgeWidgets.put(edge.getFrom(),new Array<>());
        }
        if(!edgeWidgets.containsKey(edge.getTo())) {
            edgeWidgets.put(edge.getTo(),  new Array<>());
        }

        edgeWidgets.get(edge.getFrom()).add(ew);
        edgeWidgets.get(edge.getTo()).add(ew);
    }

    public interface Controller {
        /**
         * Defines what happens, when a node in the Graph is clicked
         * @param nodeID    ID of the clicked graph node
         */
        void onNodeClicked(int nodeID);
    }
}
