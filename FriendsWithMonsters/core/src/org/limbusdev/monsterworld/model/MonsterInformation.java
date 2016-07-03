package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import org.limbusdev.monsterworld.enums.Element;
import org.limbusdev.monsterworld.utils.GS;

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

        this.statusInfos = new ArrayMap<Integer,MonsterStatusInformation>();

        // KROKI
        ArrayMap<Integer,Attack> attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5, AttackInfo.tripit);
        attacks.put(10, AttackInfo.water);
        attacks.put(20, AttackInfo.watera);
        statusInfos.put(1, new MonsterStatusInformation(
                1, "Kroki", attacks, Element.WATER, true, 2, 18));

        // DINVI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5,AttackInfo.tripit);
        attacks.put(10,AttackInfo.earth);
        attacks.put(20,AttackInfo.eartha);
        statusInfos.put(2, new MonsterStatusInformation(
                2, "Dinvi", attacks, Element.EARTH, true, 2, 18));

        // TOTOWI
        attacks = new ArrayMap<Integer, Attack>();
        attacks.put(5,AttackInfo.tripit);
        attacks.put(10,AttackInfo.facefold);
        statusInfos.put(3, new MonsterStatusInformation(
                3, "Totowi", attacks, Element.EARTH, false, 0, 0));

        // TARANYA
        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(4, new MonsterStatusInformation(
                4, "Taranya", attacks, Element.EARTH, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(5, new MonsterStatusInformation(
                5, "Sheelfish", attacks, Element.WATER, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(6, new MonsterStatusInformation(
                6, "Baleera", attacks, Element.EARTH, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(7, new MonsterStatusInformation(
                7, "Caterparan", attacks, Element.EARTH, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(8, new MonsterStatusInformation(
                8, "Toff", attacks, Element.EARTH, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(9, new MonsterStatusInformation(
                9, "Fingercrab", attacks, Element.WATER, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(10, new MonsterStatusInformation(
                10, "Springby", attacks, Element.EARTH, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(11, new MonsterStatusInformation(
                11, "Flyear", attacks, Element.AIR, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(12, new MonsterStatusInformation(
                12, "Tuxedo", attacks, Element.WATER, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(13, new MonsterStatusInformation(
                13, "Eggolomia", attacks, Element.AIR, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(14, new MonsterStatusInformation(
                14, "Big Eyed Ka", attacks, Element.AIR, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(15, new MonsterStatusInformation(
                15, "Cath", attacks, Element.EARTH, false, 0, 0));

        attacks = new ArrayMap<Integer, Attack>();
        statusInfos.put(16, new MonsterStatusInformation(
                16, "Dogh", attacks, Element.EARTH, false, 0, 0));
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public static MonsterInformation getInstance() {
        if(instance == null) instance = new MonsterInformation();
        return instance;
    }
}
