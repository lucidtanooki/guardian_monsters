package de.limbusdev.guardianmonsters.guardians.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Iterator;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * Correct Usage:
 *
 * 1. Create Teams using AGuardianFactory
 * 2. Initialize BattleSystem with these Teams
 * 3. BS.getActiveMonster()
 * 4. choose Target: BS.setChosenTaget(Target)
 * 5. choose Ability to use: BS.setChosenAbility(Ability)
 * 6. calculate Attack: BS.attack()
 * 7. apply calculated Attack: BS.applyAttack()
 * 8. continue: BS.continue()
 *
 * @author Georg Eckert 2017
 */

public class BattleSystem
{
    public static String TAG = BattleSystem.class.getSimpleName();

    private DefaultAIPlayer defaultAiPlayer;

    private Callbacks callbacks;

    private BattleQueue queue;
    private AGuardian chosenTarget;
    private ArrayMap<Integer,AGuardian> chosenArea;
    private AttackCalculationReport latestAttackReport;
    private Array<AttackCalculationReport> latestAreaAttackReports;
    private BattleResult result;
    private int chosenAttack;
    private boolean choiceComplete;
    private boolean targetChosen;
    private boolean attackChosen;
    private boolean areaChosen;
    private boolean wildEncounter;  // Guardians can be banned in wild encounters only

    // ................................................................................. CONSTRUCTOR
    public BattleSystem(Team left, Team right, Callbacks callbacks, boolean wildEncounter)
    {
        this.callbacks = callbacks;

        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;
        areaChosen = false;

        this.wildEncounter = wildEncounter;

        defaultAiPlayer = new DefaultAIPlayer();

        queue = new BattleQueue(left,right);

        result = new BattleResult(left, null);

        if (callbacks == null) System.out.println("Use 'setCallbacks(...) to set Callbacks later.'");
    }

