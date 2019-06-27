package de.limbusdev.guardianmonsters.guardians.abilities

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart

/**
 * IAbilityGraph represents a [AGuardian]'s abilities, what it can learn, when and how. It contains
 * it's current meta form and lists of it's learnt equipments and abilities. Additionally it
 * is used to set which abilities are available in a battle.
 *
 *
 * # Active Abilities
 * Abilities that are available for battle, are called **Active Abilities**. A Guardian has 7 slots
 * for those, numbered 0..6. They can be accessed with the `activeAbilities` property. If a slot is
 * not populated, it's entry is null.
 *
 * To access an active ability:
 *
 * ```kt
 * val ability : Ability.aID = guardian.abilityGraph.activeAbility[3] // can be null
 * ```
 *
 * @author Georg Eckert 2017
 */

interface IAbilityGraph
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    // .............................................................. static
    companion object { const val X = 0 ; const val Y = 1 }


    // .............................................................. public
    val nodes               : ArrayMap<Int, Node>
    val edges               : Array<Edge>
    val abilityNodes        : ArrayMap<Int, Ability.aID>
    val equipmentNodes      : ArrayMap<Int, BodyPart>
    val metamorphosisNodes  : Array<Int>

    /** Slots 0..6 with [Ability]s available in battle. `null` if slot is empty. */
    val activeAbilities     : ArrayMap<Int, Ability.aID?>

    val learntAbilities     : ArrayMap<Int, Ability.aID> // Abilities activated on the graph
    val learntEquipment     : Array<BodyPart>

    val currentForm         : Int // Meta Form, starts at 0


    // --------------------------------------------------------------------------------------------- METHODS
    /** Sets the state of the given node to ACTIVE and learns the ability at that node */
    fun activateNode(nodeID: Int)

    /**
     * Puts an ability into one of seven slots, available in battle. If the ability is active
     * already, it's slot will be changed to the new one or swapped if both slots are in use.
     * @param slot                  where the ability should be placed in battle
     * @param learntAbilityNumber   number of ability to be placed there
     */
    fun setActiveAbility(slot: Int, learntAbilityNumber: Int)


    // .............................................................. Query Methods

    /** @return If the node at the given graph position is enabled and therefore ready for activation. */
    fun isNodeEnabled(nodeID: Int): Boolean

    /** @return Whether this monster learns an attack or other ability at this node */
    fun learnsAbilityAt(nodeID: Int): Boolean

    /** @return Whether this monster learns to carry some kind of equipment at this node */
    fun learnsEquipmentAt(nodeID: Int): Boolean

    /** @return If the given graph position causes a metamorphosis. */
    fun metamorphsAt(nodeID: Int): Boolean

    /** @return Whether this monster learns an ability or to carry equipment at this node */
    fun learnsSomethingAt(nodeID: Int): Boolean

    /** @return Type of the node at the given graph position. */
    fun nodeTypeAt(nodeID: Int): Node.Type

    /**
     * Finds out if the ability to carry a specific equipment has already been learnt
     * @param bodyPart  the body part in question
     * @return          if it is already possible to carry such equipment
     */
    fun hasLearntEquipment(bodyPart: BodyPart): Boolean

    /** Returns always a valid ability. */
    fun getRandomActiveAbility() : Ability.aID

    /**
     * Returns a non-null slot of an active ability. Is always valid, since at least one slot must
     * be populated.
     */
    fun getRandomActiveAbilitySlot() : Int
}
