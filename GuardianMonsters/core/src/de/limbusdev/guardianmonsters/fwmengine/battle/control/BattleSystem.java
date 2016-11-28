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

    public static String TAG = BattleSystem.class.getSimpleName();

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
    private boolean choiceComplete;
    private boolean targetChosen;
    private boolean attackChosen;

    // Status values for GUI


    public BattleSystem(Array<Monster> hero, Array<Monster> opponent, CallbackHandler callbackHandler) {

        this.callbackHandler = callbackHandler;

        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;

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

        // Throw exception if target or attack are unset
        if(!choiceComplete) {
            throw new IllegalStateException(TAG + " you forgot to set the " + (targetChosen ? "attack" : "target"));
        }

        // Calculate Attack
        AttackCalculationReport rep = MonsterManager.calcAttack(
            getActiveMonster(), target, getActiveMonster().attacks.get(attack));
        callbackHandler.onAttack(getActiveMonster(), target, getActiveMonster().attacks.get(attack));

        // Remove active monster from current round and add it to next round
        nextMonster();
        callbackHandler.onNextTurn();

        checkKO();

        // Sort in case current speed values have changed
        reSortQueues();
    }

    public void attack(int attack) {
        attack(chosenTarget,attack);
    }

    public void attack() {
        attack(chosenTarget, chosenAttack);
    }

    public void nextMonster() {
        nextRound.add(currentRound.pop());
        chosenAttack = 0;
        chosenTarget = null;
        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;

        if (currentRound.size == 0) {
            newRound();
        }

        if(opponentsTeam.contains(getActiveMonster(),false)) {
            // It's AI's turn
            letAItakeTurn();
        } else {
            // It's player's turn
            callbackHandler.onPlayersTurn();
        }
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
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    public void letAItakeTurn() {
        if(!opponentsTeam.contains(getActiveMonster(),false)) {
            throw new IllegalStateException(TAG + " AI can't take turn. The first monster in queue" +
                "is not in it's team.");
        }
        aiPlayer.turn();
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
        targetChosen = true;
        choiceComplete = targetChosen && attackChosen;
    }

    public void setChosenAttack(int attack) {
        this.chosenAttack = attack;
        attackChosen = true;
        choiceComplete = targetChosen && attackChosen;
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
            setChosenTarget(targets.get(MathUtils.random(0,targets.size-1)));
            setChosenAttack(att);
            attack();
        }
    }


    // INNER INTERFACE
    public interface CallbackHandler {
        public void onNextTurn();
        public void onMonsterKilled(Monster m);
        public void onQueueUpdated();
        public void onAttack(Monster attacker, Monster target, Attack attack);
        public void onPlayersTurn();
    }
}
