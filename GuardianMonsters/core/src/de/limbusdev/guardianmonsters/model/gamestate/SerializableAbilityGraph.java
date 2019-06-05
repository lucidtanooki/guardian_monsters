package de.limbusdev.guardianmonsters.model.gamestate;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.abilities.Edge;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;
import de.limbusdev.guardianmonsters.guardians.abilities.Node;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;

/**
 * SerializableAbilityGraph
 *
 * @author Georg Eckert 2017
 */

public class SerializableAbilityGraph {

    public SNode[] nodes;
    public SEdge[] edges;

    public int[] activeAbilities;
    public SAbilityNode[] abilityNodes;
    public SEquipmentNode[] equipmentNodes;

    public SerializableAbilityGraph() {}

    public SerializableAbilityGraph(IAbilityGraph graph) {
        SerializableAbilityGraph serAbilGraph = new SerializableAbilityGraph();

        nodes = new SNode[graph.getNodes().size];
        edges = new SEdge[graph.getEdges().size];

        activeAbilities = new int[graph.getActiveAbilities().size];
        abilityNodes = new SAbilityNode[graph.getAbilityNodes().size];
        equipmentNodes = new SEquipmentNode[graph.getEquipmentNodes().size];

        for(int key : graph.getNodes().keys()) {
            nodes[key] = new SNode(graph.getNodes().get(key));
        }

        for(int key=0; key<graph.getEdges().size; key++) {
            edges[key] = new SEdge(graph.getEdges().get(key));
        }

        int counter=0;
        for(int key :graph.getActiveAbilities().keys()) {
            if(graph.getActiveAbilities().get(key) != null) {
                activeAbilities[counter] = graph.getActiveAbilities().get(key).ID;
                counter++;
            }
        }

        counter=0;
        for(int key : graph.getAbilityNodes().keys()) {
            abilityNodes[counter] = new SAbilityNode(key, new SerializableAbility(graph.getAbilityNodes().get(key)));
            counter++;
        }

        counter = 0;
        for(int key : graph.getEquipmentNodes().keys()) {
            equipmentNodes[counter] = new SEquipmentNode(key, graph.getEquipmentNodes().get(key));
            counter++;
        }
    }

    public static AbilityGraph deserialize(SerializableAbilityGraph graph) {
        ArrayMap<Integer,Node> nodes = new ArrayMap<>();
        for(SNode sNode : graph.nodes) {
            nodes.put(sNode.ID, SNode.deserialize(sNode));
        }

        Array<Edge> edges = new Array<>();
        for(SEdge sEdge : graph.edges) {
            edges.add(graph.deserialize(sEdge));
        }

        ArrayMap<Integer,Ability.aID> abilityNodes = new ArrayMap<>();
        for(SAbilityNode sAbilityNode : graph.abilityNodes) {
            abilityNodes.put(sAbilityNode.ID, SerializableAbility.deserialize(sAbilityNode.ability));
        }

        ArrayMap<Integer,BodyPart> equipmentNodes = new ArrayMap<>();
        for(SEquipmentNode sEquipmentNode : graph.equipmentNodes) {
            equipmentNodes.put(sEquipmentNode.ID, sEquipmentNode.part);
        }

        Array<Integer> metaNodes = new Array<>();
        for(SNode sNode : graph.nodes) {
            if(sNode.type == METAMORPHOSIS) {
                metaNodes.add(sNode.ID);
            }
        }

        ArrayMap<Integer,Ability.aID> activeAbilities = new ArrayMap<>();
        for(int i : graph.activeAbilities) {
            activeAbilities.put(i,SerializableAbility.deserialize(graph.abilityNodes[i].ability));
        }

        ArrayMap<Integer,Ability.aID> learntAbilities = new ArrayMap<>();
        for(int key : abilityNodes.keys()) {
            if(nodes.get(key).isActive()) {
                learntAbilities.put(key,abilityNodes.get(key));
            }
        }

        Array<BodyPart> learntEquipment = new Array<>();
        for(int key : equipmentNodes.keys()) {
            if(nodes.get(key).isActive()) {
                learntEquipment.add(equipmentNodes.get(key));
            }
        }

        AbilityGraph abilityGraph = new AbilityGraph(
            nodes,
            edges,
            abilityNodes,
            equipmentNodes,
            metaNodes,
            activeAbilities,
            learntAbilities,
            learntEquipment
        );

        return abilityGraph;
    }

    public Edge deserialize(SEdge sedge) {
        Node from = SNode.deserialize(nodes[sedge.from]);
        Node to = SNode.deserialize(nodes[sedge.to]);
        Edge edge = new Edge(from, to);
        return edge;
    }

    public static final int EMPTY=0, ABILITY=1, EQUIPMENT=2, METAMORPHOSIS=3;
    public static final int DISABLED=0, ENABLED=1, ACTIVE=2;

    public static class SNode {
        public int ID, x, y, type, state;

        public SNode() {}

        public SNode(Node node) {
            this.ID = node.getID();
            this.x = node.getX();
            this.y = node.getY();
            switch(node.getState()) {
                case ACTIVE:    this.state = ACTIVE;    break;
                case ENABLED:   this.state = ENABLED;   break;
                default:        this.state = DISABLED;  break;
            }
            switch(node.getType()) {
                case ABILITY:       this.type = ABILITY;        break;
                case EQUIPMENT:     this.type = EQUIPMENT;      break;
                case METAMORPHOSIS: this.type = METAMORPHOSIS;  break;
                default:            this.type = EMPTY;          break;
            }
        }

        public static Node deserialize(SNode snode) {
            Node.Type type;
            switch(snode.type) {
                case ABILITY:       type = Node.Type.ABILITY;       break;
                case EQUIPMENT:     type = Node.Type.EQUIPMENT;     break;
                case METAMORPHOSIS: type = Node.Type.METAMORPHOSIS; break;
                default:            type = Node.Type.EMPTY;         break;
            }
            Node.State state;
            switch (snode.state) {
                case ENABLED:   state = Node.State.ENABLED;     break;
                case ACTIVE:    state = Node.State.ACTIVE;      break;
                default:        state = Node.State.DISABLED;    break;
            }
            Node node = new Node(snode.x, snode.y, snode.ID, type, state);
            return node;
        }
    }

    public static class SEdge {
        public int from, to;

        public SEdge() {}

        public SEdge(Edge edge) {
            this.from = edge.getFrom().getID();
            this.to   = edge.getTo().getID();
        }
    }

    public static class SAbilityNode {
        public int ID;
        public SerializableAbility ability;

        public SAbilityNode() {}

        public SAbilityNode(int ID, SerializableAbility ability) {
            this.ID = ID;
            this.ability = ability;
        }
    }

    public static class SEquipmentNode {
        public int ID;
        public BodyPart part;

        public SEquipmentNode() {}

        public SEquipmentNode(int nodeID, BodyPart part) {
            this.ID = nodeID;
            this.part = part;
        }
    }

}
