package de.limbusdev.guardianmonsters.guardians.abilities

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart

/**
 * IAbilityGraph
 *
 * @author Georg Eckert 2017
 */

interface IAbilityGraph
{
    // .................................................................................. Properties
    var nodes               : ArrayMap<Int, Node>
    var edges               : Array<Edge>
    var abilityNodes        : ArrayMap<Int, Ability.aID>
    var equipmentNodes      : ArrayMap<Int, BodyPart>
    var metamorphosisNodes  : Array<Int>

    var activeAbilities     : ArrayMap<Int, Ability.aID> // Abilities available in battle
    var learntAbilities     : ArrayMap<Int, Ability.aID> // Abilities activated on the graph
    var learntEquipment     : Array<BodyPart>

    val currentForm         : Int


    // ..................................................................................... Methods
    /**
     * Sets the state of the given node to ACTIVE and learns the
     * ability at that node
     * @param nodeID
     */
    fun activateNode(nodeID: Int)

    fun isNodeEnabled(nodeID: Int): Boolean

    /**
     * Whether this monster learns an attack or other ability at this node
     * @param nodeID
     * @return
     */
    fun learnsAbilityAt(nodeID: Int): Boolean

    /**
     * Whether this monster learns to carry some kind of equipment at this node
     * @param nodeID
     * @return
     */
    fun learnsEquipmentAt(nodeID: Int): Boolean

    fun metamorphsAt(nodeID: Int): Boolean

    /**
     * Whether this monster learns an ability or to carry equipment at this node
     * @param nodeID
     * @return
     */
    fun learnsSomethingAt(nodeID: Int): Boolean

    fun nodeTypeAt(nodeID: Int): Node.Type

    /**
     * Finds out if the ability to carry a specific equipment has already been learnt
     * @param bodyPart  the body part in question
     * @return          if it is already possible to carry such equipment
     */
    fun hasLearntEquipment(bodyPart: BodyPart): Boolean


    /**
     * returns the ability placed at the given slot
     * @param abilitySlot   slot for in battle ability usage
     * @return              ability which resides there
     */
    fun getActiveAbility(abilitySlot: Int): Ability.aID

    fun getRandomActiveAbility() : Ability.aID

    fun getRandomActiveAbilitySlot() : Int

    /**
     * Puts an ability into one of seven slots, available in battle
     * @param slot                  where the ability should be placed in battle
     * @param learntAbilityNumber   number of ability to be placed there
     */
    fun setActiveAbility(slot: Int, learntAbilityNumber: Int)

    companion object
    {
        const val X = 0
        const val Y = 1
    }
}
