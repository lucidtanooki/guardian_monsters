package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import com.badlogic.gdx.math.MathUtils;

import java.util.Iterator;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleResult;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.Constant;

import static de.limbusdev.guardianmonsters.Constant.LEFT;
import static de.limbusdev.guardianmonsters.Constant.RIGHT;

/**
 * @author Georg Eckert 2017
 */

public class BattleSystem {

    public static String TAG = BattleSystem.class.getSimpleName();

    private AIPlayer aiPlayer;

    private Callbacks callbacks;

    private BattleQueue queue;
    private Monster chosenTarget;
    private AttackCalculationReport latestAttackReport;
    private BattleResult result;
    private int chosenAttack;
    private boolean choiceComplete;
    private boolean targetChosen;
    private boolean attackChosen;

    // ................................................................................. CONSTRUCTOR
    public BattleSystem(Team left, Team right, Callbacks callbacks) {

        this.callbacks = callbacks;

        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;

        aiPlayer = new AIPlayer();

        queue = new BattleQueue(left,right);

        result = new BattleResult(left, null);
    }

    // .............................................................................. battle methods
    public Monster getActiveMonster() {
        return queue.peekNext();
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
        Ability ability= getActiveMonster().abilityGraph.learntAbilities.get(attack);
        Monster attacker = getActiveMonster();
        latestAttackReport = MonsterManager.calcAttack(attacker, target, ability);
        callbacks.onAttack(attacker, target, ability, latestAttackReport);
    }

    public void applyAttack() {
        MonsterManager.apply(latestAttackReport);
        nextMonster();
        checkKO();
    }

    public void attack() {
        attack(chosenTarget, chosenAttack);
    }

    /**
     * The monster decides to not attack and instead raise it's defense values for one round
     */
    public void defend() {
        latestAttackReport = MonsterManager.calcDefense(getActiveMonster());
        callbacks.onDefense(getActiveMonster());
    }

    /**
     * Monster does nothing. Use when e.g. using an item.
     */
    public void doNothing() {
        callbacks.onDoingNothing(getActiveMonster());
    }

    public void nextMonster() {
        queue.next();
        chosenAttack = 0;
        chosenTarget = null;
        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;
    }

    public void continueBattle() {
        // Check if one team is KO
        if(queue.getCombatTeamLeft().isKO() || queue.getCombatTeamRight().isKO()) {
            callbacks.onBattleEnds(queue.getCombatTeamLeft().isKO());
        } else {
            if (queue.peekNextSide() == RIGHT) {
                // It's AI's turn
                letAItakeTurn();
            } else {
                // It's player's turn
                callbacks.onPlayersTurn();
            }
        }
    }

    /**
     * Checks if a monster has been defeated during the last attack
     */
    private void checkKO() {
        Iterator<Monster> it = queue.getCurrentRound().iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.stat.isKO()) {
                it.remove();
                if(queue.getRight().containsValue(m,false)) {
                    giveEXPtoWinners(m);
                }
                callbacks.onMonsterKilled(m);
            }
        }

        it = queue.getNextRound().iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.stat.isKO()) {
                it.remove();
                if(queue.getRight().containsValue(m,false)) {
                    giveEXPtoWinners(m);
                }
                callbacks.onMonsterKilled(m);
            }
        }
    }

    private void giveEXPtoWinners(Monster defeatedMonster) {
        for(Monster m : queue.getCombatTeamLeft().values()) {
            if(m.stat.isFit()) {
                float opponentFactor = 1f * defeatedMonster.stat.getLevel() / m.stat.getLevel();
                int EXP = MathUtils.floor(Constant.BASE_EXP * defeatedMonster.stat.getLevel() / 6f * opponentFactor);
                result.gainEXP(m, EXP);
                boolean levelUp = m.stat.earnEXP(EXP);
                if(levelUp) {
                    callbacks.onLevelup(m);
                }
            }
        }
    }

    /**
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    public void letAItakeTurn() {
        if(queue.peekNextSide() == LEFT) {
            throw new IllegalStateException(TAG +
                " AI can't take turn. The first monster in queue is not in it's team.");
        }
        aiPlayer.turn();
    }


    /**
     * Swaps two monsters
     * @param newMonster
     */
    public void replaceActiveMonster(Monster newMonster) {
        Monster replaced = queue.exchangeNext(newMonster);
        nextMonster();
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
                target = queue.getCombatTeamLeft().getRandomFitMember();
                if(target.stat.isFit()) {
                    foundTarget = true;
                    setChosenTarget(target);
                }
            }
        }
    }

    public BattleResult getResult() {
        return result;
    }

    public BattleQueue getQueue() {
        return queue;
    }

    // INNER INTERFACE
    public static abstract class Callbacks
    {
        public void onMonsterKilled(Monster m){}
        public void onAttack(Monster attacker, Monster target, Ability ability, AttackCalculationReport rep){}
        public void onDefense(Monster defensiveMonster){}
        public void onPlayersTurn(){}
        public void onBattleEnds(boolean winnerSide){}
        public void onDoingNothing(Monster monster){}
        public void onLevelup(Monster m){}
    }
}
