package de.limbusdev.guardianmonsters.guardians.battle;

import com.badlogic.gdx.math.MathUtils;

import java.util.Iterator;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

import static de.limbusdev.guardianmonsters.guardians.Constant.HERO;
import static de.limbusdev.guardianmonsters.guardians.Constant.OPPONENT;

/**
 * @author Georg Eckert 2017
 */

public class BattleSystem
{
    public static String TAG = BattleSystem.class.getSimpleName();

    private AIPlayer aiPlayer;

    private Callbacks callbacks;

    private de.limbusdev.guardianmonsters.guardians.battle.BattleQueue queue;
    private AGuardian chosenTarget;
    private AttackCalculationReport latestAttackReport;
    private BattleResult result;
    private int chosenAttack;
    private boolean choiceComplete;
    private boolean targetChosen;
    private boolean attackChosen;

    // ................................................................................. CONSTRUCTOR
    public BattleSystem(Team left, Team right, Callbacks callbacks)
    {

        this.callbacks = callbacks;

        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;

        aiPlayer = new AIPlayer();

        queue = new de.limbusdev.guardianmonsters.guardians.battle.BattleQueue(left,right);

        result = new BattleResult(left, null);
    }

    // .............................................................................. battle methods
    public AGuardian getActiveMonster() {
        return queue.peekNext();
    }

    /**
     * Attacks the given Monster with the currently active monster
     *
     * @param target
     * @param attack
     */
    public void attack(AGuardian target, int attack) {

        // Throw exception if target or attack are unset
        if(!choiceComplete) {
            throw new IllegalStateException(TAG + " you forgot to set the " + (targetChosen ? "attack" : "target"));
        }

        // Calculate Ability
        Ability ability= getActiveMonster().getAbilityGraph().getActiveAbilities().get(attack);
        AGuardian attacker = getActiveMonster();
        latestAttackReport = BattleCalculator.calcAttack(attacker, target, ability);
        callbacks.onAttack(attacker, target, ability, latestAttackReport);
    }

    public void applyAttack() {
        BattleCalculator.apply(latestAttackReport);
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
        latestAttackReport = BattleCalculator.calcDefense(getActiveMonster());
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

    public void continueBattle()
    {
        // Check if one team is KO
        if(queue.getCombatTeamLeft().isKO() || queue.getCombatTeamRight().isKO()) {
            callbacks.onBattleEnds(queue.getCombatTeamLeft().isKO());
        } else {
            if (queue.peekNextSide() == OPPONENT) {
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
        Iterator<AGuardian> it = queue.getCurrentRound().iterator();
        while (it.hasNext()) {
            AGuardian m = it.next();
            if (m.getIndividualStatistics().isKO()) {
                it.remove();
                if(queue.getRight().containsValue(m,false)) {
                    giveEXPtoWinners(m);
                }
                callbacks.onMonsterKilled(m);
            }
        }

        it = queue.getNextRound().iterator();
        while (it.hasNext()) {
            AGuardian m = it.next();
            if (m.getIndividualStatistics().isKO()) {
                it.remove();
                if(queue.getRight().containsValue(m,false)) {
                    giveEXPtoWinners(m);
                }
                callbacks.onMonsterKilled(m);
            }
        }
    }

    private void giveEXPtoWinners(AGuardian defeatedGuardian)
    {
        for(AGuardian m : queue.getCombatTeamLeft().values())
        {
            if(m.getIndividualStatistics().isFit())
            {
                int EXP = BattleCalculator.calculateEarnedEXP(m, defeatedGuardian);

                result.gainEXP(m, EXP);
                boolean levelUp = m.getIndividualStatistics().earnEXP(EXP);
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
    public void letAItakeTurn()
    {
        if(queue.peekNextSide() == HERO) {
            throw new IllegalStateException(TAG +
                " AI can't take turn. The first monster in queue is not in it's team.");
        }
        aiPlayer.turn();
    }


    /**
     * Swaps two monsters
     * @param newGuardian
     */
    public void replaceActiveMonster(AGuardian newGuardian) {
        AGuardian replaced = queue.exchangeNext(newGuardian);
        nextMonster();
    }

    public void setChosenTarget(AGuardian target) {
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
            AGuardian m = getActiveMonster();
            int att = MathUtils.random(0,m.getAbilityGraph().getActiveAbilities().size-1);
            chooseTarget();
            setChosenAttack(att);
            attack();
        }

        private void chooseTarget() {
            boolean foundTarget = false;
            AGuardian target;
            while(!foundTarget) {
                target = queue.getCombatTeamLeft().getRandomFitMember();
                if(target.getIndividualStatistics().isFit()) {
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
        public void onMonsterKilled(AGuardian m){}
        public void onAttack(AGuardian attacker, AGuardian target, Ability ability, AttackCalculationReport rep){}
        public void onDefense(AGuardian defensiveGuardian){}
        public void onPlayersTurn(){}
        public void onBattleEnds(boolean winnerSide){}
        public void onDoingNothing(AGuardian guardian){}
        public void onLevelup(AGuardian m){}
    }
}
