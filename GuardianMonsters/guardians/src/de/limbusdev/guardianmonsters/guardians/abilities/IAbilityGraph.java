package de.limbusdev.guardianmonsters.guardians.abilities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;

/**
 * IAbilityGraph
 *
 * @author Georg Eckert 2017
 */

public interface IAbilityGraph
{
    int X = 0;
    int Y = 1;

    ArrayMap<Integer, Node> getNodes();
    Array<Edge> getEdges();
    ArrayMap<Integer,Ability.aID> getAbilityNodes();
    ArrayMap<Integer,BodyPart> getEquipmentNodes();
    Array<Integer> getMetamorphosisNodes();


    /**
     * Sets the state of the given node to ACTIVE and learns the
     * ability at that node
     * @param nodeID
     */
    void activateNode(int nodeID);

    boolean isNodeEnabled(int nodeID);

    /**
     * Wether this monster learns an attack or other ability at this node
     * @param nodeID
     * @return
     */
    boolean learnsAbilityAt(int nodeID);

    /**
     * Wether this monster learns to carry some kind of equipment at this node
     * @param nodeID
     * @return
     */
    boolean learnsEquipmentAt(int nodeID);

    boolean metamorphsAt(int nodeID);

    int getCurrentForm();

    /**
     * Wether this monster learns an ability or to carry equipment at this node
     * @param nodeID
     * @return
     */
    boolean learnsSomethingAt(int nodeID);

    Node.Type nodeTypeAt(int nodeID);

    /**
     * Finds out if the ability to carry a specific equipment has already been learnt
     * @param bodyPart  the body part in question
     * @return          if it is already possible to carry such equipment
     */
    boolean hasLearntEquipment(BodyPart bodyPart);


    /**
     * returns the ability placed at the given slot
     * @param abilitySlot   slot for in battle ability usage
     * @return              ability which resides there
     */
    Ability.aID getActiveAbility(int abilitySlot);

    /**
     * Puts an ability into one of seven slots, available in battle
     * @param slot                  where the ability should be placed in battle
     * @param learntAbilityNumber   number of ability to be placed there
     */
    void setActiveAbility(int slot, int learntAbilityNumber);

    ArrayMap<Integer, Ability.aID> getActiveAbilities();
}
