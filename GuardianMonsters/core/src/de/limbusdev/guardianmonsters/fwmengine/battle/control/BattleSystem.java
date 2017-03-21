package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Iterator;
import java.util.Observable;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.monsters.Team;
import de.limbusdev.guardianmonsters.utils.Constant;
import de.limbusdev.guardianmonsters.utils.DebugOutput;
import de.limbusdev.guardianmonsters.model.gamestate.GameState;

/**
 * Created by georg on 21.11.16.
 */

public class BattleSystem extends Observable {

    public static String TAG = BattleSystem.class.getSimpleName();

    public static final boolean LEFT = true;
    public static final boolean RIGHT = false;

    private Team leftTeam;
    private Team rightTeam;

    private ArrayMap<Integer,Monster> leftInBattle;
    private ArrayMap<Integer,Monster> rightInBattle;

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


    public BattleSystem(Team left, Team right, CallbackHandler callbackHandler) {

        this.callbackHandler = callbackHandler;

        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;

        leftTeam = left;
        rightTeam = right;
        aiPlayer = new AIPlayer();

        // Use two queues, to see the coming round in the widget
        currentRound = new Array<>();
        nextRound = new Array<>();
        leftInBattle = new ArrayMap<>();
        rightInBattle = new ArrayMap<>();

        addFitMonstersToBattleField(leftTeam,LEFT);
        addFitMonstersToBattleField(rightTeam,RIGHT);

        newRound();
    }

    /**
     * Looks for monsters with HP > 0 and adds the first 3 to the battle
     * @param team
     * @param side
     */
    private void addFitMonstersToBattleField(Team team, boolean side) {

        ArrayMap<Integer,Monster> inBattle;
        if(side == LEFT) {
            inBattle = leftInBattle;
        } else {
            inBattle = rightInBattle;
        }

        inBattle.putAll(team.getFitTeam());
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

        // Calculate Ability
        latestAttackReport = MonsterManager.calcAttack(
            getActiveMonster(), target, getActiveMonster().abilityGraph.learntAbilities.get(attack));
        callbackHandler.onAttack(getActiveMonster(), target, getActiveMonster().abilityGraph.learntAbilities.get(attack), latestAttackReport);
    }

    public void applyAttack() {
        MonsterManager.apply(latestAttackReport);
        nextMonster();
        checkKO();
    }

    public void attack(int attack) {
        attack(chosenTarget,attack);
    }

    public void attack() {
        attack(chosenTarget, chosenAttack);
    }

    /**
     * The monster decides to not attack and instead raised it's defense values for one round
     */
    public void defend() {
        latestAttackReport = MonsterManager.calcDefense(getActiveMonster());
        callbackHandler.onDefense(getActiveMonster());
    }

    /**
     * Monster does nothing. Use when e.g. using an item.
     */
    public void doNothing() {
        callbackHandler.onDoingNothing(getActiveMonster());
    }

    public void nextMonster() {
        nextRound.insert(0,currentRound.pop());
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

    public void continueBattle() {

        // Check if one team is KO
        if(isTeamKO(leftTeam) || isTeamKO(rightTeam)) {
            callbackHandler.onBattleEnds(isTeamKO(leftTeam));
        } else {

            if (rightTeam.containsValue(getActiveMonster(), false)) {
                // It's AI's turn
                letAItakeTurn();
            } else {
                // It's player's turn
                callbackHandler.onPlayersTurn();
            }
        }
    }

    private boolean isTeamKO(ArrayMap<Integer,Monster> team) {
        boolean isKO = true;

        for(Monster m : team.values()) {
            isKO = isKO && m.stat.isKO();
        }

        return isKO;
    }

    /**
     * Initializes a new round, adding all monsters of a team to the queue and sorting the queue by speed
     */
    private void newRound() {

        currentRound = nextRound;
        nextRound = new Array<>();

        // Sort monsters by speed
        sortQueue(currentRound);

        System.out.println("\n=== NEW ROUND ===");
        DebugOutput.printRound(currentRound);
    }

    private void checkKO() {
        Iterator<Monster> it = currentRound.iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.stat.isKO()) {
                it.remove();
                if(rightTeam.containsValue(m,false)) {
                    giveEXPtoWinners(m);
                }
                callbackHandler.onMonsterKilled(m);
            }
        }

