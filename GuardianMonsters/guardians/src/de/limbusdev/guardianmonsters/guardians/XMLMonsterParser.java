package de.limbusdev.guardianmonsters.guardians;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;
import de.limbusdev.guardianmonsters.guardians.monsters.BaseStat;
import de.limbusdev.guardianmonsters.guardians.monsters.MonsterData;

/**
 * XMLMonsterParser
 *
 * @author Georg Eckert 2017
 */

public class XMLMonsterParser {

    public static MonsterData parseMonster(XmlReader.Element element, MonsterData ancestor) {
        MonsterData monsterData;

        // ............................................................................... name & id
        int ID = element.getIntAttribute("id", 0);
        String nameID = element.getAttribute("nameID", "gm000");

        // ................................................................................ elements
        XmlReader.Element elemElement = element.getChildByName("elements");
        Array<Element> elements = new Array<>();
        for(int i = 0; i < elemElement.getChildCount(); i++) {
            XmlReader.Element e = elemElement.getChild(i);
            String eStr = e.getText();
            Element newE = Element.valueOf(eStr.toUpperCase());
            elements.add(newE);
        }

        // ........................................................................... metamorphosis
        int metamorphsFrom = element.getInt("metamorphsFrom", 0);
        int metamorphsTo   = element.getInt("metamorphsTo",  0);

        if(ancestor != null) {
            monsterData = new MonsterData(ID, nameID, metamorphsTo, elements, ancestor);
            return monsterData;
        }

        Array<Integer> metamorphosisNodes = new Array<>();
        XmlReader.Element metaElement = element.getChildByName("metamorphosisNodes");
        if(metaElement != null) {
            for(int i=0; i<metaElement.getChildCount(); i++) {
                int metaNode = Integer.parseInt(metaElement.getChild(i).getText());
                metamorphosisNodes.add(metaNode);
            }
        }

        // ............................................................................... abilities
        XmlReader.Element attElement = element.getChildByName("attacks");
        ArrayMap<Integer, Ability> attacks = parseAbilities(attElement);

        // ............................................................................... equipment

        XmlReader.Element equipGraphElem = element.getChildByName("ability-graph-equip");
        ArrayMap<Integer, BodyPart> equipmentGraph = parseEquipmentGraph(equipGraphElem);

        // ................................................................................... stats
        XmlReader.Element statEl = element.getChildByName("basestats");
        BaseStat stat = parseBaseStats(statEl, ID);

        HeadEquipment.Type head;
        BodyEquipment.Type body;
        HandEquipment.Type hand;
        FootEquipment.Type feet;

        XmlReader.Element equipComp = element.getChildByName("equipment-compatibility");
        if(equipComp != null) {
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

        // ............................................................................ construction
        monsterData = new MonsterData(
            ID, nameID, metamorphsTo,
            stat, elements,
            attacks, equipmentGraph, metamorphosisNodes,
            head, body, hand, feet
        );

        return monsterData;
    }

    private static ArrayMap<Integer, Ability> parseAbilities(XmlReader.Element element) {
        ArrayMap<Integer, Ability> abilities = new ArrayMap<>();
        if(element != null) {
            AbilityDB attInf = AbilityDB.getInstance();
            for(int i = 0; i < element.getChildCount(); i++) {
                XmlReader.Element a = element.getChild(i);
                int attID = a.getIntAttribute("id", 0);
                Element el = Element.valueOf(a.getAttribute("element").toUpperCase());
                Ability att = attInf.getAbility(el, attID);
                int abilityPos = a.getIntAttribute("abilityPos", 0);
                abilities.put(abilityPos, att);
            }
        }
        return abilities;
    }

    private static ArrayMap<Integer, BodyPart> parseEquipmentGraph(XmlReader.Element equipGraphElem) {
        ArrayMap<Integer, BodyPart> equipmentGraph = new ArrayMap<>();
        if(equipGraphElem != null) {
            equipmentGraph.put(equipGraphElem.getIntAttribute("body"), BodyPart.BODY);
            equipmentGraph.put(equipGraphElem.getIntAttribute("hands"), BodyPart.HANDS);
            equipmentGraph.put(equipGraphElem.getIntAttribute("head"), BodyPart.HEAD);
            equipmentGraph.put(equipGraphElem.getIntAttribute("feet"), BodyPart.FEET);
        }
        return equipmentGraph;
    }

    private static BaseStat parseBaseStats(XmlReader.Element statEl, int ID) {
        BaseStat stat;
        if(statEl != null) {
            stat = new BaseStat(
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
            stat = new BaseStat(ID, 300, 50, 10, 10, 10, 10, 10);
        }
        return stat;
    }
}
