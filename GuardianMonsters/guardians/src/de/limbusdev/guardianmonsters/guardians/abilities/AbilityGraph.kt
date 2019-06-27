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
    // --------------------------------------------------------------------------------------------- PROPERTIES

    // .............................................................. public
    // Interface Properties
    override val nodes              get() = _nodes
    override val edges              get() = _edges
    override val abilityNodes       get() = _abilityNodes
    override val equipmentNodes     get() = _equipmentNodes
    override val metamorphosisNodes get() = _metamorphosisNodes
    override val activeAbilities    get() = _activeAbilities
    override val learntAbilities    get() = _learntAbilities
    override val learntEquipment    get() = _learntEquipment

    // .............................................................. private

    // Backing Property Fields
    private var _nodes              : ArrayMap<Int, Node>         = ArrayMap()
    private var _edges              : Array<Edge>                 = Array()
    private var _abilityNodes       : ArrayMap<Int, Ability.aID>  = ArrayMap()
    private var _equipmentNodes     : ArrayMap<Int, BodyPart>     = ArrayMap()
    private var _metamorphosisNodes : Array<Int>                  = Array()
    private var _activeAbilities    : ArrayMap<Int, Ability.aID?> = ArrayMap()
    private var _learntAbilities    : ArrayMap<Int, Ability.aID>  = ArrayMap()
    private var _learntEquipment    : Array<BodyPart>             = Array()

    private lateinit var core       : AGuardian // Guardian this graph belongs to


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    constructor
    (
            nodes               : ArrayMap<Int, Node>,
            edges               : Array<Edge>,
            abilityNodes        : ArrayMap<Int, Ability.aID>,
            equipmentNodes      : ArrayMap<Int, BodyPart>,
            metamorphosisNodes  : Array<Int>,
            activeAbilities     : ArrayMap<Int, Ability.aID?>,
            learntAbilities     : ArrayMap<Int, Ability.aID>,
            learntEquipment     : Array<BodyPart>
     ){
        _nodes              = nodes
        _edges              = edges
        _abilityNodes       = abilityNodes
        _equipmentNodes     = equipmentNodes
        _metamorphosisNodes = metamorphosisNodes
        _activeAbilities    = activeAbilities
        _learntAbilities    = learntAbilities
        _learntEquipment    = learntEquipment
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
        for(slot in 0..6) { activeAbilities[slot] = null }
        learntAbilities.values().forEach { a -> activeAbilities[counter] = a; counter++ }

        activateNode(0)
    }


    // --------------------------------------------------------------------------------------------- INTERFACE

    // .............................................................. Interface Properties
    override val currentForm: Int get()
    {
        var activeMetamorphosisNodeCounter = 0
        for (key in metamorphosisNodes)
        {
            if (nodes[key].isActive) { activeMetamorphosisNodeCounter++ }
        }
        return activeMetamorphosisNodeCounter
    }


    // .............................................................. Interface Methods
    override fun activateNode(nodeID: Int)
    {
        val node = nodes.get(nodeID)

        node.activate()
        enableNeighborNodes(nodeID)

        when (node.type)
        {
            Node.Type.ABILITY   -> learnAbility(nodeID)
            Node.Type.EQUIPMENT -> learnEquipment(nodeID)
            else                -> {}
        }

        core.setAbilitiesChanged()
        core.notifyObservers()
    }

    override fun setActiveAbility(slot: Int, learntAbilityNumber: Int)
    {
        check(slot in 0..6)
        check(learntAbilities.containsKey(learntAbilityNumber))
        val newActiveAbility = learntAbilities[learntAbilityNumber]
        checkNotNull(newActiveAbility)

        // Check if chosen ability already occupies a slot, -1 if not present
        val oldSlotOfNewAbility = activeAbilities.indexOfValue(newActiveAbility, false)

        // If Ability has already been active, clear the previous slot
        if(oldSlotOfNewAbility != -1) { activeAbilities[oldSlotOfNewAbility] = null }

        // Get ability that is currently in this slot
        val oldActiveAbility = activeAbilities[slot]

        // If slot was preoccupied, reset it
        if(oldActiveAbility != null) { activeAbilities[slot] = null }

        // Put new ability into the slot
        activeAbilities[slot] = newActiveAbility

        // If the new slot was already occupied and the new ability has been active, swap slots
        if(oldSlotOfNewAbility != -1 && oldActiveAbility != null)
        {
            activeAbilities[oldSlotOfNewAbility] = oldActiveAbility
        }

        core.setAbilitiesChanged()
        core.notifyObservers()
    }

    override fun isNodeEnabled(nodeID: Int) = nodes[nodeID].isEnabled

    override fun learnsAbilityAt(nodeID: Int) = abilityNodes.containsKey(nodeID)

    override fun learnsEquipmentAt(nodeID: Int) = equipmentNodes.containsKey(nodeID)

    override fun metamorphsAt(nodeID: Int) = metamorphosisNodes.contains(nodeID, false)

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

    override fun getRandomActiveAbility() : Ability.aID
    {
        val slot = getRandomActiveAbilitySlot()
        val ability = activeAbilities[slot]

        checkNotNull(ability) { "Random ability is null. The must always be at least one active ability." }
        return ability
    }

    override fun getRandomActiveAbilitySlot() : Int
    {
        return MathUtils.random(0, activeAbilities.size - 1)
    }


    // --------------------------------------------------------------------------------------------- HELPER METHODS
    private fun learnAbility(nodeID: Int): Boolean
    {
        if(learnsAbilityAt(nodeID))
        {
            learntAbilities.put(nodeID, abilityNodes.get(nodeID))
            return true
        }
        return false
    }

    private fun learnEquipment(nodeID: Int): Boolean
    {
        if (learnsEquipmentAt(nodeID))
        {
            learntEquipment.add(equipmentNodes.get(nodeID))
            return true
        }
        return false
    }

    /** Usually only neighbors of active nodes may be activated as well. This marks them as ENABLED. */
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
