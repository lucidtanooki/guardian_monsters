package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Locale;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.Node;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;

/**
 * JSONGuardianParser
 *
 * Takes the JSON representation of a Guardians species description and parses it to create a
 * {@link SpeciesDescription} object.
 *
 * Every Guardian can have several {@link SpeciesDescription.MetaForm}s. Every MetaForm of a
 * Guardian is contained in it's SpeciesDescription. When metamorphing, a Guardian doesn't
 * transform to another species. It only reaches a new MetaForm, so the SpeciesID stays the
 *
 * Required JSON format:
 *
 * {
 *     "id": <ID of guardian>,
 *     "metamorphosisNodes": [
 *          <position of node 1>,
 *          <position of node 2>,
 *          ...
 *     ],
 *     "abilities": [
 *          {
 *              "abilityID":  <ID of ability>,
 *              "element":    <name of ability element>,
 *              "abilityPos": <position in Ability Graph>
 *          },
 *          {<ability 2>},
 *          {<ability 3>},
 *          ...
 *     ],
 *     "basestats": {
 *          "hp":    <value>,
 *          "mp":    <value>,
 *          "speed": <value>,
 *          "pstr":  <value>,
 *          "pdef":  <value>,
 *          "mstr":  <value>,
 *          "mdef":  <value>
 *     },
 *     "equipment-compatibility": {
 *          "head":  <head equipment type>,
 *          "hands": <hands equipment type>,
 *          "body":  <body equipment type>,
 *          "feet":  <feet equipment type>
 *     },
 *     "ability-graph-equip": {
 *          "head":  <node position>,
 *          "hands": <node position>,
 *          "body":  <node position>,
 *          "feet":  <node position>
 *     }
 *     "metaForms": [
 *          {
 *              "form":     <meta level>,
 *              "nameID":   <nameID of this meta level>,
 *              "elements": [<element 1>, <element 2>, ...]
 *          },
 *          {<meta form 2>},
 *          {<meta form 3>},
 *          ...
 *     ]
 * }
 *
 * @author Georg Eckert 2018
 */
public class JSONGuardianParser
{
    /**
     * Helper class for easier JSON parsing
     */
    private static class JSONGuardianSpeciesDescription {

        int id;
        int metamorphosisNodes[];
        JSONGuardianAbility abilities[];
        JSONGuardianBaseStats basestats;
        JSONGuardianEquipmentCompatibility equipmentCompatibility;
        JSONGuardianGraphEquip abilityGraphEquip;
        JSONGuardianMetaForm metaForms[];

        static class JSONGuardianAbility {
            int abilityID;
            String element;
            int abilityPos;
        }

        static class JSONGuardianBaseStats {
            int hp, mp, pstr, pdef, mstr, mdef, speed;
        }

        static class JSONGuardianEquipmentCompatibility {
            String head, hands, body, feet;
        }

        static class JSONGuardianGraphEquip {
            int head, hands, body, feet;
        }

        static class JSONGuardianMetaForm {
            int form;
            String nameID;
            String elements[];
        }

        @Override
        public String toString() {
            String output = String.format(
                "--------------------\nSpecies ID: %d\nMeta forms: %d\n%d abilities\n" +
                    "Stats: HP: %d MP: %d\nEquipment:\n\tHands: %s (Node: %d)\n--------------------",
                id,
                metamorphosisNodes.length,
                abilities.length,
                basestats.hp,
                basestats.mp,
                equipmentCompatibility.hands,
                abilityGraphEquip.hands
            );
            return output;
        }
    }

    public static JsonValue parseGuardianList(String jsonString)
    {
        Json json = new Json();
        JsonValue rootElement = new JsonReader().parse(jsonString);
        return rootElement.get("guardians");
    }

