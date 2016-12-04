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

    private Array<Monster> leftTeam;
    private Array<Monster> rightTeam;

    private AIPlayer aiPlayer;

    private Array<Monster> currentRound;
    private Array<Monster> nextRound;

    private CallbackHandler callbackHandler;

    private Monster chosenTarget;
    private int chosenAttack;
    private boolean choiceComplete;
    private boolean targetChosen;
    private boolean attackChosen;

    private AttackCalculationReport latestAttackReport;

    // Status values for GUI


    public BattleSystem(Array<Monster> left, Array<Monster> right, CallbackHandler callbackHandler) {

        this.callbackHandler = callbackHandler;

        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;

        leftTeam = left;
        rightTeam = right;
        aiPlayer = new AIPlayer();

        // Use two queues, to see the coming round in the widget
        currentRound = new Array<Monster>();
        nextRound = new Array<Monster>();

        nextRound.addAll(left);
        nextRound.addAll(right);

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
        latestAttackReport = MonsterManager.calcAttack(
            getActiveMonster(), target, getActiveMonster().attacks.get(attack));
        callbackHandler.onAttack(getActiveMonster(), target, getActiveMonster().attacks.get(attack), latestAttackReport);

        // Remove active monster from current round and add it to next round
        nextMonster();

        checkKO();

        // Sort in case current speed values have changed
        reSortQueues();
    }

    public void applyAttack() {
        MonsterManager.apply(latestAttackReport);
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

        callbackHandler.onQueueUpdated();
    }

    public boolean continueBattle() {
        if(rightTeam.contains(getActiveMonster(),false)) {
            // It's AI's turn
            letAItakeTurn();
            return false;
        } else {
            // It's player's turn
            callbackHandler.onPlayersTurn();
            return true;
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
        if(!rightTeam.contains(getActiveMonster(),false)) {
            throw new IllegalStateException(TAG +
                " AI can't take turn. The first monster in queue" +
                "is not in it's team.");
        }
        aiPlayer.turn();
    }

    /**
     * Sorts all left monsters of the round by speed
     */
    private void reSortQueues() {
        System.out.println("-- Current Round --");
        currentRound.sort(new MonsterSpeedComparator());
        DebugOutput.printRound(currentRound);

        System.out.println("-- Next Round --");
        nextRound.sort(new MonsterSpeedComparator());
        DebugOutput.printRound(nextRound);

        try {
            callbackHandler.onQueueUpdated();
        } catch(Exception e) {
            // TODO
        }
    }

    public Array<Monster> getCurrentRound() {
        return currentRound;
    }

    public Array<Monster> getNextRound() {
        return nextRound;
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
            for(Monster h : leftTeam) {
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
        public void onAttack(Monster attacker, Monster target, Attack attack, AttackCalculationReport rep);
        public void onPlayersTurn();
    }
}
