package de.limbusdev.guardianmonsters.guardians.abilities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription;

/**
 * @author Georg Eckert 2017
 */

public class AbilityGraph implements IAbilityGraph
{
    private AGuardian core;

    private ArrayMap<Integer, Node> nodes;
    private Array<Edge> edges;

    private ArrayMap<Integer,Ability.aID> abilityNodes;
    private ArrayMap<Integer,BodyPart> equipmentNodes;
    private Array<Integer> metamorphosisNodes;

    private ArrayMap<Integer, Ability.aID> activeAbilities; // Abilities available in battle
    private ArrayMap<Integer, Ability.aID> learntAbilities; // Abilities activated on the graph
    private Array<BodyPart> learntEquipment;

    public AbilityGraph(AGuardian core, SpeciesDescription data)
    {
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

        init(core, data);
    }

    public AbilityGraph(
            ArrayMap<Integer, Node> nodes,
            Array<Edge> edges,
            ArrayMap<Integer, Ability.aID> abilityNodes,
            ArrayMap<Integer, BodyPart> equipmentNodes,
            Array<Integer> metamorphosisNodes,
            ArrayMap<Integer, Ability.aID> activeAbilities,
            ArrayMap<Integer, Ability.aID> learntAbilities,
            Array<BodyPart> learntEquipment)
    {
        this.nodes = nodes;
        this.edges = edges;
        this.abilityNodes = abilityNodes;
        this.equipmentNodes = equipmentNodes;
        this.metamorphosisNodes = metamorphosisNodes;
        this.activeAbilities = activeAbilities;
        this.learntAbilities = learntAbilities;
        this.learntEquipment = learntEquipment;
    }

    private void init(AGuardian core, SpeciesDescription data)
    {
        this.core = core;

        for(int key : data.getAbilityNodes().keys())
        {
            nodes.get(key).type = Node.Type.ABILITY;
            abilityNodes.put(key, data.getAbilityNodes().get(key));
        }

        for(int key : data.getEquipmentNodes().keys())
        {
            nodes.get(key).type = Node.Type.EQUIPMENT;
            equipmentNodes.put(key, data.getEquipmentNodes().get(key));
        }

        for(int key : data.getMetamorphosisNodes())
        {
            nodes.get(key).type = Node.Type.METAMORPHOSIS;
            metamorphosisNodes.add(key);
        }

        int counter = 0;
        for(Ability.aID a : learntAbilities.values())
        {
            activeAbilities.put(counter, a);
            counter++;
        }

        activateNode(0);
    }


    // ........................................................................... GETTERS & SETTERS
    @Override
    public ArrayMap<Integer, Node> getNodes() {
        return nodes;
    }

    @Override
    public Array<Edge> getEdges() {
        return edges;
    }

    @Override
    public ArrayMap<Integer, Ability.aID> getAbilityNodes()
    {
        return abilityNodes;
    }

    @Override
    public ArrayMap<Integer, BodyPart> getEquipmentNodes()
    {
        return equipmentNodes;
    }

    @Override
    public Array<Integer> getMetamorphosisNodes()
    {
        return metamorphosisNodes;
    }

    @Override
    public void activateNode(int nodeID)
    {
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

        core.setAbilitiesChanged();
        core.notifyObservers();
    }

    @Override
    public boolean isNodeEnabled(int nodeID) {
        return nodes.get(nodeID).isEnabled();
    }

    @Override
    public boolean learnsAbilityAt(int nodeID) {
        return abilityNodes.containsKey(nodeID);
    }

    @Override
    public boolean learnsEquipmentAt(int nodeID) {
        return equipmentNodes.containsKey(nodeID);
    }

    public boolean metamorphsAt(int nodeID) {
        return metamorphosisNodes.contains(nodeID, false);
    }

    @Override
    public int getCurrentForm()
    {
        int activatedMetamorphosisNodes = 0;
        for(int key : metamorphosisNodes)
        {
            if(isNodeEnabled(key)) activatedMetamorphosisNodes++;
        }
        return activatedMetamorphosisNodes;
    }

    @Override
    public boolean learnsSomethingAt(int nodeID)
    {
        return (learnsAbilityAt(nodeID) || learnsEquipmentAt(nodeID) || metamorphsAt(nodeID));
    }

    @Override
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

    @Override
    public boolean hasLearntEquipment(BodyPart bodyPart) {
        if(learntEquipment.contains(bodyPart,true)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Ability.aID getActiveAbility(int abilitySlot)
    {
        return activeAbilities.get(abilitySlot);
    }

    @Override
    public Ability.aID getRandomActiveAbility()
    {
        return getActiveAbility(MathUtils.random(0, activeAbilities.size-1));
    }

    @Override
    public void setActiveAbility(int slot, int learntAbilityNumber)
    {
        Ability.aID abilityToLearn = learntAbilities.get(learntAbilityNumber);
        if(abilityToLearn == null) return;

        for(int key : activeAbilities.keys()) {
            Ability.aID abilityAtThisSlot = activeAbilities.get(key);

            if(abilityAtThisSlot != null) {
                if (abilityAtThisSlot.equals(abilityToLearn)) {
                    activeAbilities.put(key, null);
                }
            }
        }
        activeAbilities.put(slot, learntAbilities.get(learntAbilityNumber));

        core.setAbilitiesChanged();
        core.notifyObservers();
    }

    @Override
    public ArrayMap<Integer, Ability.aID> getActiveAbilities() {
        return activeAbilities;
    }


    // .............................................................................. HELPER METHODS
    private boolean learnAbility(int nodeID)
    {
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
}
