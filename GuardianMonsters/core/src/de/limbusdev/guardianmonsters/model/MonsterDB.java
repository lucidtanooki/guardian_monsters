package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.items.BodyEquipment;
import de.limbusdev.guardianmonsters.model.items.BodyPart;
import de.limbusdev.guardianmonsters.model.items.Equipment;
import de.limbusdev.guardianmonsters.model.items.FootEquipment;
import de.limbusdev.guardianmonsters.model.items.HandEquipment;
import de.limbusdev.guardianmonsters.model.items.HeadEquipment;
import de.limbusdev.guardianmonsters.model.monsters.Element;
import de.limbusdev.guardianmonsters.model.monsters.BaseStat;
import de.limbusdev.guardianmonsters.model.monsters.MonsterData;


/**
 * @author Georg Eckert 2017
 */
public class MonsterDB {
    /* ............................................................................ ATTRIBUTES .. */
    private ArrayMap<Integer, MonsterData> statusInfos;
    private static MonsterDB instance;


    /* ........................................................................... CONSTRUCTOR .. */

    public static MonsterDB singleton() {
        if(instance == null) instance = new MonsterDB();
        return instance;
    }

    public String getNameById(int id) {
        return statusInfos.get(id).nameID;
    }

    private MonsterDB() {
        statusInfos = new ArrayMap<>();

        FileHandle handle = Gdx.files.internal("data/guardians.xml");
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element rootElement;

        try {
            rootElement = xmlReader.parse(handle);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < rootElement.getChildCount(); i++) {
            MonsterData info = parseMonster(rootElement.getChild(i));
            statusInfos.put(info.ID,info);
        }

    }

    private MonsterData parseMonster(XmlReader.Element element) {
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
        int metamorphsTo   = element.getInt("metamorphesTo",  0);

        if(metamorphsFrom > 0) {
            MonsterData ancestorMonData = getData(metamorphsFrom);
            monsterData = new MonsterData(ID, nameID, metamorphsTo, elements, ancestorMonData);
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
        AbilityDB attInf = AbilityDB.getInst();
        XmlReader.Element attElement = element.getChildByName("attacks");
        ArrayMap<Integer, Ability> attacks = new ArrayMap<>();
        for(int i = 0; i < attElement.getChildCount(); i++) {
            XmlReader.Element a = attElement.getChild(i);
            int attID = a.getIntAttribute("id", 0);
            Element el = Element.valueOf(a.getAttribute("element").toUpperCase());
            Ability att = attInf.getAttack(el, attID);
            int abilityPos = a.getIntAttribute("abilityPos", 0);
            attacks.put(abilityPos, att);
        }

        // ............................................................................... equipment
        ArrayMap<Integer, BodyPart> equipmentGraph = new ArrayMap<>();
        XmlReader.Element equipGraphElem = element.getChildByName("ability-graph-equip");
        equipmentGraph.put(equipGraphElem.getIntAttribute("body"), BodyPart.BODY);
        equipmentGraph.put(equipGraphElem.getIntAttribute("hands"), BodyPart.HANDS);
        equipmentGraph.put(equipGraphElem.getIntAttribute("head"), BodyPart.HEAD);
        equipmentGraph.put(equipGraphElem.getIntAttribute("feet"), BodyPart.FEET);

        // ................................................................................... stats
        XmlReader.Element statEl = element.getChildByName("basestats");
        BaseStat stat = new BaseStat(
            ID,
            statEl.getIntAttribute("hp",    300),
            statEl.getIntAttribute("mp",    50),
            statEl.getIntAttribute("pstr",  10),
            statEl.getIntAttribute("pdef",  10),
            statEl.getIntAttribute("mstr",  10),
            statEl.getIntAttribute("mdef",  10),
            statEl.getIntAttribute("speed", 10)
        );

        XmlReader.Element equipComp = element.getChildByName("equipment-compatibility");
        HeadEquipment.Type head = HeadEquipment.Type.valueOf(equipComp.getAttribute("head", "helmet").toUpperCase());
        BodyEquipment.Type body = BodyEquipment.Type.valueOf(equipComp.getAttribute("body", "shield").toUpperCase());
        HandEquipment.Type hand = HandEquipment.Type.valueOf(equipComp.getAttribute("hands", "sword").toUpperCase());
        FootEquipment.Type feet = FootEquipment.Type.valueOf(equipComp.getAttribute("feet", "claws").toUpperCase());

        // ............................................................................ construction
        monsterData = new MonsterData(
            ID, nameID, metamorphsTo,
            stat, elements,
            attacks, equipmentGraph, metamorphosisNodes,
            head, body, hand, feet
        );

        return monsterData;
    }

    public ArrayMap<Integer, MonsterData> getStatusInfos() {
        return statusInfos;
    }

    public MonsterData getData(int monsterID) {
        return statusInfos.get(monsterID);
    }


}
