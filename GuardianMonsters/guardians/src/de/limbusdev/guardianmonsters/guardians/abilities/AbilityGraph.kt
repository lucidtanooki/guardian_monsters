package de.limbusdev.guardianmonsters.guardians.abilities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription
import de.limbusdev.utils.extensions.set

/**
 * @author Georg Eckert 2019
 */

class AbilityGraph : IAbilityGraph
{
    // .................................................................................. Properties
    private var core:               AGuardian? = null

    private var nodes:              ArrayMap<Int, Node>
    private var edges:              Array<Edge>

    private var abilityNodes:       ArrayMap<Int, Ability.aID>
    private var equipmentNodes:     ArrayMap<Int, BodyPart>
    private var metamorphosisNodes: Array<Int>

    private var activeAbilities:    ArrayMap<Int, Ability.aID> // Abilities available in battle
    private var learntAbilities:    ArrayMap<Int, Ability.aID> // Abilities activated on the graph
    private var learntEquipment:    Array<BodyPart>


    // ................................................................................ Constructors
    init
    {
        nodes               = ArrayMap()
        edges               = Array()
        abilityNodes        = ArrayMap()
        equipmentNodes      = ArrayMap()
        metamorphosisNodes  = Array()
        activeAbilities     = ArrayMap()
        learntAbilities     = ArrayMap()
        learntEquipment     = Array()
    }

    constructor
    (
            nodes: ArrayMap<Int, Node>,
            edges: Array<Edge>,
            abilityNodes: ArrayMap<Int, Ability.aID>,
            equipmentNodes: ArrayMap<Int, BodyPart>,
            metamorphosisNodes: Array<Int>,
            activeAbilities: ArrayMap<Int, Ability.aID>,
            learntAbilities: ArrayMap<Int, Ability.aID>,
            learntEquipment: Array<BodyPart>
     ){
        this.nodes              = nodes
        this.edges              = edges
        this.abilityNodes       = abilityNodes
        this.equipmentNodes     = equipmentNodes
        this.metamorphosisNodes = metamorphosisNodes
        this.activeAbilities    = activeAbilities
        this.learntAbilities    = learntAbilities
        this.learntEquipment    = learntEquipment
    }

    constructor(core: AGuardian, data: SpeciesDescription)
    {
        // Assemble ability graph layout: Nodes
        for (i in GraphTemplate.coordinates.indices)
        {
            val nodePosition = GraphTemplate.getNodePosition(i)
            nodes.put(i, Node(nodePosition.x, nodePosition.y, i))
        }

        // Assemble ability graph layout: Edges
        for (i in GraphTemplate.connections.indices)
        {
            val edge = GraphTemplate.getEdge(i)
            edges.add(Edge(nodes[edge.x], nodes[edge.y]))
        }

        initialize(core, data)
    }



    private fun initialize(core: AGuardian, data: SpeciesDescription)
    {
        this.core = core

        for (key in data.abilityNodes.keys())
        {
            nodes[key].type = Node.Type.ABILITY
            abilityNodes[key] = data.abilityNodes[key]
        }

        for (key in data.equipmentNodes.keys())
        {
            nodes[key].type = Node.Type.EQUIPMENT
            equipmentNodes[key] = data.equipmentNodes[key]
        }

        for (key in data.metamorphosisNodes)
        {
            nodes[key].type = Node.Type.METAMORPHOSIS
            metamorphosisNodes.add(key)
        }

        var counter = 0
        learntAbilities.values().forEach { a -> activeAbilities[counter] = a; counter++ }

        activateNode(0)
    }


    // ........................................................................... GETTERS & SETTERS
    override fun getNodes(): ArrayMap<Int, Node> = nodes

    override fun getEdges(): Array<Edge> = edges

    override fun getAbilityNodes(): ArrayMap<Int, Ability.aID> = abilityNodes

    override fun getEquipmentNodes(): ArrayMap<Int, BodyPart> = equipmentNodes

