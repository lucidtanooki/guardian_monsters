package de.limbusdev.guardianmonsters.guardians.abilities;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.monsters.MonsterData;

/**
 * @author Georg Eckert 2017
 */

public class AbilityGraph extends Signal<AbilityGraph> {

    private final static int X=0, Y=1;

    private ArrayMap<Integer, Node> nodes;
    private Array<Edge> edges;

    public ArrayMap<Integer,Ability> abilityNodes;
    public ArrayMap<Integer,BodyPart> equipmentNodes;
    public Array<Integer> metamorphosisNodes;

    private ArrayMap<Integer, Ability> activeAbilities;
    public ArrayMap<Integer,Ability> learntAbilities;
    public Array<BodyPart> learntEquipment;

    public AbilityGraph(MonsterData data) {
        nodes = new ArrayMap<>();
        edges = new Array<>();
        abilityNodes = new ArrayMap<>();
        equipmentNodes = new ArrayMap<>();
        learntAbilities = new ArrayMap<>();
        learntEquipment = new Array<>();
        metamorphosisNodes = new Array<>();

        for(int i = 0; i < GraphTemplate.coords.length; i++) {
            int v[] = GraphTemplate.coords[i];
            nodes.put(i, new Node(v[X+1], v[Y+1], i));
        }

        for(int i = 0; i < GraphTemplate.conns.length; i++) {
            int e[] = GraphTemplate.conns[i];
            edges.add(new Edge(nodes.get(e[X]), nodes.get(e[Y])));
        }

        activeAbilities = new ArrayMap<>();
        for(int i=0; i<7; i++) {
            activeAbilities.put(i,null);
        }

        init(data);

    }

    private void init(MonsterData data) {
        for(int key : data.getAbilityNodes().keys()) {
            nodes.get(key).type = Node.Type.ABILITY;
            abilityNodes.put(key, data.getAbilityNodes().get(key));
        }
        for(int key : data.getEquipmentNodes().keys()) {
            nodes.get(key).type = Node.Type.EQUIPMENT;
            equipmentNodes.put(key, data.getEquipmentNodes().get(key));
        }
        for(int key : data.getMetamorphosisNodes()) {
            nodes.get(key).type = Node.Type.METAMORPHOSIS;
            metamorphosisNodes.add(key);
        }

        int counter = 0;
        for(Ability a : learntAbilities.values()) {
            activeAbilities.put(counter, a);
            counter++;
        }

        activateNode(0);
    }

    public AbilityGraph(ArrayMap<Integer, Node> nodes, Array<Edge> edges, ArrayMap<Integer, Ability> abilityNodes, ArrayMap<Integer, BodyPart> equipmentNodes, Array<Integer> metamorphosisNodes, ArrayMap<Integer, Ability> activeAbilities, ArrayMap<Integer, Ability> learntAbilities, Array<BodyPart> learntEquipment) {
        this.nodes = nodes;
        this.edges = edges;
        this.abilityNodes = abilityNodes;
        this.equipmentNodes = equipmentNodes;
        this.metamorphosisNodes = metamorphosisNodes;
        this.activeAbilities = activeAbilities;
        this.learntAbilities = learntAbilities;
        this.learntEquipment = learntEquipment;
    }

    // ........................................................................... GETTERS & SETTERS
    public ArrayMap<Integer, Node> getNodes() {
        return nodes;
    }

    public Array<Edge> getEdges() {
        return edges;
    }


    /**
     * Sets the state of the given node to ACTIVE and learns the
     * ability at that node
     * @param nodeID
     */
    public void activateNode(int nodeID) {
        Node node = nodes.get(nodeID);

        node.activate();
        enableNeighborNodes(nodeID);

        switch(node.type) {
            case ABILITY:
                learnAbility(nodeID);
                break;
            case EQUIPMENT:
                learnEquipment(nodeID);
                break;
            default:
                break;
        }

        dispatch(this);
    }

    public boolean isNodeEnabled(int nodeID) {
        return nodes.get(nodeID).isEnabled();
    }

    /**
     * Wether this monster learns an attack or other ability at this node
     * @param nodeID
     * @return
     */
    public boolean learnsAbilityAt(int nodeID) {
        return abilityNodes.containsKey(nodeID);
    }

    /**
     * Wether this monster learns to carry some kind of equipment at this node
     * @param nodeID
     * @return
     */
    public boolean learnsEquipmentAt(int nodeID) {
        return equipmentNodes.containsKey(nodeID);
    }

    public boolean metamorphsAt(int nodeID) {
        return metamorphosisNodes.contains(nodeID, false);
    }

    /**
     * Wether this monster learns an ability or to carry equipment at this node
     * @param nodeID
     * @return
     */
    public boolean learnsSomethingAt(int nodeID) {
        return (learnsAbilityAt(nodeID) || learnsEquipmentAt(nodeID) || metamorphsAt(nodeID));
    }

    public Node.Type nodeTypeAt(int nodeID) {
        if(learnsAbilityAt(nodeID)) {
            return Node.Type.ABILITY;
        }
        if(learnsEquipmentAt(nodeID)) {
            return Node.Type.EQUIPMENT;
        }
        if(metamorphsAt(nodeID)) {
            return Node.Type.METAMORPHOSIS;
        }
        return Node.Type.EMPTY;
    }

    /**
     * Finds out if the ability to carry a specific equipment has already been learnt
     * @param bodyPart  the body part in question
     * @return          if it is already possible to carry such equipment
     */
    public boolean hasLearntEquipment(BodyPart bodyPart) {
        if(learntEquipment.contains(bodyPart,true)) {
            return true;
        } else {
            return false;
        }
    }


    // .............................................................................. HELPER METHODS
    private boolean learnAbility(int nodeID) {
        if(learnsAbilityAt(nodeID)) {
            learntAbilities.put(nodeID, abilityNodes.get(nodeID));
            return true;
        } else {
            return false;
        }
    }

    private boolean learnEquipment(int nodeID) {
        if(learnsEquipmentAt(nodeID)) {
            learntEquipment.add(equipmentNodes.get(nodeID));
            return true;
        } else {
            return false;
        }
    }

    private void enableNeighborNodes(int nodeID) {
        for (Edge e : edges) {
            if (e.from.ID == nodeID || e.to.ID == nodeID) {
                int enableID = e.from.ID == nodeID ? e.to.ID : e.from.ID;
                nodes.get(enableID).enable();
            }
        }
    }

    /**
     * returns the ability placed at the given slot
     * @param abilitySlot   slot for in battle ability usage
     * @return              ability which resides there
     */
    public Ability getActiveAbility(int abilitySlot) {
        return activeAbilities.get(abilitySlot);
    }

    /**
     * Puts an ability into one of seven slots, available in battle
     * @param slot                  where the ability should be placed in battle
     * @param learntAbilityNumber   number of ability to be placed there
     */
    public void setActiveAbility(int slot, int learntAbilityNumber) {
        Ability abilityToLearn = learntAbilities.get(learntAbilityNumber);
        if(abilityToLearn == null) return;

        for(int key : activeAbilities.keys()) {
            Ability abilityAtThisSlot = activeAbilities.get(key);

            if(abilityAtThisSlot != null) {
                if (abilityAtThisSlot.equals(abilityToLearn)) {
                    activeAbilities.put(key, null);
                }
            }
        }
        activeAbilities.put(slot, learntAbilities.get(learntAbilityNumber));

        dispatch(this);
    }

    public ArrayMap<Integer, Ability> getActiveAbilities() {
        return activeAbilities;
    }
}
