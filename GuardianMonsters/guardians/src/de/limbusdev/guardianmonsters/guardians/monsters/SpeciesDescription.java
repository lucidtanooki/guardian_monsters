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

    private ArrayMap<Integer, Ability.aID> abilityNodes;
    private ArrayMap<Integer, BodyPart> equipmentNodes;
    private Array<Integer> metamorphosisNodes;

    private CommonStatistics commonStatistics;

    private HeadEquipment.Type headType;
    private BodyEquipment.Type bodyType;
    private HandEquipment.Type handType;
    private FootEquipment.Type footType;

    private ArrayMap<Integer, MetaForm> metaForms;

    public static class MetaForm {

        public MetaForm(int form, String nameID, Array<Element> elements)
        {
            this.form = form;
            this.nameID = nameID;
            this.elements = elements;
        }

        private int form;
        private String nameID;
        private Array<Element> elements;
    }

    // ............................................................................................. CONSTRUCTOR

    /**
     * For Serialization only!
     */
    public SpeciesDescription() {}

    protected SpeciesDescription (
        int speciesID,
        CommonStatistics commonStatistics,
        ArrayMap<Integer, Ability.aID> abilityNodes,
        ArrayMap<Integer, BodyPart> equipmentNodes,
        Array<Integer> metamorphosisNodes,
        HeadEquipment.Type head,
        BodyEquipment.Type body,
        HandEquipment.Type hands,
        FootEquipment.Type feet,
        ArrayMap<Integer, MetaForm> metaForms
    ) {
        this.speciesID = speciesID;
        this.abilityNodes = abilityNodes;
        this.equipmentNodes = equipmentNodes;
        this.metamorphosisNodes = metamorphosisNodes;
        this.commonStatistics = commonStatistics;
        this.handType = hands;
        this.bodyType = body;
        this.headType = head;
        this.footType = feet;
        this.metaForms = metaForms;
    }

    // ............................................................................................. METHODS
    
    // ............................................................................................. GETTERS & SETTERS

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

    public String getNameID(int currentForm) {
        return metaForms.get(currentForm).nameID;
    }

    public ArrayMap<Integer, Ability.aID> getAbilityNodes() {
        return abilityNodes;
    }

    public ArrayMap<Integer, BodyPart> getEquipmentNodes() {
        return equipmentNodes;
    }

    public Array<Integer> getMetamorphosisNodes() {
        return metamorphosisNodes;
    }


    public Array<Element> getElements(int currentForm) {
        return metaForms.get(currentForm).elements;
    }

    public CommonStatistics getCommonStatistics() {
        return commonStatistics;
    }


    // ............................................................................................. OBJECT

    @Override
    public String toString()
    {
        String text = "Species: " + speciesID;
        return text;
    }

    public String prettyPrint()
    {
        String pretty = "";
        pretty += "+---- Guardian Species Description ----+\n";
        pretty += "| Species: " + speciesID + "\n";
        pretty += "| Abilitiy-Nodes: \n";
        for(int key : abilityNodes.keys()) pretty += "|\tNode " + key + ":\t" + abilityNodes.get(key).ID + "_" + abilityNodes.get(key).element + "\n";
        pretty += "| Equipment-Nodes: \n";
        for(int key : equipmentNodes.keys()) pretty += "|\tNode " + key + ":\t" + equipmentNodes.get(key).toString() + "\n";
        pretty += "| Equipment-Type:\n";
        pretty += "|\tBody:\t"   + bodyType.toString() + "\n";
        pretty += "|\tHands:\t"  + handType.toString() + "\n";
        pretty += "|\tFeet:\t"   + footType.toString() + "\n";
        pretty += "|\tHead:\t"   + headType.toString() + "\n";
        pretty += "| Can metamorph: " + ((metaForms.size > 1) ? "no" : "yes") + "\n";
        pretty += "| Metamorphs\t" + (metaForms.size-1) + " times\n";
        pretty += "+--------------------------------------+\n";
        return pretty;
    }
}
