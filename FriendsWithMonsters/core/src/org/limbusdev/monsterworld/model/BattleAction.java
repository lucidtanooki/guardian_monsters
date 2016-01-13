package org.limbusdev.monsterworld.model;

/**
 * Created by georg on 13.01.16.
 */
public class BattleAction {
    /* ............................................................................ ATTRIBUTES .. */
    public int attackerPosition;
    public int defenderPosition;


    /* ........................................................................... CONSTRUCTOR .. */
    public BattleAction(int attackerPosition, int defenderPosition) {
        this.attackerPosition = attackerPosition;
        this.defenderPosition = defenderPosition;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