    public static SpeciesDescription parseGuardian(JsonValue element)
    {
        Json json = new Json();
        SpeciesDescription speciesDescription;
        JSONGuardianSpeciesDescription spec = json.fromJson(JSONGuardianSpeciesDescription.class, element.toString());

        // ......................................................................................... name & id
        int speciesID = spec.id;

        // ......................................................................................... metamorphosis
        Array<Integer> metamorphosisNodes = parseMetamorphosisNodes(spec);

        // ......................................................................................... abilities
        ArrayMap<Integer, Ability.aID> attacks = parseAbilities(spec);

        // ......................................................................................... equipment
        ArrayMap<Integer, BodyPart> equipmentGraph = parseEquipmentGraph(spec);

        // ......................................................................................... stats

        CommonStatistics stat = parseBaseStats(spec);

        HeadEquipment.Type head = HeadEquipment.Type.valueOf(spec.equipmentCompatibility.head.toUpperCase());
        BodyEquipment.Type body = BodyEquipment.Type.valueOf(spec.equipmentCompatibility.body.toUpperCase());
        HandEquipment.Type hand = HandEquipment.Type.valueOf(spec.equipmentCompatibility.hands.toUpperCase());
        FootEquipment.Type feet = FootEquipment.Type.valueOf(spec.equipmentCompatibility.feet.toUpperCase());

        // ......................................................................................... meta forms
        ArrayMap<Integer,SpeciesDescription.MetaForm> metaForms = new ArrayMap<>();

        for(JSONGuardianSpeciesDescription.JSONGuardianMetaForm form : spec.metaForms)
        {
            Array<Element> elements = new Array<>();
            for(String jsonElement : form.elements) elements.add(Element.valueOf(jsonElement.toUpperCase()));
            SpeciesDescription.MetaForm metaForm
                = new SpeciesDescription.MetaForm(form.form, form.nameID, elements);
            metaForms.put(form.form, metaForm);
        }


        // ......................................................................................... construction
            speciesDescription = new SpeciesDescription(
                speciesID,
                stat,
                attacks,
                equipmentGraph,
                metamorphosisNodes,
                head, body, hand, feet,
                metaForms
            );

        if(Constant.PRINT_PARSED_GUARDIAN)
        {
            System.out.println("Parsed JSON Guardian Data:\n");
            System.out.println(speciesDescription.prettyPrint());
        }

        return speciesDescription;
}


    // ............................................................................................. XML Element Parsers


    /**
     * Parses the the {@link Node}s which allow a
     * guardian to metamorph.
     */
    public static Array<Integer> parseMetamorphosisNodes(JSONGuardianSpeciesDescription spec)
    {
        Array<Integer> metamorphosisNodes = new Array<>();
        for(int node : spec.metamorphosisNodes) metamorphosisNodes.add(node);
        return metamorphosisNodes;
    }

    /**
     * Parses the {@link Ability}s of a Guardian.
     */
    public static ArrayMap<Integer, Ability.aID> parseAbilities(JSONGuardianSpeciesDescription spec)
    {
        ArrayMap<Integer, Ability.aID> abilities = new ArrayMap<>();

        for(JSONGuardianSpeciesDescription.JSONGuardianAbility jsonAbility : spec.abilities)
        {
            Element element = Element.valueOf(jsonAbility.element.toUpperCase());
            abilities.put(jsonAbility.abilityPos, new Ability.aID(jsonAbility.abilityID, element));
        }

        return abilities;
    }

    /**
     * Parses the {@link Equipment} nodes of
     * a Guardian.
     */
    public static ArrayMap<Integer, BodyPart> parseEquipmentGraph(JSONGuardianSpeciesDescription spec)
    {
        ArrayMap<Integer, BodyPart> equipmentGraph = new ArrayMap<>();

            equipmentGraph.put(spec.abilityGraphEquip.body,  BodyPart.BODY);
            equipmentGraph.put(spec.abilityGraphEquip.hands, BodyPart.HANDS);
            equipmentGraph.put(spec.abilityGraphEquip.head,  BodyPart.HEAD);
            equipmentGraph.put(spec.abilityGraphEquip.feet,  BodyPart.FEET);

        return equipmentGraph;
    }

    /**
     * Parses the {@link CommonStatistics} of a Guardian.
     */
    public static CommonStatistics parseBaseStats(JSONGuardianSpeciesDescription spec)
    {
        CommonStatistics stat = new CommonStatistics(
            spec.basestats.hp,
            spec.basestats.mp,
            spec.basestats.pstr,
            spec.basestats.pdef,
            spec.basestats.mstr,
            spec.basestats.mdef,
            spec.basestats.speed
        );
        return stat;
    }


}
