package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import de.limbusdev.guardianmonsters.guardians.AbilityDB;
import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;

/**
 * XMLMonsterParser
 *
 * Takes the XML representation of a Guardians species description and parses it to create a
 * {@link SpeciesDescription} object.
 *
 * Required XML format:
 *
 * <guardians>
 *   <guardian speciesID="1" nameID="gm001_fordin">
 *     <metamorphsTo>2</metamorphsTo>
 *     <metamorphosisNodes> ... </metamorphosisNodes>
 *     <elements>           ... </elements>
 *     <attacks>            ... </attacks>
 *     <basestats           ... />
 *     <ability-graph-equip ... />
 *     <equipment-compatibility head="bridle" hands="claws" body="barding" feet="shinprotection" />
 *   </guardian>
 * </guardians>
 *
 * Guardians which have metamorphed from another guardian don't need the complete data structure,
 * but only a small part of it, as they inherit the rest from their ancestor guardian.
 *
 * <guardian speciesID="3" nameID="gm003_brachifor">
 *   <metamorphsFrom>2</metamorphsFrom>
 *   <elements> ... </elements>
 * </guardian>
 *
 * Values:
 *   metamorphsTo .. speciesID of the next metamorphosis stage (usually current speciesID+1,
 *                   0 if not metamorphing)
 *   metamorphsFrom .. speciesID of the ancestor Guardian (usually current speciesID-1,
 *                     0 if there is no ancestor
 *
 * @author Georg Eckert 2017
 */
public class XMLGuardianParser
{
    public static SpeciesDescription parseMonster(XmlReader.Element element, SpeciesDescription ancestor)
    {
        SpeciesDescription speciesDescription;

        // ......................................................................................... name & id
        int speciesID = element.getIntAttribute("speciesID", 0);
        String nameID = element.getAttribute("nameID", "gm000");

        // ......................................................................................... elements
        Array<Element> elements = parseElements(element);

        // ......................................................................................... metamorphosis
        int metamorphsFrom = element.getInt("metamorphsFrom", 0);
        int metamorphsTo   = element.getInt("metamorphsTo",  0);



        if(ancestor != null) {

            // ..................................................................................... ancestor
            speciesDescription = new SpeciesDescription(speciesID, nameID, metamorphsTo, elements, ancestor);

        } else {
            // ..................................................................................... metamorphosis
            Array<Integer> metamorphosisNodes = parseMetamorphosisNodes(element);

            // ..................................................................................... abilities
            ArrayMap<Integer, Ability> attacks = parseAbilities(element);

            // ..................................................................................... equipment
            ArrayMap<Integer, BodyPart> equipmentGraph = parseEquipmentGraph(element);

            // ..................................................................................... stats

            CommonStatistics stat = parseBaseStats(element, speciesID);

            HeadEquipment.Type head;
            BodyEquipment.Type body;
            HandEquipment.Type hand;
            FootEquipment.Type feet;

            XmlReader.Element equipComp = element.getChildByName("equipment-compatibility");
            if (equipComp != null) {
                head = HeadEquipment.Type.valueOf(equipComp.getAttribute("head", "helmet").toUpperCase());
                body = BodyEquipment.Type.valueOf(equipComp.getAttribute("body", "shield").toUpperCase());
                hand = HandEquipment.Type.valueOf(equipComp.getAttribute("hands", "sword").toUpperCase());
                feet = FootEquipment.Type.valueOf(equipComp.getAttribute("feet", "claws").toUpperCase());
            } else {
                head = HeadEquipment.Type.HELMET;
                body = BodyEquipment.Type.ARMOR;
                hand = HandEquipment.Type.SWORD;
                feet = FootEquipment.Type.SHOES;
            }

            // ..................................................................................... construction
            speciesDescription = new SpeciesDescription(
                speciesID,
                nameID,
                metamorphsTo,
                stat,
                elements,
                attacks,
                equipmentGraph,
                metamorphosisNodes,
                head,
                body,
                hand,
                feet,
                metamorphsFrom
            );
        }

        if(Constant.DEBUGGING_ON)
        {
            System.out.println("Parsed XML Guardian Data:\n");
            System.out.println(speciesDescription.prettyPrint());
        }

        return speciesDescription;
    }


    // ............................................................................................. XML Element Parsers

    /**
     * Parses the {@link Element}s of a Guardian. The XML element must provide this structure:
     *
     * <guardian>
     *   <element>
     *       <element>earth</element>
     *       <element>fire</element>
     *   </element>
     * </guardian>
     *
     * @param xmlRootElement
     * @return
     */
    private static Array<Element> parseElements(XmlReader.Element xmlRootElement)
    {
        XmlReader.Element elemElement = xmlRootElement.getChildByName("elements");

        Array<Element> elements = new Array<>();
        for(int i = 0; i < elemElement.getChildCount(); i++) {
            XmlReader.Element e = elemElement.getChild(i);
            String eStr = e.getText();
            Element newE = Element.valueOf(eStr.toUpperCase());
            elements.add(newE);
        }

        return elements;
    }

