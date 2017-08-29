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
 * Species Description
 *
 * Holds a formal description of the given ID. This data follows the idea of a biological
 * classification, following the classical evolutionary classification systematic.
 *
 * Life > Domain > Kingdom > Phylum > Class > Order > Family > Genus > Species
 *
 * Guardians however are divided by only two hierarchical levels: Phylum > Species
 * Where this can be simplified to Element > Species
 *
 * The species can be identified exactly by the unique SpeciesID or simply ID.
 *
 * Instances of this class hold all attributes which are common for all Guardians of the same species.
 *
 * @author Georg Eckert 2016
 */
public class SpeciesDescription
{
    // ............................................................................................. ATTRIBUTES
    private int speciesID;
    private String nameID;

    private ArrayMap<Integer, Ability> abilityNodes;
    private ArrayMap<Integer, BodyPart> equipmentNodes;
    private Array<Integer> metamorphosisNodes;

    private int metamorphsTo;
    private Array<Element> elements;
    private CommonStatistics baseStat;

    private HeadEquipment.Type headType;
    private BodyEquipment.Type bodyType;
    private HandEquipment.Type handType;
    private FootEquipment.Type footType;

    // ............................................................................................. CONSTRUCTOR

    /**
     * For Serialization only!
     */
    public SpeciesDescription() {}

    public SpeciesDescription(int ID, String nameID, int metamorphsTo, CommonStatistics baseStat,
                              Array<Element> elements, ArrayMap<Integer, Ability> abilityNodes,
                              ArrayMap<Integer, BodyPart> equipmentNodes,
                              Array<Integer> metamorphosisNodes, HeadEquipment.Type head,
                              BodyEquipment.Type body, HandEquipment.Type hands, FootEquipment.Type feet) {
        this.speciesID = ID;
        this.nameID = nameID;
        this.abilityNodes = abilityNodes;
        this.equipmentNodes = equipmentNodes;
        this.metamorphosisNodes = metamorphosisNodes;
        this.metamorphsTo = metamorphsTo;
        this.elements = elements;
        this.baseStat = baseStat;
        this.handType = hands;
        this.bodyType = body;
        this.headType = head;
        this.footType = feet;
    }

    public SpeciesDescription(int ID, String nameID, int metamorphsTo, Array<Element> elements, SpeciesDescription ancestorData) {
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
        return speciesID;
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

    public int getMetamorphsTo() {
        return metamorphsTo;
    }

    public Array<Element> getElements() {
        return elements;
    }

    public CommonStatistics getBaseStat() {
        return baseStat;
    }


    // ............................................................................................. OBJECT

    @Override
    public String toString()
    {
        String text = "Species: " + speciesID + "\t with nameID: " + nameID;
        return text;
    }

    public String prettyPrint()
    {
        String pretty = "";
        pretty += "+---- Guardian Species Description ----+\n";
        pretty += "| Species: " + speciesID + "\n";
        pretty += "| nameID: " + nameID + "\n";
        pretty += "| Elements: ";
        for(Element e : elements) pretty += e.toString() + ", ";
        pretty += "\n";
        pretty += "| Metamorphosis to: " + metamorphsTo + "\n";
        pretty += "+--------------------------------------+\n";
        return pretty;
    }

}
