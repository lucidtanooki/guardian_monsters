package de.limbusdev.guardianmonsters.model;

/**
 * Created by georg on 15.08.16.
 */
public class MonsterInBattle {
    public Monster monster;


    // ------------------------------------------------------------------------------- BATTLE STATUS
    public boolean ready;
    public boolean KO;
    public boolean attackingRightNow;   // whether monster is involved in battle right now
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
        this.ready = false;
        this.KO = false;
        this.attackingRightNow = false;
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
        this.ready = false;
        this.attackChosen = true;
    }

    /**
     * Call this method as soon as this monster gets removed from the queue
     */
    public void finishAttack() {
        attackChosen = false;
        attackingRightNow = false;
    }

    /**
     * Call this method when monster reaches first slot in Queue
     */
    public void startAttack() {
        this.attackingRightNow = true;
    }

    public void update() {
        monster.update();
    }
}
