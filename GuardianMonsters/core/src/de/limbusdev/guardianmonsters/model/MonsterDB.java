package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.enums.Element;


/**
 * Created by Georg Eckert on 20.12.15.
 */
public class MonsterDB {
    /* ............................................................................ ATTRIBUTES .. */
    private ArrayMap<Integer,MonsterData> statusInfos;
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
        MonsterData monData;

        int ID = element.getIntAttribute("id", 0);
        String nameID = element.getAttribute("nameID", "gm000");

        XmlReader.Element elemElement = element.getChildByName("elements");
        Array<Element> elements = new Array<>();
        for(int i = 0; i < elemElement.getChildCount(); i++) {
            XmlReader.Element e = elemElement.getChild(i);
            String eStr = e.getText();
            Element newE = Element.valueOf(eStr.toUpperCase());
            elements.add(newE);
        }

        int metamorphsFrom = element.getInt("metamorphsFrom", 0);
        int metamorphsTo   = element.getInt("metamorphesTo",  0);

        if(metamorphsFrom > 0) {
            MonsterData ancestorMonData = getData(metamorphsFrom);
            monData = new MonsterData(ID, nameID, metamorphsTo, elements, ancestorMonData);
            return monData;
        }

        Array<Integer> metamorphosisNodes = new Array<>();
        XmlReader.Element metaElement = element.getChildByName("metamorphosisNodes");
        if(metaElement != null) {
            for(int i=0; i<metaElement.getChildCount(); i++) {
                int metaNode = Integer.parseInt(metaElement.getChild(i).getText());
                metamorphosisNodes.add(metaNode);
            }
        }

        AttackInfo attInf = AttackInfo.getInst();
        XmlReader.Element attElement = element.getChildByName("attacks");
        ArrayMap<Integer, de.limbusdev.guardianmonsters.model.abilities.Ability> attacks = new ArrayMap<>();
        for(int i = 0; i < attElement.getChildCount(); i++) {
            XmlReader.Element a = attElement.getChild(i);
            int attID = a.getIntAttribute("id", 0);
            Element el = Element.valueOf(a.getAttribute("element").toUpperCase());
            de.limbusdev.guardianmonsters.model.abilities.Ability att = attInf.getAttack(el, attID);
            int abilityPos = a.getIntAttribute("abilityPos", 0);
            attacks.put(abilityPos, att);
        }

        ArrayMap<Integer, de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE> equipmentGraph = new ArrayMap<>();
        XmlReader.Element equipGraphElem = element.getChildByName("ability-graph-equip");
        equipmentGraph.put(equipGraphElem.getIntAttribute("body"), de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE.BODY);
        equipmentGraph.put(equipGraphElem.getIntAttribute("hands"), de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE.HANDS);
        equipmentGraph.put(equipGraphElem.getIntAttribute("head"), de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE.HEAD);
        equipmentGraph.put(equipGraphElem.getIntAttribute("feet"), de.limbusdev.guardianmonsters.model.items.Equipment.EQUIPMENT_TYPE.FEET);

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
        de.limbusdev.guardianmonsters.model.items.Equipment.HeadEquipment head = de.limbusdev.guardianmonsters.model.items.Equipment.HeadEquipment.valueOf(equipComp.getAttribute("head", "helmet").toUpperCase());
        de.limbusdev.guardianmonsters.model.items.Equipment.BodyEquipment body = de.limbusdev.guardianmonsters.model.items.Equipment.BodyEquipment.valueOf(equipComp.getAttribute("body", "shield").toUpperCase());
        de.limbusdev.guardianmonsters.model.items.Equipment.HandEquipment hand = de.limbusdev.guardianmonsters.model.items.Equipment.HandEquipment.valueOf(equipComp.getAttribute("hands", "sword").toUpperCase());
        de.limbusdev.guardianmonsters.model.items.Equipment.FootEquipment feet = de.limbusdev.guardianmonsters.model.items.Equipment.FootEquipment.valueOf(equipComp.getAttribute("feet", "claws").toUpperCase());

        monData = new MonsterData(
            ID, nameID, metamorphsTo,
            stat, elements,
            attacks, equipmentGraph, metamorphosisNodes,
            head, body, hand, feet
        );

        return monData;
    }

    public ArrayMap<Integer, MonsterData> getStatusInfos() {
        return statusInfos;
    }

    public MonsterData getData(int monsterID) {
        return statusInfos.get(monsterID);
    }


    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */

}
