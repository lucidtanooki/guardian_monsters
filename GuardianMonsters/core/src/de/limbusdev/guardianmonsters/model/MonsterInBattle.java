package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 15.08.16.
 */
public class MonsterInBattle {
    public Monster monster;


    // ------------------------------------------------------------------------------- BATTLE STATUS
    public boolean attackStarted;
    public boolean KO;
    public boolean attackChosen;
    public final boolean battleFieldSide;
    public int     battleFieldPosition;

    public Attack nextAttack;
    public int    nextTarget;

    public MonsterInBattle(Monster mon, int pos, boolean side) {
        monster = mon;
        battleFieldPosition = pos;
        battleFieldSide = side;

        // BATTLE
        this.attackStarted = false;
        this.KO = false;
        this.attackChosen = false;
        this.nextTarget = 0;
        this.battleFieldPosition = pos;

    }

    /**
     * Call this method when this monster is added to a battle action waiting queue
     * @param targetPosition
     * @param attackIndex
     */
    public void prepareForAttack(int targetPosition, int attackIndex) {
        this.nextTarget = targetPosition;
        this.nextAttack = monster.attacks.get(attackIndex);
        this.attackStarted = false;
        this.attackChosen = true;
    }

    public void newRound() {
        if(monster.getHP() == 0) KO = true;
        attackChosen = false;
    }


    public void update() {
        monster.update();
    }
}
