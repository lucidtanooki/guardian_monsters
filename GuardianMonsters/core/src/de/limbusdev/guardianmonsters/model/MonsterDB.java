package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.enums.Element;


/**
 * Created by georg on 20.12.15.
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
        MonsterData status;
        boolean canEvolve = element.getChildByName("evolutionID") != null;
        int evolutionID = element.getInt("evolutionID", 0);
        int evolutionLvl = element.getInt("evolutionLvl", 0);

        AttackInfo attInf = AttackInfo.getInst();
        XmlReader.Element attElement = element.getChildByName("attacks");
        ArrayMap<Integer,Ability> attacks = new ArrayMap<>();
        for(int i = 0; i < attElement.getChildCount(); i++) {
            XmlReader.Element a = attElement.getChild(i);
            int ID = a.getIntAttribute("id", 0);
            Element el = Element.valueOf(a.getAttribute("element").toUpperCase());
            Ability att = attInf.getAttack(el, ID);
            int abilityPos = a.getIntAttribute("abilityPos", 0);
            attacks.put(abilityPos, att);
        }

        ArrayMap<Integer,Equipment.EQUIPMENT_TYPE> equipmentGraph = new ArrayMap<>();
        XmlReader.Element equipGraphElem = element.getChildByName("ability-graph-equip");
        equipmentGraph.put(equipGraphElem.getIntAttribute("body"), Equipment.EQUIPMENT_TYPE.BODY);
        equipmentGraph.put(equipGraphElem.getIntAttribute("hands"), Equipment.EQUIPMENT_TYPE.HANDS);
        equipmentGraph.put(equipGraphElem.getIntAttribute("head"), Equipment.EQUIPMENT_TYPE.HEAD);
        equipmentGraph.put(equipGraphElem.getIntAttribute("feet"), Equipment.EQUIPMENT_TYPE.FEET);

        XmlReader.Element elemElement = element.getChildByName("elements");
        Array<Element> elements = new Array<>();
        for(int i = 0; i < elemElement.getChildCount(); i++) {
            XmlReader.Element e = elemElement.getChild(i);
            String eStr = e.getText();
            Element newE = Element.valueOf(eStr.toUpperCase());
            elements.add(newE);
        }

        int ID = element.getIntAttribute("id", 0);
        String nameID = element.getAttribute("nameID", "gm000");

        XmlReader.Element statEl = element.getChildByName("basestats");
        BaseStat stat = new BaseStat(
            ID,
            statEl.getIntAttribute("hp", 99),
            statEl.getIntAttribute("mp", 19),
            statEl.getIntAttribute("pstr", 1),
            statEl.getIntAttribute("pdef", 1),
            statEl.getIntAttribute("mstr", 1),
            statEl.getIntAttribute("mdef", 1),
            statEl.getIntAttribute("speed", 1)
        );

        XmlReader.Element equipComp = element.getChildByName("equipment-compatibility");
        Equipment.HeadEquipment head = Equipment.HeadEquipment.valueOf(equipComp.getAttribute("head", "helmet").toUpperCase());
        Equipment.BodyEquipment body = Equipment.BodyEquipment.valueOf(equipComp.getAttribute("body", "shield").toUpperCase());
        Equipment.HandEquipment hand = Equipment.HandEquipment.valueOf(equipComp.getAttribute("hands", "sword").toUpperCase());
        Equipment.FootEquipment feet = Equipment.FootEquipment.valueOf(equipComp.getAttribute("feet", "claws").toUpperCase());

        status = new MonsterData(
            ID, nameID, attacks, canEvolve, evolutionID, evolutionLvl, elements, stat, equipmentGraph,
            head, body, hand, feet);

        return status;
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