        it = nextRound.iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.stat.isKO()) {
                it.remove();
                if(rightTeam.containsValue(m,false)) {
                    giveEXPtoWinners(m);
                }
                callbackHandler.onMonsterKilled(m);
            }
        }
    }

    private void giveEXPtoWinners(Monster defeatedMonster) {
        for(Monster m : leftInBattle.values()) {
            if(m.stat.isFit()) {
                float opponentFactor = 1f * defeatedMonster.stat.getLevel() / m.stat.getLevel();
                int EXP = MathUtils.floor(Constant.BASE_EXP * defeatedMonster.stat.getLevel() / 6f * opponentFactor);
                boolean levelUp = m.stat.earnEXP(EXP);
                if(levelUp) {
                    callbackHandler.onLevelup(m);
                }
            }
        }
    }

    /**
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    public void letAItakeTurn() {
        if(!rightTeam.containsValue(getActiveMonster(),false)) {
            throw new IllegalStateException(TAG +
                " AI can't take turn. The first monster in queue" +
                "is not in it's team.");
        }
        aiPlayer.turn();
    }

    /**
     * Sorts all left monsters of the round by speed
     */
    private void sortQueue(Array<Monster> queue) {
        queue.sort(new MonsterSpeedComparator());
        DebugOutput.printRound(queue);

        try {
            callbackHandler.onQueueUpdated();
        } catch(Exception e) {
            // TODO
        }
    }

    /**
     * Swaps two monsters
     * @param newMonster
     */
    public void replaceActiveMonster(Monster newMonster) {

        Monster replaced = currentRound.pop();
        boolean side;

        ArrayMap<Integer,Monster> inBattle;
        if(leftInBattle.containsValue(replaced,false)) {
            inBattle = leftInBattle;
            side = true;
        } else {
            inBattle = rightInBattle;
            side = false;
        }

        inBattle.put(inBattle.getKey(replaced,false),newMonster);

        currentRound.add(newMonster);
        nextMonster();

        setChanged();
        notifyObservers(side);
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
            int att = MathUtils.random(0,m.abilityGraph.learntAbilities.size-1);
            chooseTarget();
            setChosenAttack(att);
            attack();
        }

        private void chooseTarget() {
            boolean foundTarget = false;
            Monster target;
            while(!foundTarget) {
                target = leftInBattle.get(MathUtils.random(0,leftInBattle.size-1));
                if(target.stat.isFit()) {
                    foundTarget = true;
                    setChosenTarget(target);
                }
            }
        }
    }

    public ArrayMap<Integer,Monster> getLeftTeam() {
        return leftTeam;
    }

    public ArrayMap<Integer,Monster> getRightTeam() {
        return rightTeam;
    }

    public ArrayMap<Integer,Monster> getLeftInBattle() {
        return leftInBattle;
    }

    public ArrayMap<Integer,Monster> getRightInBattle() {
        return rightInBattle;
    }

    /**
     *
     * @param m
     * @return  true == LEFT, false == RIGHT
     */
    public boolean getBattleFieldSideFor(Monster m) {
        if(leftInBattle.containsValue(m,false)) {
            return LEFT;
        } else {
            if(rightInBattle.containsValue(m,false)) {
                return RIGHT;
            } else {
                throw new IllegalArgumentException("Monster " + m.INSTANCE_ID + " is not in battle right now.");
            }
        }
    }

    public int getBattleFieldPositionFor(Monster m) {
        if(getBattleFieldSideFor(m) == LEFT) {
            return leftInBattle.getKey(m,false);
        } else {
            return rightInBattle.getKey(m,false);
        }
    }

    // INNER INTERFACE
    public static abstract class CallbackHandler {
        public void onMonsterKilled(Monster m){}
        public void onQueueUpdated(){}
        public void onAttack(Monster attacker, Monster target, Ability ability, AttackCalculationReport rep){}
        public void onDefense(Monster defensiveMonster){}
        public void onPlayersTurn(){}
        public void onBattleEnds(boolean winnerSide){}
        public void onDoingNothing(Monster monster){}
        public void onLevelup(Monster m){}
    }
}
