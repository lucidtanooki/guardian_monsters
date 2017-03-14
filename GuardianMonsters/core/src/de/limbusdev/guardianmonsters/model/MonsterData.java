package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.Element;


/**
 * Hols some general data about monsters of a specific Index
 *
 * Created by Georg Eckert on 24.01.16.
 */
public class MonsterData {
    /* ............................................................................ ATTRIBUTES .. */
    public final int ID;
    public final String nameID;
    public final ArrayMap<Integer,Ability> learnableAbilitiesByNode;
    public final ArrayMap<Integer, Equipment.EQUIPMENT_TYPE> learnableEquipmentByNode;
    public final Array<Integer> metamorphosisNodes;
    public final int metamorphesTo;
    public final Array<Element> elements;
    public final BaseStat baseStat;
    private final Equipment.HeadEquipment compatibleHeadEquip;
    private final Equipment.BodyEquipment compatibleBodyEquip;
    private final Equipment.HandEquipment compatibleHandEquip;
    private final Equipment.FootEquipment compatibleFootEquip;

    /* ........................................................................... CONSTRUCTOR .. */

    public MonsterData(
        int ID, String nameID, int metamorphesTo,
        BaseStat baseStat, Array<Element> elements,
        ArrayMap<Integer, Ability> learnableAbilitiesByNode,
        ArrayMap<Integer, Equipment.EQUIPMENT_TYPE> learnableEquipmentByNode,
        Array<Integer> metamorphosisNodes,
        Equipment.HeadEquipment head,
        Equipment.BodyEquipment body,
        Equipment.HandEquipment hands,
        Equipment.FootEquipment feet) {

        this.ID = ID;
        this.nameID = nameID;
        this.learnableAbilitiesByNode = learnableAbilitiesByNode;
        this.learnableEquipmentByNode = learnableEquipmentByNode;
        this.metamorphosisNodes = metamorphosisNodes;
        this.metamorphesTo = metamorphesTo;
        this.elements = elements;
        this.baseStat = baseStat;
        this.compatibleHandEquip = hands;
        this.compatibleBodyEquip = body;
        this.compatibleHeadEquip = head;
        this.compatibleFootEquip = feet;
    }

    public MonsterData(int ID, String nameID, int metamorphsTo, Array<Element> elements, MonsterData ancestorData) {
        this(
            ID, nameID, metamorphsTo,
            ancestorData.baseStat,
            elements,
            ancestorData.learnableAbilitiesByNode,
            ancestorData.learnableEquipmentByNode,
            ancestorData.metamorphosisNodes,
            ancestorData.getCompatibleHeadEquip(),
            ancestorData.getCompatibleBodyEquip(),
            ancestorData.getCompatibleHandEquip(),
            ancestorData.getCompatibleFootEquip()
        );
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */

    public Equipment.HeadEquipment getCompatibleHeadEquip() {
        return compatibleHeadEquip;
    }

    public Equipment.BodyEquipment getCompatibleBodyEquip() {
        return compatibleBodyEquip;
    }

    public Equipment.HandEquipment getCompatibleHandEquip() {
        return compatibleHandEquip;
    }

    public Equipment.FootEquipment getCompatibleFootEquip() {
        return compatibleFootEquip;
    }

}
