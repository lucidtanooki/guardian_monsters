package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Created by georg on 20.12.15.
 */
public class MonsterInformation {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<String> monsterNames;
    public ArrayMap<Integer,MonsterStatusInformation> statusInfos;
    private static MonsterInformation instance;
    /* ........................................................................... CONSTRUCTOR .. */
    private MonsterInformation() {
        this.monsterNames = new Array<String>();
        for(int i = 0; i< GS.MONSTER_SPRITES; i++) monsterNames.add("");
        monsterNames.set(0, "Kroki");
        monsterNames.set(1, "Dinvi");
        monsterNames.set(2, "Totowi");
        monsterNames.set(3, "Taranya");
        monsterNames.set(4, "Sheelfish");
        monsterNames.set(5, "Balleera");
        monsterNames.set(6, "Caterparan");
        monsterNames.set(7, "Toff");
        monsterNames.set(8, "Fingercrab");
        monsterNames.set(9, "Springby");
        monsterNames.set(10, "Flyear");
        monsterNames.set(11, "Tuxedo");
        monsterNames.set(12, "Eggolomia");
        monsterNames.set(13, "Big eyed Ka");
        monsterNames.set(14, "Cath");
        monsterNames.set(15, "Dogh");
        monsterNames.set(16, "Firoxotl");

        this.statusInfos = new ArrayMap<Integer,MonsterStatusInformation>();

        // 001 KROKI
        ArrayMap<Integer,Attack> attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.sprinkle);
        attacks.put(5, AttackInfo.tripit);
        attacks.put(10, AttackInfo.water);
        attacks.put(20, AttackInfo.watera);
        Array<Element> elements = new Array<Element>();
        elements.add(Element.WATER);
        statusInfos.put(1, new MonsterStatusInformation(
                1, "Kroki", attacks, true, 2, 18, elements));

        // 002 DINVI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5,AttackInfo.tripit);
        attacks.put(10,AttackInfo.earth);
        attacks.put(20,AttackInfo.eartha);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        statusInfos.put(2, new MonsterStatusInformation(
                2, "Dinvi", attacks, true, 2, 18, elements));

        // 003 TOTOWI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tripit);
        attacks.put(10,AttackInfo.facefold);
        elements = new Array<Element>();
        elements.add(Element.FOREST);
        statusInfos.put(3, new MonsterStatusInformation(
                3, "Totowi", attacks, false, 0, 0, elements));

        // 004 TARANYA
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.ARTHROPODA);
        statusInfos.put(4, new MonsterStatusInformation(
                4, "Taranya", attacks, false, 0, 0, elements));

        // 005 Sheelfish
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        statusInfos.put(5, new MonsterStatusInformation(
                5, "Sheelfish", attacks, false, 0, 0, elements));

        // 006 Baleera
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5,AttackInfo.kick);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        statusInfos.put(6, new MonsterStatusInformation(
                6, "Baleera", attacks, false, 0, 0, elements));

        // 007 Caterparan
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.ARTHROPODA);
        statusInfos.put(7, new MonsterStatusInformation(
                7, "Caterparan", attacks, false, 0, 0, elements));

        // 008 Toff
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.leafgust);
        elements = new Array<Element>();
        elements.add(Element.FOREST);
        statusInfos.put(8, new MonsterStatusInformation(
                8, "Toff", attacks, false, 0, 0, elements));

        // 009 Fingercrab
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        elements.add(Element.ARTHROPODA);
        statusInfos.put(9, new MonsterStatusInformation(
                9, "Fingercrab", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.kick);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        statusInfos.put(10, new MonsterStatusInformation(
                10, "Springby", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.kick);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        elements.add(Element.SPIRIT);
        statusInfos.put(11, new MonsterStatusInformation(
                11, "Flyear", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        statusInfos.put(12, new MonsterStatusInformation(
                12, "Tuxeduck", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        statusInfos.put(13, new MonsterStatusInformation(
                13, "Eggolomia", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.kick);
        attacks.put(5,AttackInfo.darkspunk);
        elements = new Array<Element>();
        elements.add(Element.DEMON);
        statusInfos.put(14, new MonsterStatusInformation(
                14, "Big Eyed Ka", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        statusInfos.put(15, new MonsterStatusInformation(
                15, "Cath", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        statusInfos.put(16, new MonsterStatusInformation(
                16, "Dogh", attacks, false, 0, 0, elements));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.embers);
        attacks.put(10,AttackInfo.fire);
        attacks.put(20,AttackInfo.fira);
        elements = new Array<Element>();
        elements.add(Element.FIRE);
        elements.add(Element.DRAGON);
        statusInfos.put(17, new MonsterStatusInformation(
            17, "Firoxotl", attacks, false, 0, 0, elements));
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public static MonsterInformation getInstance() {
        if(instance == null) instance = new MonsterInformation();
        return instance;
    }
}
