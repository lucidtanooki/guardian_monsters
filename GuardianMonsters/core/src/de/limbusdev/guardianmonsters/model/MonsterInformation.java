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
        monsterNames.set(0, "mon_kroki");
        monsterNames.set(1, "mon_dinvi");
        monsterNames.set(2, "mon_totowi");
        monsterNames.set(3, "mon_taranya");
        monsterNames.set(4, "mon_sheelfish");
        monsterNames.set(5, "mon_balleera");
        monsterNames.set(6, "mon_caterparan");
        monsterNames.set(7, "mon_toff");
        monsterNames.set(8, "mon_fingercrab");
        monsterNames.set(9, "mon_springby");
        monsterNames.set(10, "mon_flyear");
        monsterNames.set(11, "mon_tuxeduck");
        monsterNames.set(12, "mon_eggolomia");
        monsterNames.set(13, "mon_big_eyed_ka");
        monsterNames.set(14, "mon_cath");
        monsterNames.set(15, "mon_dogh");
        monsterNames.set(16, "mon_firoxotl");

        this.statusInfos = new ArrayMap<Integer,MonsterStatusInformation>();

        // 001 KROKI
        ArrayMap<Integer,Attack> attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.sprinkle);
        attacks.put(5, AttackInfo.tripit);
        attacks.put(10, AttackInfo.water);
        attacks.put(20, AttackInfo.watera);
        Array<Element> elements = new Array<Element>();
        elements.add(Element.WATER);
        BaseStat base = new BaseStat(1,30,5,9,11,8,10,10);
        statusInfos.put(1, new MonsterStatusInformation(
                1, "mon_kroki", attacks, true, 2, 18, elements, base));

        // 002 DINVI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5,AttackInfo.tripit);
        attacks.put(10,AttackInfo.earth);
        attacks.put(20,AttackInfo.eartha);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(2);
        statusInfos.put(2, new MonsterStatusInformation(
                2, "mon_dinvi", attacks, true, 2, 18, elements, base));

        // 003 TOTOWI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tripit);
        attacks.put(10,AttackInfo.facefold);
        elements = new Array<Element>();
        elements.add(Element.FOREST);
        base = new BaseStat(3,30,5,9,11,8,10,12);
        statusInfos.put(3, new MonsterStatusInformation(
                3, "mon_totowi", attacks, false, 0, 0, elements, base));

        // 004 TARANYA
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.ARTHROPODA);
        base = new BaseStat(4);
        statusInfos.put(4, new MonsterStatusInformation(
                4, "mon_taranya", attacks, false, 0, 0, elements, base));

        // 005 Sheelfish
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        base = new BaseStat(5);
        statusInfos.put(5, new MonsterStatusInformation(
                5, "mon_sheelfish", attacks, false, 0, 0, elements, base));

        // 006 Baleera
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5,AttackInfo.kick);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(6);
        statusInfos.put(6, new MonsterStatusInformation(
                6, "mon_baleera", attacks, false, 0, 0, elements, base));

        // 007 Caterparan
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.ARTHROPODA);
        base = new BaseStat(7,30,5,9,11,8,10,9);
        statusInfos.put(7, new MonsterStatusInformation(
                7, "mon_caterparan", attacks, false, 0, 0, elements, base));

        // 008 Toff
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.leafgust);
        elements = new Array<Element>();
        elements.add(Element.FOREST);
        base = new BaseStat(8);
        statusInfos.put(8, new MonsterStatusInformation(
                8, "mon_toff", attacks, false, 0, 0, elements, base));

        // 009 Fingercrab
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        elements.add(Element.ARTHROPODA);
        base = new BaseStat(9);
        statusInfos.put(9, new MonsterStatusInformation(
                9, "mon_fingercrab", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.kick);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(10);
        statusInfos.put(10, new MonsterStatusInformation(
                10, "mon_springby", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.kick);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        elements.add(Element.SPIRIT);
        base = new BaseStat(11);
        statusInfos.put(11, new MonsterStatusInformation(
                11, "mon_flyear", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        base = new BaseStat(12);
        statusInfos.put(12, new MonsterStatusInformation(
                12, "mon_tuxeduck", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        base = new BaseStat(13);
        statusInfos.put(13, new MonsterStatusInformation(
                13, "mon_eggolomia", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.kick);
        attacks.put(5,AttackInfo.darkspunk);
        elements = new Array<Element>();
        elements.add(Element.DEMON);
        base = new BaseStat(14);
        statusInfos.put(14, new MonsterStatusInformation(
                14, "mon_big_eyed_ka", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(15);
        statusInfos.put(15, new MonsterStatusInformation(
                15, "mon_cath", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.tooth);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(16);
        statusInfos.put(16, new MonsterStatusInformation(
                16, "mon_dogh", attacks, false, 0, 0, elements, base));

        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1,AttackInfo.embers);
        attacks.put(10,AttackInfo.fire);
        attacks.put(20,AttackInfo.fira);
        elements = new Array<Element>();
        elements.add(Element.FIRE);
        elements.add(Element.DRAGON);
        base = new BaseStat(17);
        statusInfos.put(17, new MonsterStatusInformation(
            17, "mon_firoxotl", attacks, false, 0, 0, elements, base));
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public static MonsterInformation getInstance() {
        if(instance == null) instance = new MonsterInformation();
        return instance;
    }
}
