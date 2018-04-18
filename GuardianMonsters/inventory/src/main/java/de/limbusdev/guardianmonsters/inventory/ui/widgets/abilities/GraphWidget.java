package main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities;

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
    private ArrayMap<Integer, main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.NodeWidget> nodeWidgets;
    private ArrayMap<Node,Array<main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.EdgeWidget>> edgeWidgets;

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
        final int nodeID = node.ID;
        main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.NodeWidget nw = new main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.NodeWidget(skin, node);
        nw.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onNodeClicked(nodeID);
            }
        });
        nw.setPosition(nw.getNode().x*32+300,nw.getNode().y*32+150);
        nodeWidgets.put(node.ID,nw);
        addActor(nw);
        nodeGroup.add(nw);
    }

    private void addNewEdgeWidget(Edge edge) {
        main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.EdgeWidget ew = new main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.EdgeWidget(skin, edge);
        ew.setPosition(ew.pivot.x*32+300, ew.pivot.y*32+150);
        addActor(ew);

        if(!edgeWidgets.containsKey(edge.from)) {
            edgeWidgets.put(edge.from,new Array<main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.EdgeWidget>());
        }
        if(!edgeWidgets.containsKey(edge.to)) {
            edgeWidgets.put(edge.to,  new Array<main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities.EdgeWidget>());
        }

        edgeWidgets.get(edge.from).add(ew);
        edgeWidgets.get(edge.to).add(ew);
    }

    public interface Controller {
        /**
         * Defines what happens, when a node in the Graph is clicked
         * @param nodeID    ID of the clicked graph node
         */
        void onNodeClicked(int nodeID);
    }
}
