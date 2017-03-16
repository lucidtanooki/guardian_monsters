package de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.model.abilities.Edge;
import de.limbusdev.guardianmonsters.model.abilities.Node;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * @author Georg Eckert 2017
 */

public class GraphWidget extends Group {

    private Controller callbacks;

    private AbilityGraph graph;
    private Skin skin;

    private ButtonGroup nodeGroup;
    private ArrayMap<Integer,NodeWidget> nodeWidgets;
    private ArrayMap<Node,Array<EdgeWidget>> edgeWidgets;

    private Monster currentMonster;


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



    public void init(Monster monster) {
        clear();
        currentMonster = monster;
        this.graph = monster.abilityGraph;

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
        NodeWidget nw = new NodeWidget(skin, node);
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
        EdgeWidget ew = new EdgeWidget(skin, edge);
        ew.setPosition(ew.pivot.x*32+300, ew.pivot.y*32+150);
        addActor(ew);

        if(!edgeWidgets.containsKey(edge.from)) {
            edgeWidgets.put(edge.from,new Array<EdgeWidget>());
        }
        if(!edgeWidgets.containsKey(edge.to)) {
            edgeWidgets.put(edge.to,  new Array<EdgeWidget>());
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
