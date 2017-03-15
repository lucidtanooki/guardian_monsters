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
    public final ArrayMap<Integer, de.limbusdev.guardianmonsters.model.abilities.Ability> learnableAbilitiesByNode;
    public final ArrayMap<Integer, de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE> learnableEquipmentByNode;
    public final Array<Integer> metamorphosisNodes;
    public final int metamorphesTo;
    public final Array<Element> elements;
    public final BaseStat baseStat;
    private final de.limbusdev.guardianmonsters.model.items.Equipment.HeadEquipment compatibleHeadEquip;
    private final de.limbusdev.guardianmonsters.model.items.Equipment.BodyEquipment compatibleBodyEquip;
    private final de.limbusdev.guardianmonsters.model.items.Equipment.HandEquipment compatibleHandEquip;
    private final de.limbusdev.guardianmonsters.model.items.Equipment.FootEquipment compatibleFootEquip;

    /* ........................................................................... CONSTRUCTOR .. */

    public MonsterData(
        int ID, String nameID, int metamorphesTo,
        BaseStat baseStat, Array<Element> elements,
        ArrayMap<Integer, de.limbusdev.guardianmonsters.model.abilities.Ability> learnableAbilitiesByNode,
        ArrayMap<Integer, de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE> learnableEquipmentByNode,
        Array<Integer> metamorphosisNodes,
        de.limbusdev.guardianmonsters.model.items.Equipment.HeadEquipment head,
        de.limbusdev.guardianmonsters.model.items.Equipment.BodyEquipment body,
        de.limbusdev.guardianmonsters.model.items.Equipment.HandEquipment hands,
        de.limbusdev.guardianmonsters.model.items.Equipment.FootEquipment feet) {

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

    public de.limbusdev.guardianmonsters.model.items.Equipment.HeadEquipment getCompatibleHeadEquip() {
        return compatibleHeadEquip;
    }

    public de.limbusdev.guardianmonsters.model.items.Equipment.BodyEquipment getCompatibleBodyEquip() {
        return compatibleBodyEquip;
    }

    public de.limbusdev.guardianmonsters.model.items.Equipment.HandEquipment getCompatibleHandEquip() {
        return compatibleHandEquip;
    }

    public de.limbusdev.guardianmonsters.model.items.Equipment.FootEquipment getCompatibleFootEquip() {
        return compatibleFootEquip;
    }

}
