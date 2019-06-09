package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.Constant
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.guardians.monsters.Team

/**
 * Correct Usage:
 *
 * 1. Create Teams using AGuardianFactory
 * 2. Initialize BattleSystem with these Teams
 * 3. BS.getActiveMonster()
 * 4. choose Target: BS.setChosenTarget(Target)
 * 5. choose Ability to use: BS.setChosenAbility(Ability)
 * 6. calculate Attack: BS.attack()
 * 7. apply calculated Attack: BS.applyAttack()
 * 8. continue: BS.continue()
 *
 * @author Georg Eckert 2019
 */

class BattleSystem
(
        left: Team,
        right: Team,
        callbacks: Callbacks = NullCallbacks(), // Replace with real Callbacks, if not debugging
        isWildEncounter: Boolean = false        // Guardians can be banned in wild encounters only
) {
    // .................................................................................. Properties
    // .............................................................. public
    val queue           : BattleQueue
    val result          : BattleResult
    val isWildEncounter : Boolean = isWildEncounter

    // .............................................................. private
    private val defaultAiPlayer: DefaultAIPlayer


    private var chosenTarget: AGuardian? = null
    private var chosenArea  : ArrayMap<Int, AGuardian>? = null

    private var chosenAttack            : Int = 0

    private var choiceComplete          : Boolean = false
    private var targetChosen            : Boolean = false
    private var attackChosen            : Boolean = false
    private var areaChosen              : Boolean = false


    private var latestAttackReport      : AttackCalculationReport? = null
    private var latestAreaAttackReports : Array<AttackCalculationReport> = Array()

    private var callbacks               : Callbacks = callbacks


    // ................................................................................ Constructors
    init
    {
        defaultAiPlayer = DefaultAIPlayer()

        queue = BattleQueue(left, right)

        result = BattleResult(left, Array())

        if (callbacks::class == NullCallbacks::class)
        { println("No Callbacks set. NullCallbacks used.") }
    }

    // .............................................................................. Battle Methods
    /** Returns the currently active Guardian. The one who's turn it is. */
    val activeMonster: AGuardian get() = queue.peekNext()

    /**
     * Attacks the given Monster with the currently active monster
     *
     * @param target
     * @param attack
     */
    private fun attack(target: AGuardian?, attack: Int)
    {
        if(target == null) { throw NullPointerException("$TAG: Attack Target is null!") }

        // Throw exception if target or attack are unset
        if (!choiceComplete)
        {
            throw IllegalStateException(TAG + " you forgot to set the " + if (targetChosen) "attack" else "target")
        }

        // Calculate Ability
        val aID = activeMonster.abilityGraph.activeAbilities.get(attack)
        val ability = GuardiansServiceLocator.abilities.getAbility(aID)


        val attacker = activeMonster
        val report = BattleCalculator.calcAttack(attacker, target, aID)
        latestAttackReport = report;
        callbacks.onAttack(attacker, target, ability, report)
    }

    private fun attackArea(targets: ArrayMap<Int, AGuardian>?, attack: Int)
    {
        if(targets == null) { throw NullPointerException("$TAG: Area Attack Target is null!") }

        if (!choiceComplete)
        {
            throw IllegalStateException("$TAG you forgot to set area, attack or targets.")
        }

        // Calculate Ability
        val aID = activeMonster.abilityGraph.activeAbilities[attack]
        val ability = GuardiansServiceLocator.abilities.getAbility(aID)

        val attacker = activeMonster
        latestAreaAttackReports = Array()
        for (g in targets.values())
        {
            val report = BattleCalculator.calcAttack(attacker, g, aID)
            latestAreaAttackReports.add(report)
        }
        callbacks.onAreaAttack(attacker, targets, ability, latestAreaAttackReports)
    }

    /**
     * Puts a Guardian back in the queue, after it was KO
     */
    fun revive(guardian: AGuardian) { queue.revive(guardian) }

    /**
     * Whether this application defeated a guardian
     * @return
     */
    fun applyAttack(): Boolean
    {
        if (areaChosen)
        {
            latestAreaAttackReports.forEach { report -> BattleCalculator.apply(report) }
        }
        else
        {
            if(latestAttackReport != null) { BattleCalculator.apply(latestAttackReport!!) }
            else                           { println("Latest Attack Report is null! This should not happen.") }
        }
        return checkKO()
    }

    fun applyStatusEffect()
    {
        if (activeMonster.individualStatistics.statusEffect != IndividualStatistics.StatusEffect.HEALTHY)
        {
            BattleCalculator.applyStatusEffect(activeMonster)
            callbacks.onApplyStatusEffect(activeMonster)
            checkKO()
        }
    }

    fun attack()
    {
        // if status effect prevents normal attack calculation
        val activeGuardian = activeMonster

        when (activeGuardian.individualStatistics.statusEffect)
        {
            IndividualStatistics.StatusEffect.PETRIFIED ->
            {
                doNothing()
                return
            }
            IndividualStatistics.StatusEffect.LUNATIC ->
            {
                // End Lunatic Status with a chance of 20%
                if (MathUtils.randomBoolean(0.2f))
                {
                    activeGuardian.individualStatistics.statusEffect =
                            IndividualStatistics.StatusEffect.HEALTHY
                }
                // Attack arbitrarily chosen target
                else
                {
                    val aID = activeGuardian.abilityGraph.randomActiveAbility
                    val ability = GuardiansServiceLocator.abilities.getAbility(aID)
                    val att = activeGuardian.abilityGraph.activeAbilities.getKey(aID, false)

                    if (ability.areaDamage) { setChosenArea(queue.randomCombatTeam)    }
                    else                    { setChosenTarget(getRandomFitCombatant()) }

                    setChosenAttack(att)
                }
            }
            else /* HEALTHY */ -> {}
        }

        // if no status effect prevents normal attack calculation
        if (areaChosen) { attackArea(chosenArea, chosenAttack) }
        else            { attack(chosenTarget, chosenAttack) }
    }

    /**
     * The monster decides to not attack and instead raise it's defense values for one round
     */
    fun defend()
    {
        latestAttackReport = BattleCalculator.calcDefense(activeMonster)
        callbacks.onDefense(activeMonster)
    }

    /**
     * Monster does nothing. Use when e.g. using an item.
     */
    fun doNothing()
    {
        callbacks.onDoingNothing(activeMonster)
    }

    private fun getRandomFitCombatant(): AGuardian
    {
        return if (MathUtils.randomBoolean()) {  queue.combatTeamLeft.getRandomFitMember()  }
               else                           {  queue.combatTeamRight.getRandomFitMember() }
    }

    fun nextMonster()
    {
        queue.next()
        chosenAttack = 0
        chosenTarget = null
        choiceComplete = false
        targetChosen = false
        attackChosen = false
        areaChosen = false
        chosenArea = null
    }

    fun continueBattle()
    {
        // Check if one team is KO
        if (queue.combatTeamLeft.isKO() || queue.combatTeamRight.isKO())
        {
            finishBattle()
        }
        else
        {
            if (queue.peekNextSide() == Constant.OPPONENT)
            {
                // It's AI's turn
                letAITakeTurn()
            }
            else
            {
                // It's player's turn
                callbacks.onPlayersTurn()
            }
        }
    }

    /**
     * Called, when one of the teams is KO
     */
    private fun finishBattle()
    {
        queue.resetTeamsModifiedStats(Constant.LEFT)
        queue.resetTeamsModifiedStats(Constant.RIGHT)
        callbacks.onBattleEnds(!queue.combatTeamLeft.isKO())
    }

    /**
     * Checks if a monster has been defeated during the last attack
     */
    private fun checkKO(): Boolean
    {
        var guardianDefeated = false
        val iterators: Array<MutableIterator<AGuardian>> = Array()
        iterators.add(queue.currentRound.iterator())
        iterators.add(queue.nextRound.iterator())

        for (it in iterators)
        {
            while (it.hasNext())
            {
                val m = it.next()
                if (m.individualStatistics.isKO)
                {
                    when(queue.getTeamSideFor(m))
                    {
                        Side.RIGHT ->
                        {
                            giveEXPtoWinners(m)
                            if (queue.right.teamKO())
                            {
                                it.remove()
                                callbacks.onMonsterKilled(m)
                            }
                            else
                            {
                                randomlyReplaceDefeatedGuardian(Constant.RIGHT, m)
                            }
                        }
                        Side.LEFT ->
                        {
                            it.remove()
                            callbacks.onMonsterKilled(m)
                        }
                    }

                    guardianDefeated = true
                }
            }
        }

        return guardianDefeated
    }


    private fun giveEXPtoWinners(defeatedGuardian: AGuardian)
    {
        for (m in queue.combatTeamLeft.values())
        {
            if (m.individualStatistics.isFit)
            {
                val exp = BattleCalculator.calculateEarnedEXP(m, defeatedGuardian)

                result.gainEXP(m, exp)
            }
        }
    }

    /**
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    private fun letAITakeTurn()
    {
        if (queue.peekNextSide() == Constant.HERO)
        {
            throw IllegalStateException("$TAG AI can't take turn. The first monster in queue is not in it's team.")
        }
        defaultAiPlayer.turn()
    }


    /**
     * Swaps two monsters
     * @param newGuardian
     */
    fun replaceActiveMonster(newGuardian: AGuardian)
    {
        val fieldPos = queue.getFieldPositionFor(activeMonster)
        activeMonster.deleteObservers()
        val replaced = queue.exchangeActive(newGuardian)
        callbacks.onGuardianSubstituted(replaced, newGuardian, fieldPos)
    }

    fun banWildGuardian(bannedGuardian: AGuardian, item: ChakraCrystalItem)
    {
        if (!isWildEncounter)
        {
            throw IllegalStateException("Guardians can be banned in wild encounters only!")
        }

        if (queue.combatTeamRight.countFitMembers() > 1)
        {
            throw IllegalStateException("Banning Guardians is possible only, when there is only 1 Guardian left.")
        }

        val fieldPos = queue.getFieldPositionFor(bannedGuardian)

        callbacks.onBanningWildGuardian(bannedGuardian, item, fieldPos)
    }

    fun banWildGuardian(item: ChakraCrystalItem)
    {
        if (!isWildEncounter)
        {
            throw IllegalStateException("Guardians can be banned in wild encounters only!")
        }

        if (queue.combatTeamRight.countFitMembers() > 1)
        {
            throw IllegalStateException("Banning Guardians is possible only, when there is only 1 Guardian left.")
        }

        val lastGuardian = queue.combatTeamRight.getRandomFitMember()
        banWildGuardian(lastGuardian, item)
    }

    /**
     * Mainly for replacing defeated opponent Guardians
     */
    private fun randomlyReplaceDefeatedGuardian(side: Boolean, defeated: AGuardian)
    {
        val fieldPos = queue.getFieldPositionFor(defeated)
        defeated.deleteObservers()
        val substitute = queue.randomlyExchangeDefeated(defeated)
        callbacks.onReplacingDefeatedGuardian(defeated, substitute, fieldPos)
    }

    fun setChosenTarget(target: AGuardian)
    {
        this.chosenTarget = target
        this.targetChosen = true
        this.choiceComplete = targetChosen && attackChosen
    }

    fun setChosenArea(targets: ArrayMap<Int, AGuardian>)
    {
        this.areaChosen = true
        this.targetChosen = true
        this.chosenArea = targets
        this.choiceComplete = targetChosen && attackChosen && areaChosen
    }

    fun setChosenAttack(attack: Int)
    {
        this.chosenAttack = attack
        attackChosen = true
        choiceComplete = targetChosen && attackChosen
    }


    // ........................................................................... Getters & Setters
    /**
     * If callbacks must be set later, usually only in debugging.
     * @param callbacks
     */
    fun setCallbacks(callbacks: Callbacks)
    {
        this.callbacks = callbacks
    }


    // ............................................................................... Inner Classes
    abstract class Callbacks
    {
        open fun onMonsterKilled(m: AGuardian) {}
        open fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, rep: AttackCalculationReport) {}
        open fun onAreaAttack(attacker: AGuardian, targets: ArrayMap<Int, AGuardian>, ability: Ability, reports: Array<AttackCalculationReport>) {}
        open fun onDefense(defensiveGuardian: AGuardian) {}
        open fun onPlayersTurn() {}
        open fun onBattleEnds(winnerSide: Boolean) {}
        open fun onDoingNothing(guardian: AGuardian) {}
        open fun onApplyStatusEffect(guardian: AGuardian) {}
        open fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int) {}
        open fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int) {}
        open fun onBanningWildGuardian(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int) {}
        open fun onBanningWildGuardianFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int) {}
        open fun onBanningWildGuardianSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int) {}
    }

    /** Null Implementation so the constructor is not forced to allow Callbacks? (null) */
    private class NullCallbacks : Callbacks() { }


    /**
     * Defines AIPlayer. Use this to implement different players like:
     *
     * Normal Enemy
     * Boss Enemy
     * Training Enemy
     */
    interface AIPlayer
    {
        /**
         * Define how the player makes his turn.
         */
        fun turn()
    }

    private inner class DefaultAIPlayer : AIPlayer
    {
        override fun turn()
        {
            println("\n### AI's turn ###")
            val m = activeMonster

            if (activeMonster.individualStatistics.statusEffect === IndividualStatistics.StatusEffect.PETRIFIED)
            {
                doNothing()
            }
            else
            {
                var att = 0
                var aID: Ability.aID? = null

                while (aID == null)
                {
                    att = MathUtils.random(0, m.abilityGraph.activeAbilities.size - 1)
                    aID = m.abilityGraph.getActiveAbility(att)
                }

                val ability = GuardiansServiceLocator.abilities.getAbility(aID)

                if (ability.areaDamage) { setChosenArea(queue.combatTeamLeft) }
                else                    { chooseTarget() }

                setChosenAttack(att)
                attack()
                // applyAttack() is called by the client, as soon as it is necessary
                // e.g. when the Battle Animation finishes
            }
        }

        private fun chooseTarget()
        {
            var foundTarget = false
            var target: AGuardian
            while (!foundTarget)
            {
                target = queue.combatTeamLeft.getRandomFitMember()
                if (target.individualStatistics.isFit)
                {
                    foundTarget = true
                    setChosenTarget(target)
                }
            }
        }
    }

    // ............................................................................ Companion Object
    companion object
    {
        private const val TAG: String = "BattleSystem"
    }
}
