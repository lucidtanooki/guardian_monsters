package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;


/**
 * Holds common data of a monster of the given ID
 *
 * @author Georg Eckert 2016
 */
public class GuardianData
{
    /* ............................................................................ ATTRIBUTES .. */
    private int ID;
    private String nameID;

    private ArrayMap<Integer, Ability> abilityNodes;
    private ArrayMap<Integer, BodyPart> equipmentNodes;
    private Array<Integer> metamorphosisNodes;

    private int metamorphesTo;
    private Array<Element> elements;
    private BaseStat baseStat;

    private HeadEquipment.Type headType;
    private BodyEquipment.Type bodyType;
    private HandEquipment.Type handType;
    private FootEquipment.Type footType;

    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * For Serialization only!
     */
    public GuardianData() {}

    public GuardianData(int ID, String nameID, int metamorphesTo, BaseStat baseStat,
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

    public GuardianData(int ID, String nameID, int metamorphsTo, Array<Element> elements, GuardianData ancestorData) {
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

    public int getID() {
        return ID;
    }

    public String getNameID() {
        return nameID;
    }

    public ArrayMap<Integer, Ability> getAbilityNodes() {
        return abilityNodes;
    }

    public ArrayMap<Integer, BodyPart> getEquipmentNodes() {
        return equipmentNodes;
    }

    public Array<Integer> getMetamorphosisNodes() {
        return metamorphosisNodes;
    }

    public int getMetamorphesTo() {
        return metamorphesTo;
    }

    public Array<Element> getElements() {
        return elements;
    }

    public BaseStat getBaseStat() {
        return baseStat;
    }
}
