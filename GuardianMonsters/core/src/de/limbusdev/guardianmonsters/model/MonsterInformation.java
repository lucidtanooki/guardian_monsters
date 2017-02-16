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

        monsterNames.set(1, "mon_buuni");
        monsterNames.set(2, "mon_labuuni");
        monsterNames.set(3, "mon_boxbuuni");

        monsterNames.set(4, "mon_kroki");
        monsterNames.set(5, "mon_krokami");
        monsterNames.set(6, "mon_krakatau");

        monsterNames.set(7, "mon_dinvi");
        monsterNames.set(8, "mon_dinvari");
        monsterNames.set(9, "mon_dinvarex");

        monsterNames.set(10, "mon_flyear");
        monsterNames.set(11, "mon_nosgel");
        monsterNames.set(12, "mon_wingelair");

        monsterNames.set(13, "mon_eggolomia");
        monsterNames.set(14, "mon_hootlock");

        //monsterNames.set(15, "mon_sailcoon");
        monsterNames.set(16, "mon_cootiger");

//        monsterNames.set(17, "mon_woggie");
//        monsterNames.set(18, "mon_wogteeth");

        monsterNames.set(27, "mon_toff");
        monsterNames.set(28, "mon_bonfico");


/*
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
        */

        this.statusInfos = new ArrayMap<Integer,MonsterStatusInformation>();

        // 001 BUUNI
        ArrayMap<Integer,Attack> attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(5, AttackInfo.kick);
        attacks.put(10, AttackInfo.earth);
        attacks.put(20, AttackInfo.eartha);
        Array<Element> elements = new Array<Element>();
        elements.add(Element.EARTH);
        BaseStat base = new BaseStat(1,30,5,9,11,8,10,13);
        statusInfos.put(1, new MonsterStatusInformation(
            1, "mon_buuni", attacks, true, 2, 18, elements, base));

        // 002 LABUUNI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(5, AttackInfo.kick);
        attacks.put(10, AttackInfo.earth);
        attacks.put(20, AttackInfo.eartha);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(2,35,15,12,13,10,12,12);
        statusInfos.put(2, new MonsterStatusInformation(
            2, "mon_labuuni", attacks, true, 3, 40, elements, base));

        // 003 BOXBUUNI
        attacks = new ArrayMap<>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(5, AttackInfo.kick);
        attacks.put(10, AttackInfo.earth);
        attacks.put(20, AttackInfo.eartha);
        elements = new Array<>();
        elements.add(Element.EARTH);
        base = new BaseStat(3,399,666,800,777,405,100,600);
        statusInfos.put(3, new MonsterStatusInformation(
            3, "mon_boxbuuni", attacks, false, 0, 0, elements, base));

        // 004 KROKI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(1, AttackInfo.sprinkle);
        attacks.put(5, AttackInfo.tripit);
        attacks.put(10, AttackInfo.water);
        attacks.put(20, AttackInfo.watera);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        base = new BaseStat(4,30,5,9,11,8,10,10);
        statusInfos.put(4, new MonsterStatusInformation(
                4, "mon_kroki", attacks, true, 5, 20, elements, base));

        // 005 KROKAMI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(1, AttackInfo.sprinkle);
        attacks.put(5, AttackInfo.tripit);
        attacks.put(10, AttackInfo.water);
        attacks.put(20, AttackInfo.watera);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        base = new BaseStat(5,30,5,9,11,8,10,10);
        statusInfos.put(5, new MonsterStatusInformation(
            5, "mon_krokami", attacks, true, 6, 39, elements, base));

        // 006 KRAKATAU
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(1, AttackInfo.sprinkle);
        attacks.put(5, AttackInfo.tripit);
        attacks.put(10, AttackInfo.water);
        attacks.put(20, AttackInfo.watera);
        elements = new Array<Element>();
        elements.add(Element.WATER);
        base = new BaseStat(6,30,5,9,11,8,10,10);
        statusInfos.put(6, new MonsterStatusInformation(
            6, "mon_krakatau", attacks, false, 0, 0, elements, base));

        // 007 DINVI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(5, AttackInfo.embers);
        attacks.put(10, AttackInfo.fire);
        attacks.put(20, AttackInfo.fira);
        elements = new Array<Element>();
        elements.add(Element.FIRE);
        base = new BaseStat(7,30,5,9,11,8,10,10);
        statusInfos.put(7, new MonsterStatusInformation(
            7, "mon_dinvi", attacks, true, 8, 19, elements, base));

        // 008 DINVARI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(5, AttackInfo.embers);
        attacks.put(10, AttackInfo.fire);
        attacks.put(20, AttackInfo.fira);
        elements = new Array<Element>();
        elements.add(Element.FIRE);
        base = new BaseStat(8,30,5,9,11,8,10,10);
        statusInfos.put(8, new MonsterStatusInformation(
            8, "mon_dinvari", attacks, true, 9, 40, elements, base));

        // 009 DINVAREX
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(5, AttackInfo.embers);
        attacks.put(10, AttackInfo.fire);
        attacks.put(20, AttackInfo.fira);
        elements = new Array<Element>();
        elements.add(Element.FIRE);
        base = new BaseStat(9,30,5,9,11,8,10,10);
        statusInfos.put(9, new MonsterStatusInformation(
            9, "mon_dinvarex", attacks, false, 0, 0, elements, base));

        // 010 FLYEAR
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.kick);
        attacks.put(5, AttackInfo.tooth);
        attacks.put(10, AttackInfo.leafgust);
        attacks.put(20, AttackInfo.darkspunk);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        base = new BaseStat(10,30,5,9,11,8,10,10);
        statusInfos.put(10, new MonsterStatusInformation(
            10, "mon_flyear", attacks, true, 11, 22, elements, base));

        // 011 NOSGEL
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.kick);
        attacks.put(5, AttackInfo.tooth);
        attacks.put(10, AttackInfo.leafgust);
        attacks.put(20, AttackInfo.darkspunk);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        base = new BaseStat(11,30,5,9,11,8,10,10);
        statusInfos.put(11, new MonsterStatusInformation(
            11, "mon_nosgel", attacks, true, 12, 44, elements, base));

        // 012 WINGEL
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.kick);
        attacks.put(5, AttackInfo.tooth);
        attacks.put(10, AttackInfo.leafgust);
        attacks.put(20, AttackInfo.darkspunk);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        base = new BaseStat(12,30,5,9,11,8,10,10);
        statusInfos.put(12, new MonsterStatusInformation(
            12, "mon_wingel", attacks, false, 0, 0, elements, base));

        // 013 EGGOLOMIA
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.sprinkle);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        base = new BaseStat(13,30,5,9,11,8,10,10);
        statusInfos.put(13, new MonsterStatusInformation(
            13, "mon_eggolomia", attacks, true, 14, 21, elements, base));

        // 014 HOOTLOCK
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(10, AttackInfo.leafgust);
        elements = new Array<Element>();
        elements.add(Element.AIR);
        base = new BaseStat(14,30,5,9,11,8,10,10);
        statusInfos.put(14, new MonsterStatusInformation(
            14, "mon_hootlock", attacks, false, 0, 0, elements, base));

        // 016 COOTIGER
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.tooth);
        attacks.put(10, AttackInfo.leafgust);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(16,30,5,9,11,8,10,10);
        statusInfos.put(16, new MonsterStatusInformation(
            16, "mon_cootiger", attacks, false, 0, 0, elements, base));

        // 027 TOFF
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.kick);
        attacks.put(5, AttackInfo.tooth);
        attacks.put(10, AttackInfo.earth);
        attacks.put(20, AttackInfo.eartha);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(27,30,5,9,11,8,10,10);
        statusInfos.put(27, new MonsterStatusInformation(
            27, "mon_toff", attacks, true, 28, 19, elements, base));

        // 028 BONFICO
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(1, AttackInfo.kick);
        attacks.put(5, AttackInfo.tooth);
        attacks.put(10, AttackInfo.earth);
        attacks.put(20, AttackInfo.eartha);
        elements = new Array<Element>();
        elements.add(Element.EARTH);
        base = new BaseStat(28,30,5,9,11,8,10,10);
        statusInfos.put(28, new MonsterStatusInformation(
            28, "mon_bonfico", attacks, false, 0, 0, elements, base));

    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public static MonsterInformation getInstance() {
        if(instance == null) instance = new MonsterInformation();
        return instance;
    }
}
