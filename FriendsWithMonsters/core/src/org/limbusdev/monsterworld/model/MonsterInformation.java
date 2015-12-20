package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 20.12.15.
 */
public class MonsterInformation {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<String> monsterNames;
    private static MonsterInformation instance;
    /* ........................................................................... CONSTRUCTOR .. */
    private MonsterInformation() {
        this.monsterNames = new Array<String>();
        for(int i=0;i< GlobalSettings.MONSTER_SPRITES;i++) monsterNames.add("");
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
        monsterNames.set(13, "Big eyed Katago");
        monsterNames.set(14, "Cath");
        monsterNames.set(15, "Dogh");
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public static MonsterInformation getInstance() {
        if(instance == null) instance = new MonsterInformation();
        return instance;
    }
}
