package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 12.12.15.
 */
public class BattleFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private static Array<Monster> monsters;
    private static BattleFactory instance;
    /* ........................................................................... CONSTRUCTOR .. */

    private BattleFactory() {
        this.monsters = new Array<Monster>();
        for(int i=0; i< GlobalSettings.MONSTER_SPRITES; i++) {
            Monster mon = new Monster();
            mon.ID = i+1;
            monsters.add(new Monster());
        }
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public Monster createMonster(int ID) {
        Monster monster = new Monster();
        monster.ID = ID;
        this.monsters.add(monster);
        return monster;
    }

    public static BattleFactory getInstance() {
        if(instance == null) instance = new BattleFactory();
        return instance;
    }
}