    /**
     * Parses the the {@link de.limbusdev.guardianmonsters.guardians.abilities.Node}s which allow a
     * guardian to metamorph. The XML element (guardian root element) must provide this structure:
     *
     * <metamorphosisNodes>
     *   <metamorphosisNode>91</metamorphosisNode>
     *   <metamorphosisNode>99</metamorphosisNode>
     * </metamorphosisNodes>
     *
     * @param xmlRootElement
     * @return
     */
    private static Array<Integer> parseMetamorphosisNodes(XmlReader.Element xmlRootElement)
    {
        Array<Integer> metamorphosisNodes = new Array<>();
        XmlReader.Element metaElement = xmlRootElement.getChildByName("metamorphosisNodes");
        if (metaElement != null) {
            for (int i = 0; i < metaElement.getChildCount(); i++) {
                int metaNode = Integer.parseInt(metaElement.getChild(i).getText());
                metamorphosisNodes.add(metaNode);
            }
        }

        return metamorphosisNodes;
    }

    /**
     * Parses the {@link Ability}s of a Guardian. The XML element must provide this structure:
     *
     * <attacks>
     *   <ability element="none"  abilityID="2" abilityPos="0" />
     *   <ability element="earth" abilityID="2" abilityPos="13" />
     * </attacks>
     *
     * @param xmlRootElement
     * @return
     */
    private static ArrayMap<Integer, Ability> parseAbilities(XmlReader.Element xmlRootElement)
    {
        XmlReader.Element element = xmlRootElement.getChildByName("attacks");
        ArrayMap<Integer, Ability> abilities = new ArrayMap<>();
        if(element != null) {
            AbilityDB attInf = AbilityDB.getInstance();
            for(int i = 0; i < element.getChildCount(); i++) {
                XmlReader.Element a = element.getChild(i);
                int attID = a.getIntAttribute("abilityID", 0);
                Element el = Element.valueOf(a.getAttribute("element").toUpperCase());
                Ability att = attInf.getAbility(el, attID);
                int abilityPos = a.getIntAttribute("abilityPos", 0);
                abilities.put(abilityPos, att);
            }
        }
        return abilities;
    }

    /**
     * Parses the {@link de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment} nodes of
     * a Guardian. The XML element must provide this structure:
     *
     * <ability-graph-equip body="21" hands="23" feet="89" head="90" />
     *
     * @param xmlRootElement
     * @return
     */
    private static ArrayMap<Integer, BodyPart> parseEquipmentGraph(XmlReader.Element xmlRootElement)
    {
        XmlReader.Element equipGraphElem = xmlRootElement.getChildByName("ability-graph-equip");
        ArrayMap<Integer, BodyPart> equipmentGraph = new ArrayMap<>();
        if(equipGraphElem != null) {
            equipmentGraph.put(equipGraphElem.getIntAttribute("body"), BodyPart.BODY);
            equipmentGraph.put(equipGraphElem.getIntAttribute("hands"), BodyPart.HANDS);
            equipmentGraph.put(equipGraphElem.getIntAttribute("head"), BodyPart.HEAD);
            equipmentGraph.put(equipGraphElem.getIntAttribute("feet"), BodyPart.FEET);
        }
        return equipmentGraph;
    }

    /**
     * Parses the {@link CommonStatistics} of a Guardian. The XML element must provide this structure:
     *
     * <basestats hp="300" mp="50" speed="10" pstr="10" pdef="10" mstr="10" mdef="10" />
     *
     * @param xmlRootElement
     * @return
     */
    private static CommonStatistics parseBaseStats(XmlReader.Element xmlRootElement, int ID)
    {
        XmlReader.Element statEl = xmlRootElement.getChildByName("basestats");

        CommonStatistics stat;
        if(statEl != null) {
            stat = new CommonStatistics(
                ID,
                statEl.getIntAttribute("hp",    300),
                statEl.getIntAttribute("mp",    50),
                statEl.getIntAttribute("pstr",  10),
                statEl.getIntAttribute("pdef",  10),
                statEl.getIntAttribute("mstr",  10),
                statEl.getIntAttribute("mdef",  10),
                statEl.getIntAttribute("speed", 10)
            );
        } else {
            stat = new CommonStatistics(ID, 300, 50, 10, 10, 10, 10, 10);
        }
        return stat;
    }
}