    /**
     * If callbacks must be set later, usually only in debugging.
     * @param callbacks
     */
    public void setCallbacks(Callbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    // .............................................................................. battle methods
    public AGuardian getActiveMonster()
    {
        return queue.peekNext();
    }

    /**
     * Attacks the given Monster with the currently active monster
     *
     * @param target
     * @param attack
     */
    private void attack(AGuardian target, int attack)
    {
        // Throw exception if target or attack are unset
        if(!choiceComplete) {
            throw new IllegalStateException(TAG + " you forgot to set the " + (targetChosen ? "attack" : "target"));
        }

        // Calculate Ability
        Ability.aID aID = getActiveMonster().getAbilityGraph().getActiveAbilities().get(attack);
        Ability ability = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(aID);


        AGuardian attacker = getActiveMonster();
        latestAttackReport = BattleCalculator.INSTANCE.calcAttack(attacker, target, aID);
        callbacks.onAttack(attacker, target, ability, latestAttackReport);
    }

    private void attackArea(ArrayMap<Integer,AGuardian> targets, int attack)
    {
        if(!choiceComplete) {
            throw new IllegalStateException(TAG + " you forgot to set area, attack or targets.");
        }

        // Calculate Ability
        Ability.aID aID= getActiveMonster().getAbilityGraph().getActiveAbilities().get(attack);
        Ability ability = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(aID);


        AGuardian attacker = getActiveMonster();
        latestAreaAttackReports = new Array<>();
        for(AGuardian g : targets.values())
        {
            AttackCalculationReport report = BattleCalculator.INSTANCE.calcAttack(attacker, g, aID);
            latestAreaAttackReports.add(report);
        }
        callbacks.onAreaAttack(attacker, targets, ability, latestAreaAttackReports);
    }

    public void revive(AGuardian guardian)
    {
        queue.revive(guardian);
    }

    /**
     * Whether this application defeated a guardian
     * @return
     */
    public boolean applyAttack()
    {
        if(areaChosen) {
            for(AttackCalculationReport report : latestAreaAttackReports)
            {
                BattleCalculator.INSTANCE.apply(report);
            }
        } else {
            BattleCalculator.INSTANCE.apply(latestAttackReport);
        }
        return checkKO();
    }

    public void applyStatusEffect()
    {
        if(getActiveMonster().getIndividualStatistics().getStatusEffect() != IndividualStatistics.StatusEffect.HEALTHY) {
            BattleCalculator.INSTANCE.applyStatusEffect(getActiveMonster());
            callbacks.onApplyStatusEffect(getActiveMonster());
            checkKO();
        }

    }

    public AGuardian getRandomFitCombatant()
    {
        if(MathUtils.randomBoolean()) {
            return queue.getCombatTeamLeft().getRandomFitMember();
        } else {
            return queue.getCombatTeamRight().getRandomFitMember();
        }
    }

    public void attack()
    {
        // if status effect prevents normal attack calculation
        AGuardian activeGuardian = getActiveMonster();

        switch(activeGuardian.getIndividualStatistics().getStatusEffect())
        {
            case PETRIFIED:
                doNothing();
                return;
            case LUNATIC:
                if(MathUtils.randomBoolean(0.2f)) {
                    activeGuardian
                        .getIndividualStatistics()
                        .setStatusEffect(IndividualStatistics.StatusEffect.HEALTHY);
                } else {
                    // chose arbitrary target and ability
                    Ability.aID aID = activeGuardian.getAbilityGraph().getRandomActiveAbility();
                    Ability ability = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(aID);
                    int att = activeGuardian.getAbilityGraph().getActiveAbilities().getKey(aID, false);

                    if (ability.getAreaDamage()) {setChosenArea(queue.getRandomCombatTeam());}
                    else                    {setChosenTarget(getRandomFitCombatant());}

                    setChosenAttack(att);
                    break;
                }
            default: /*case: HEALTHY*/
                break;
        }

        // if no status effect prevents normal attack calculation
        if(areaChosen) {
            attackArea(chosenArea, chosenAttack);
        } else {
            attack(chosenTarget, chosenAttack);
        }
    }

    /**
     * The monster decides to not attack and instead raise it's defense values for one round
     */
    public void defend()
    {
        latestAttackReport = BattleCalculator.INSTANCE.calcDefense(getActiveMonster());
        callbacks.onDefense(getActiveMonster());
    }

    /**
     * Monster does nothing. Use when e.g. using an item.
     */
    public void doNothing()
    {
        callbacks.onDoingNothing(getActiveMonster());
    }

    public void nextMonster()
    {
        queue.next();
        chosenAttack = 0;
        chosenTarget = null;
        choiceComplete = false;
        targetChosen = false;
        attackChosen = false;
        areaChosen = false;
        chosenArea = null;
    }

    public void continueBattle()
    {
        // Check if one team is KO
        if(queue.getCombatTeamLeft().isKO() || queue.getCombatTeamRight().isKO()) {

            finishBattle();

        } else {

            if (queue.peekNextSide() == Constant.OPPONENT) {
                // It's AI's turn
                letAItakeTurn();
            } else {
                // It's player's turn
                callbacks.onPlayersTurn();
            }
        }
    }

    /**
     * Called, when one of the teams is KO
     */
    private void finishBattle()
    {
        queue.resetTeamsModifiedStats(Constant.LEFT);
        queue.resetTeamsModifiedStats(Constant.RIGHT);
        callbacks.onBattleEnds(!queue.getCombatTeamLeft().isKO());
    }

    /**
     * Checks if a monster has been defeated during the last attack
     */
    private boolean checkKO()
    {
        boolean guardianDefeated = false;
        Array<Iterator<AGuardian>> its;
        its = new Array<>();
        its.add(queue.getCurrentRound().iterator());
        its.add(queue.getNextRound().iterator());

        for(Iterator<AGuardian> it : its)
        {
            while (it.hasNext())
            {
                AGuardian m = it.next();
                if (m.getIndividualStatistics().isKO()) {

                    if (queue.getTeamSideFor(m) == Constant.RIGHT) {
                        giveEXPtoWinners(m);
                        if(queue.getRight().teamKO()) {
                            it.remove();
                            callbacks.onMonsterKilled(m);
                        } else {
                            randomlyReplaceDefeatedGuardian(Constant.RIGHT, m);
                        }
                    } else {
                        it.remove();
                        callbacks.onMonsterKilled(m);
                    }
                    guardianDefeated = true;
                }
            }
        }

        return guardianDefeated;
    }



    private void giveEXPtoWinners(AGuardian defeatedGuardian)
    {
        for(AGuardian m : queue.getCombatTeamLeft().values())
        {
            if(m.getIndividualStatistics().isFit())
            {
                int EXP = BattleCalculator.INSTANCE.calculateEarnedEXP(m, defeatedGuardian);

                result.gainEXP(m, EXP);
            }
        }
    }

    /**
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    public void letAItakeTurn()
    {
        if(queue.peekNextSide() == Constant.HERO) {
            throw new IllegalStateException(TAG +
                " AI can't take turn. The first monster in queue is not in it's team.");
        }
        defaultAiPlayer.turn();
    }


    /**
     * Swaps two monsters
     * @param newGuardian
     */
    public void replaceActiveMonster(AGuardian newGuardian)
    {
        int fieldPos = queue.getFieldPositionFor(getActiveMonster());
        getActiveMonster().deleteObservers();
        AGuardian replaced = queue.exchangeActive(newGuardian);
        callbacks.onGuardianSubstituted(replaced, newGuardian, fieldPos);
    }

    public void banWildGuardian(AGuardian bannedGuardian, ChakraCrystalItem item)
    {
        if(!isWildEncounter()) {
            throw new IllegalStateException("Guardians can be banned in wild encounters only!");
        }

        if(queue.getCombatTeamRight().countFitMembers() > 1) {
            throw new IllegalStateException("Banning Guardians is possible only, when there is only 1 Guardian left.");
        }

        int fieldPos = queue.getFieldPositionFor(bannedGuardian);

        callbacks.onBanningWilduardian(bannedGuardian, item, fieldPos);
    }

    public void banWildGuardian(ChakraCrystalItem item)
    {
        if(!isWildEncounter()) {
            throw new IllegalStateException("Guardians can be banned in wild encounters only!");
        }

        if(queue.getCombatTeamRight().countFitMembers() > 1) {
            throw new IllegalStateException("Banning Guardians is possible only, when there is only 1 Guardian left.");
        }

        AGuardian lastGuardian = queue.getCombatTeamRight().getRandomFitMember();
        banWildGuardian(lastGuardian, item);
    }

    /**
     * Mainly for replacing defeated opponent Guardians
     */
    private void randomlyReplaceDefeatedGuardian(boolean side, AGuardian defeated) throws IllegalStateException
    {
        int fieldPos = queue.getFieldPositionFor(defeated);
        defeated.deleteObservers();
        AGuardian substitute = queue.randomlyExchangeDefeated(defeated);
        callbacks.onReplacingDefeatedGuardian(defeated, substitute, fieldPos);
    }

    public void setChosenTarget(AGuardian target)
    {
        this.chosenTarget = target;
        this.targetChosen = true;
        this.choiceComplete = targetChosen && attackChosen;
    }

    public void setChosenArea(ArrayMap<Integer,AGuardian> targets)
    {
        this.areaChosen = true;
        this.targetChosen = true;
        this.chosenArea = targets;
        this.choiceComplete = targetChosen && attackChosen && areaChosen;
    }

    public void setChosenAttack(int attack)
    {
        this.chosenAttack = attack;
        attackChosen = true;
        choiceComplete = targetChosen && attackChosen;
    }

    // INNER CLASS

    /**
     * Defines AIPlayer. Use this to implement different players like:
     *
     *  Normal Enemy
     *  Boss Enemy
     *  Training Enemy
     */
    public interface AIPlayer
    {
        /**
         * Define how the player makes his turn.
         */
        void turn();
    }

    private class DefaultAIPlayer implements AIPlayer
    {

        public DefaultAIPlayer() {
            // TODO
        }

        public void turn()
        {
            System.out.println("\n### AI's turn ###");
            AGuardian m = getActiveMonster();

            if(getActiveMonster().getIndividualStatistics().getStatusEffect() == IndividualStatistics.StatusEffect.PETRIFIED) {

                doNothing();

            } else {

                int att = 0;
                Ability.aID aID = null;

                while (aID == null) {
                    att = MathUtils.random(0, m.getAbilityGraph().getActiveAbilities().size - 1);
                    aID = m.getAbilityGraph().getActiveAbility(att);
                }

                Ability ability = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(aID);

                if(ability.getAreaDamage()) {

                    setChosenArea(queue.getCombatTeamLeft());

                } else {

                    chooseTarget();

                }

                setChosenAttack(att);
                attack();
                // applyAttack() is called by the client, as soon as it is neccessary
                // e.g. when the Battle Animation finishes
            }
        }

        private void chooseTarget()
        {
            boolean foundTarget = false;
            AGuardian target;
            while(!foundTarget)
            {
                target = queue.getCombatTeamLeft().getRandomFitMember();
                if(target.getIndividualStatistics().isFit())
                {
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

    public boolean isWildEncounter()
    {
        return wildEncounter;
    }

    // INNER INTERFACE
    public static abstract class Callbacks
    {
        public void onMonsterKilled(AGuardian m){}
        public void onAttack(AGuardian attacker, AGuardian target, Ability ability, AttackCalculationReport rep){}
        public void onAreaAttack(AGuardian attacker, ArrayMap<Integer,AGuardian> targets, Ability ability, Array<AttackCalculationReport> reports) {}
        public void onDefense(AGuardian defensiveGuardian){}
        public void onPlayersTurn(){}
        public void onBattleEnds(boolean winnerSide){}
        public void onDoingNothing(AGuardian guardian){}
        public void onApplyStatusEffect(AGuardian guardian){}
        public void onGuardianSubstituted(AGuardian substituted, AGuardian substitute, int fieldPos) {}
        public void onReplacingDefeatedGuardian(AGuardian substituted, AGuardian substitute, int fieldPos) {}
        public void onBanningWilduardian(AGuardian bannedGuardian, ChakraCrystalItem crystal, int fieldPos) {}
        public void onBanningWildGuardianFailure(AGuardian bannedGuardian, ChakraCrystalItem crystal, int fieldPos) {}
        public void onBanningWildGuardianSuccess(AGuardian bannedGuardian, ChakraCrystalItem crystal, int fieldPos) {}
    }
}
