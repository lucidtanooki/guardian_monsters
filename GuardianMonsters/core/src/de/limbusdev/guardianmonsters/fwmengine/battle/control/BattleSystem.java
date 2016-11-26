package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.DebugOutput;

/**
 * Created by georg on 21.11.16.
 */

public class BattleSystem {

    public static final int REMOVED_MONSTER=0;
    public static final int CHANGED_POSITION=1;
    public static final int NEXT_MONSTER=2;

    private Array<Monster> herosTeam;
    private Array<Monster> opponentsTeam;

    private AIPlayer aiPlayer;

    private Array<Monster> currentRound;
    private Array<Monster> nextRound;

    private CallbackHandler callbackHandler;

    private Monster chosenTarget;
    private int chosenAttack;

    // Status values for GUI


    public BattleSystem(Array<Monster> hero, Array<Monster> opponent, CallbackHandler callbackHandler) {

        this.callbackHandler = callbackHandler;

        herosTeam = hero;
        opponentsTeam = opponent;
        aiPlayer = new AIPlayer();

        // Use two queues, to see the coming round in the widget
        currentRound = new Array<Monster>();
        nextRound = new Array<Monster>();

        nextRound.addAll(hero);
        nextRound.addAll(opponent);

        newRound();
    }


    // .............................................................................. battle methods
    public Monster getActiveMonster() {
        return currentRound.get(currentRound.size - 1);
    }

    /**
     * Attacks the given Monster with the currently active monster
     *
     * @param target
     * @param attack
     */
    public void attack(Monster target, int attack) {
        // Calculate Attack
        AttackCalculationReport rep = MonsterManager.calcAttack(
            getActiveMonster(), target, getActiveMonster().attacks.get(attack));
        callbackHandler.onAttack(getActiveMonster(), target, getActiveMonster().attacks.get(attack));

        // Remove active monster from current round and add it to next round
        nextRound.add(currentRound.pop());
        callbackHandler.onNextTurn();

        checkKO();

        // Sort in case current speed values have changed
        reSortQueues();

        if (currentRound.size == 0) {
            newRound();
        }

        // Check, if next monster is from AI
        if (opponentsTeam.contains(getActiveMonster(), true)) {
            aiPlayer.turn();
        }
    }

    public void attack(int attack) {
        attack(chosenTarget,attack);
    }

    public void attack() {
        attack(chosenTarget, chosenAttack);
    }

    /**
     * Initializes a new round, adding all monsters of a team to the queue and sorting the queue by speed
     */
    private void newRound() {

        currentRound = nextRound;
        nextRound = new Array<Monster>();

        // Sort monsters by speed
        reSortQueues();

        System.out.println("\n=== NEW ROUND ===");
        DebugOutput.printRound(currentRound);
    }

    private void checkKO() {
        Iterator<Monster> it = currentRound.iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.getHP() == 0) {
                it.remove();
                callbackHandler.onMonsterKilled(m);
            }
        }

        it = nextRound.iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.getHP() == 0) {
                it.remove();
                callbackHandler.onMonsterKilled(m);
            }
        }
    }

    /**
     * Sorts all left monsters of the round by speed
     */
    private void reSortQueues() {
        currentRound.sort(new MonsterSpeedComparator());
        nextRound.sort(new MonsterSpeedComparator());
        callbackHandler.onQueueUpdated();
    }

    public Array<Monster> getMonsterQueue() {
        Array<Monster> queue = new Array<Monster>();
        queue.addAll(currentRound);
        queue.addAll(nextRound);
        return queue;
    }

    public void setChosenTarget(Monster target) {
        this.chosenTarget = target;
    }

    public void setChosenAttack(int attack) {
        this.chosenAttack = attack;
    }

    // INNER CLASS

    private class AIPlayer {

        public AIPlayer() {
            // TODO
        }

        public void turn() {
            System.out.println("\n### AI's turn ###");
            Monster m = getActiveMonster();
            int att = MathUtils.random(0,m.attacks.size-1);
            Array<Monster> targets = new Array<Monster>();
            for(Monster h : herosTeam) {
                if(h.getHP() > 0) {
                    targets.add(h);
                }
            }
            Monster target = targets.get(MathUtils.random(0,targets.size-1));
            attack(target,att);
        }
    }


    // INNER INTERFACE
    public interface CallbackHandler {
        public void onNextTurn();
        public void onMonsterKilled(Monster m);
        public void onQueueUpdated();
        public void onAttack(Monster attacker, Monster target, Attack attack);
    }
}
