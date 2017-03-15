package de.limbusdev.guardianmonsters.model.monsters;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.items.BodyEquipment;
import de.limbusdev.guardianmonsters.model.items.BodyPart;
import de.limbusdev.guardianmonsters.model.items.Equipment;
import de.limbusdev.guardianmonsters.model.items.FootEquipment;
import de.limbusdev.guardianmonsters.model.items.HandEquipment;
import de.limbusdev.guardianmonsters.model.items.HeadEquipment;


/**
 * Holds common data of a monster of the given ID
 *
 * @author Georg Eckert 2016
 */
public class MonsterData {
    /* ............................................................................ ATTRIBUTES .. */
    public final int ID;
    public final String nameID;

    public final ArrayMap<Integer, Ability> abilityNodes;
    public final ArrayMap<Integer, BodyPart> equipmentNodes;
    public final Array<Integer> metamorphosisNodes;

    public final int metamorphesTo;
    public final Array<Element> elements;
    public final BaseStat baseStat;

    private final HeadEquipment.Type headType;
    private final BodyEquipment.Type bodyType;
    private final HandEquipment.Type handType;
    private final FootEquipment.Type footType;

    /* ........................................................................... CONSTRUCTOR .. */

    public MonsterData(int ID, String nameID, int metamorphesTo, BaseStat baseStat,
                       Array<Element> elements, ArrayMap<Integer, Ability> abilityNodes,
                       ArrayMap<Integer, BodyPart> equipmentNodes,
                       Array<Integer> metamorphosisNodes, HeadEquipment.Type head,
                       BodyEquipment.Type body, HandEquipment.Type hands, FootEquipment.Type feet) {
        this.ID = ID;
        this.nameID = nameID;
        this.abilityNodes = abilityNodes;
        this.equipmentNodes = equipmentNodes;
        this.metamorphosisNodes = metamorphosisNodes;
        this.metamorphesTo = metamorphesTo;
        this.elements = elements;
        this.baseStat = baseStat;
        this.handType = hands;
        this.bodyType = body;
        this.headType = head;
        this.footType = feet;
    }

    public MonsterData(int ID, String nameID, int metamorphsTo, Array<Element> elements, MonsterData ancestorData) {
        this(
            ID, nameID, metamorphsTo,
            ancestorData.baseStat,
            elements,
            ancestorData.abilityNodes,
            ancestorData.equipmentNodes,
            ancestorData.metamorphosisNodes,
            ancestorData.getHeadType(),
            ancestorData.getBodyType(),
            ancestorData.getHandType(),
            ancestorData.getFootType()
        );
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */

    public HeadEquipment.Type getHeadType() {
        return headType;
    }

    public BodyEquipment.Type getBodyType() {
        return bodyType;
    }

    public HandEquipment.Type getHandType() {
        return handType;
    }

    public FootEquipment.Type getFootType() {
        return footType;
    }

}