    override fun getMetamorphosisNodes(): Array<Int> = metamorphosisNodes

    override fun activateNode(nodeID: Int)
    {
        checkNotNull(core)

        val node = nodes.get(nodeID)

        node.activate()
        enableNeighborNodes(nodeID)

        when (node.type)
        {
            Node.Type.ABILITY -> learnAbility(nodeID)
            Node.Type.EQUIPMENT -> learnEquipment(nodeID)
            else -> {}
        }

        core?.setAbilitiesChanged()
        core?.notifyObservers()
    }

    override fun isNodeEnabled(nodeID: Int) = nodes[nodeID].isEnabled

    override fun learnsAbilityAt(nodeID: Int) = abilityNodes.containsKey(nodeID)

    override fun learnsEquipmentAt(nodeID: Int) = equipmentNodes.containsKey(nodeID)

    override fun metamorphsAt(nodeID: Int) = metamorphosisNodes.contains(nodeID, false)

    override fun getCurrentForm(): Int
    {
        var activatedMetamorphosisNodes = 0
        for (key in metamorphosisNodes)
        {
            if (getNodes()[key].isActive) { activatedMetamorphosisNodes++ }
        }
        return activatedMetamorphosisNodes
    }

    override fun learnsSomethingAt(nodeID: Int): Boolean
    {
        return learnsAbilityAt(nodeID) || learnsEquipmentAt(nodeID) || metamorphsAt(nodeID)
    }

    override fun nodeTypeAt(nodeID: Int): Node.Type
    {
        return when
        {
            learnsAbilityAt(nodeID)   -> Node.Type.ABILITY
            learnsEquipmentAt(nodeID) -> Node.Type.EQUIPMENT
            metamorphsAt(nodeID)      -> Node.Type.METAMORPHOSIS
            else                      -> Node.Type.EMPTY
        }
    }

    override fun hasLearntEquipment(bodyPart: BodyPart): Boolean
    {
        return learntEquipment.contains(bodyPart, true)
    }

    override fun getActiveAbility(abilitySlot: Int): Ability.aID
    {
        return activeAbilities.get(abilitySlot)
    }

    override fun getRandomActiveAbility(): Ability.aID
    {
        return getActiveAbility(MathUtils.random(0, activeAbilities.size - 1))
    }

    override fun setActiveAbility(slot: Int, learntAbilityNumber: Int)
    {
        checkNotNull(core)

        val abilityToLearn = learntAbilities.get(learntAbilityNumber) ?: return

        for (key in activeAbilities.keys()) {
            val abilityAtThisSlot = activeAbilities[key]

            if (abilityAtThisSlot != null)
            {
                if (abilityAtThisSlot == abilityToLearn)
                {
                    activeAbilities.put(key, null)
                }
            }
        }
        activeAbilities.put(slot, learntAbilities.get(learntAbilityNumber))

        core?.setAbilitiesChanged()
        core?.notifyObservers()
    }

    override fun getActiveAbilities(): ArrayMap<Int, Ability.aID> = activeAbilities

    override fun getLearntAbilities(): ArrayMap<Int, Ability.aID> = learntAbilities


    // .............................................................................. HELPER METHODS
    private fun learnAbility(nodeID: Int): Boolean
    {
        return if(learnsAbilityAt(nodeID))
        {
            learntAbilities.put(nodeID, abilityNodes.get(nodeID))
            true
        }
        else false
    }

    private fun learnEquipment(nodeID: Int): Boolean
    {
        return if (learnsEquipmentAt(nodeID))
        {
            learntEquipment.add(equipmentNodes.get(nodeID))
            return true
        }
        else false
    }

    private fun enableNeighborNodes(nodeID: Int)
    {
        for (e in edges)
        {
            if (e.from.ID == nodeID || e.to.ID == nodeID)
            {
                val enableID = if (e.from.ID == nodeID) e.to.ID else e.from.ID
                nodes.get(enableID).enable()
            }
        }
    }
}
