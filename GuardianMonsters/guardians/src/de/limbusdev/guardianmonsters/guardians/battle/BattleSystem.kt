package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.Constant
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.Side.LEFT as HERO
import de.limbusdev.guardianmonsters.guardians.Side.RIGHT as OPPONENT
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import ktx.log.info

/**
 * Correct Usage:
 *
 * 1. Create Teams using AGuardianFactory
 * 2. Initialize BattleSystem with these Teams
 * 3.                           BS.getActiveMonster()
 * 4. choose Target:            BS.setChosenTarget(Target)
 * 5. choose Ability to use:    BS.setChosenAbility(Ability)
 * 6. calculate Attack:         BS.calculateAttack()
 * 7. apply calculated Attack:  BS.applyAttack()
 * 8. continue:                 BS.continue()
 *
 * @author Georg Eckert 2019
 */

class BattleSystem
(
        left            : Team,
        right           : Team,
        eventHandler    : EventHandler = NullEventHandler(), // Replace with real Callbacks, if not debugging
        isWildEncounter : Boolean = false                    // Guardians can be banned in wild encounters only
) {
    // .................................................................................. Properties
    // .............................................................. public
    val queue           : BattleQueue
    val result          : BattleResult
    val isWildEncounter : Boolean = isWildEncounter

    /** Returns the currently active Guardian. The one who's turn it is. */
    val activeGuardian: AGuardian get() = queue.peekNext()


    // .............................................................. private
    private val defaultAiPlayer: DefaultAIPlayer


    private var chosenTarget            : AGuardian? = null
    private var chosenArea              : ArrayMap<Int, AGuardian>? = null

    private var chosenAttack            : Int = 0

    private var choiceComplete          : Boolean = false
    private var targetChosen            : Boolean = false
    private var attackChosen            : Boolean = false
    private var areaChosen              : Boolean = false


    private var latestAttackReport      : AttackCalculationReport? = null
    private var latestAreaAttackReports : Array<AttackCalculationReport> = Array()

    private var eventHandler            : EventHandler = eventHandler


    // ................................................................................ Constructors
    init
    {
        defaultAiPlayer = DefaultAIPlayer()

        queue = BattleQueue(left, right)

        result = BattleResult(left, Array())

        if (eventHandler is NullEventHandler) { println("$TAG: No EventHandler set. NullEventHandler used.") }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////// BATTLE METHODS
    // ............................................................................ Main Battle Loop
    /** Main Battle Loop Method
     *
     *  if one team is KO                 -> finishes battle
     *  if next Guardian is from Hero     -> calls onPlayersTurn()
     *  if next Guardian is from Opponent -> calls onAIPlayersTurn()
     */
    fun continueBattle()
    {
        // If one team is KO, end battle
        if (queue.combatTeamLeft.isKO() || queue.combatTeamRight.isKO())
        {
            finishBattle()
        }
        // Else decide what to do next
        else
        {
            when(queue.peekNextSide())
            {
                OPPONENT -> onAIPlayersTurn()            // It's AI's turn
                HERO     -> eventHandler.onPlayersTurn() // It's player's turn
            }
        }
    }

    /** Called, when one of the teams is KO */
    private fun finishBattle()
    {
        queue.resetTeamsModifiedStats(HERO)
        queue.resetTeamsModifiedStats(OPPONENT)
        eventHandler.onBattleEnds(!queue.combatTeamLeft.isKO())
    }

    /**
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    private fun onAIPlayersTurn()
    {
        info(TAG) { "onAIPlayersTurn()" }

        check(queue.peekNextSide() != Constant.HERO)
        { "$TAG AI can't take turn. The first monster in queue is not in it's team." }

        defaultAiPlayer.turn()
    }


    // ................................................................................ Attack Setup
    fun setChosenTarget(target: AGuardian)
    {
        this.chosenTarget = target
        this.targetChosen = true
        this.choiceComplete = targetChosen && attackChosen
    }

    fun setChosenArea(targets: ArrayMap<Int, AGuardian>)
    {
        areaChosen = true
        targetChosen = true
        chosenArea = targets
        choiceComplete = targetChosen && attackChosen && areaChosen
    }

    fun setChosenAttack(attack: Int)
    {
        chosenAttack = attack
        attackChosen = true
        choiceComplete = targetChosen && attackChosen
    }


    // .......................................................................... Attack Application
    /** Starts the attacking sequence. */
    fun calculateAttack()
    {
        // if status effect prevents normal attack calculation
        when(activeGuardian.stats.statusEffect)
        {
            StatusEffect.PETRIFIED ->
            {
                // Petrification prevents any kind of action
                doNothing()
                return
            }
            StatusEffect.LUNATIC ->
            {
                // Lunatics attack a randomly chosen target
                // End Lunatic Status with a chance of 20%
                if (MathUtils.randomBoolean(0.2f))
                {
                    activeGuardian.stats.statusEffect = StatusEffect.HEALTHY
                }
                // Attack arbitrarily chosen target
                else
                {
                    val aID = activeGuardian.abilityGraph.getRandomActiveAbility()
                    val ability = GuardiansServiceLocator.abilities.getAbility(aID)
                    val att = activeGuardian.abilityGraph.activeAbilities.getKey(aID, false)

                    // Set chosen target(s) and chosen attack

                    when(ability.areaDamage)
                    {
                        true  -> setChosenArea(queue.randomCombatTeam)
                        false -> setChosenTarget(getRandomFitCombatant())
                    }

                    setChosenAttack(att)
                }
            }
            else -> {}
        }

        // if no status effect prevents normal attack calculation
        when(areaChosen)
        {
            true  -> calculateAreaAttack(chosenArea, chosenAttack)
            false -> calculateSingleTargetAttack(chosenTarget, chosenAttack)
        }
    }

    /** Attacks the given Guardian with the currently active Guardian */
    private fun calculateSingleTargetAttack(target: AGuardian?, attack: Int)
    {
        // Throw exception if target or attack are unset
        checkNotNull(target) { "$TAG: Attack Target is null!" }
        check(choiceComplete) { "$TAG you forgot to set the ${if (targetChosen) "attack" else "target"}" }

        // Calculate Ability
        val aID = activeGuardian.abilityGraph.activeAbilities.get(attack)
        val ability = GuardiansServiceLocator.abilities.getAbility(aID)

        latestAttackReport = BattleCalculator.calcAttack(activeGuardian, target, aID)

        eventHandler.onAttack(activeGuardian, target, ability, latestAttackReport!!)
    }

    /** Attacks all given Guardians with the currently active Guardian. */
    private fun calculateAreaAttack(targets: ArrayMap<Int, AGuardian>?, attack: Int)
    {
        checkNotNull(targets) { "$TAG: Area Attack Target is null!" }
        check(choiceComplete) { "$TAG you forgot to set area, attack or targets." }

        // Calculate Ability
        val aID = activeGuardian.abilityGraph.activeAbilities[attack]
        val ability = GuardiansServiceLocator.abilities.getAbility(aID)

        latestAreaAttackReports.clear()
        for (guardian in targets.values())
        {
            val report = BattleCalculator.calcAttack(activeGuardian, guardian, aID)
            latestAreaAttackReports.add(report)
        }

        eventHandler.onAreaAttack(activeGuardian, targets, ability, latestAreaAttackReports)
    }

    /** The monster decides to not attack and instead raise it's defense values for one round */
    fun defend()
    {
        latestAttackReport = BattleCalculator.calcDefense(activeGuardian)
        eventHandler.onDefense(activeGuardian)
    }

    /** Monster does nothing. Use when e.g. using an item. */
    fun doNothing()
    {
        eventHandler.onDoingNothing(activeGuardian)
    }

    /** Puts a Guardian back in the queue, after it was KO */
    fun revive(guardian: AGuardian) { queue.revive(guardian) }

    /** Applies the latest attack and returns, if any Guardian was defeated due to it. */
    fun applyAttack(): Boolean
    {
        if (areaChosen)
        {
            latestAreaAttackReports.forEach { report -> BattleCalculator.apply(report) }
        }
        else
        {
            checkNotNull(latestAttackReport) { "Latest Attack Report is null! This should not happen." }
            BattleCalculator.apply(latestAttackReport!!)
        }

        return checkKO()
    }

    /** Applies the latest changes in status effects */
    fun applyStatusEffect()
    {
        if (activeGuardian.stats.statusEffect != StatusEffect.HEALTHY)
        {
            BattleCalculator.applyStatusEffect(activeGuardian)
            eventHandler.onApplyStatusEffect(activeGuardian)
            checkKO()
        }
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


    //////////////////////////////////////////////////////////////////////////////////////////////// GETTERS
    private fun getRandomFitCombatant(): AGuardian
    {
        return if (MathUtils.randomBoolean()) {  queue.combatTeamLeft.getRandomFitMember()  }
               else                           {  queue.combatTeamRight.getRandomFitMember() }
    }

    fun nextGuardian()
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



    /** Checks if a monster has been defeated during the last attack */
    private fun checkKO(): Boolean
    {
        var guardianDefeated = false
        val queueIterators: Array<MutableIterator<AGuardian>> = Array()
        queueIterators.add(queue.currentRound.iterator())
        queueIterators.add(queue.nextRound.iterator())

        // Iterate over both rounds: Current and Next
        for (iterator in queueIterators)
        {
            while (iterator.hasNext())
            {
                val guardian = iterator.next()
                if (guardian.stats.isKO)
                {
                    when(queue.getTeamSideFor(guardian))
                    {
                        HERO ->     // If defeated Guardian is from Hero's Team
                        {
                            iterator.remove()
                            eventHandler.onGuardianDefeated(guardian)
                        }
                        OPPONENT -> // If defeated Guardian is from Opponent's Team
                        {
                            giveEXPtoWinners(guardian)
                            if (queue.right.allKO)
                            {
                                // If opponent's whole team is defeated, call event handler
                                iterator.remove()
                                eventHandler.onGuardianDefeated(guardian)
                            }
                            else
                            {
                                // If Opponent has fit team member, replace defeated
                                randomlyReplaceDefeatedGuardian(OPPONENT, guardian)
                            }
                        }
                    }

                    guardianDefeated = true
                }
            }
        }

        return guardianDefeated
    }


    //////////////////////////////////////////////////////////////////////////////////////////////// TEAM ORGANIZATION
    /**
     * Swaps two monsters
     * @param newGuardian
     */
    fun replaceActiveMonsterWith(newGuardian: AGuardian)
    {
        val fieldPos = queue.getFieldPositionFor(activeGuardian)
        activeGuardian.deleteObservers()
        val replaced = queue.exchangeActive(newGuardian)
        eventHandler.onGuardianSubstituted(replaced, newGuardian, fieldPos)
    }

    fun banWildGuardian(bannedGuardian: AGuardian, item: ChakraCrystalItem)
    {
        check(isWildEncounter) { "Guardians can be banned in wild encounters only!" }

        check(queue.combatTeamRight.countFitMembers() == 1)
        { "Banning Guardians is possible only, when there is only 1 Guardian on the battle field." }

        val fieldPos = queue.getFieldPositionFor(bannedGuardian)

        eventHandler.onBanning(bannedGuardian, item, fieldPos)
    }

    fun banWildGuardian(item: ChakraCrystalItem)
    {
        check(isWildEncounter) { "Guardians can be banned in wild encounters only!" }

        check(queue.combatTeamRight.countFitMembers() == 1)
        { "Banning Guardians is possible only, when there is only 1 Guardian on the battle field." }

        val lastGuardian = queue.combatTeamRight.getRandomFitMember()
        banWildGuardian(lastGuardian, item)
    }

    /** Mainly for replacing defeated opponent Guardians */
    private fun randomlyReplaceDefeatedGuardian(side: Side, defeated: AGuardian)
    {
        val fieldPos = queue.getFieldPositionFor(defeated)
        defeated.deleteObservers()
        val substitute = queue.randomlyExchangeDefeated(defeated)
        eventHandler.onReplacingDefeatedGuardian(defeated, substitute, fieldPos)
    }




    //////////////////////////////////////////////////////////////////////////////////////////////// SETTERS
    /** If callbacks must be set later, usually only in debugging. */
    fun setCallbacks(eventHandler: EventHandler) { this.eventHandler = eventHandler }


    // ............................................................................... Inner Classes
    abstract class EventHandler
    {
        open fun onGuardianDefeated(guardian: AGuardian) {}
        open fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, rep: AttackCalculationReport) {}
        open fun onAreaAttack(attacker: AGuardian, targets: ArrayMap<Int, AGuardian>, ability: Ability, reports: Array<AttackCalculationReport>) {}
        open fun onDefense(defensiveGuardian: AGuardian) {}
        open fun onPlayersTurn() {}
        open fun onBattleEnds(winnerSide: Boolean) {}
        open fun onDoingNothing(guardian: AGuardian) {}
        open fun onApplyStatusEffect(guardian: AGuardian) {}
        open fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int) {}
        open fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int) {}
        open fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int) {}
        open fun onBanningFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int) {}
        open fun onBanningSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int) {}
    }

    /**
     * Defines AIPlayer. Use this to implement different players like:
     *
     * Normal Enemy
     * Boss Enemy
     * Training Enemy
     */
    interface AIPlayer
    {
        /** Define how the player makes his turn. */
        fun turn()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Artificial Intelligence
    private inner class DefaultAIPlayer : AIPlayer
    {
        override fun turn()
        {
            println(); println("""
                        +++---------------------------------------------------------------------+++
                        |||                           AI's turn                                 |||
                        +++---------------------------------------------------------------------+++
                    """.trimIndent())

            val guardian = activeGuardian

            val abilitySlot = guardian.abilityGraph.getRandomActiveAbilitySlot()
            val aID: Ability.aID = guardian.abilityGraph.getActiveAbility(abilitySlot)
            val chosenAbility = GuardiansServiceLocator.abilities.getAbility(aID)

            when(chosenAbility.areaDamage)
            {
                true  -> setChosenArea(queue.combatTeamLeft)
                false -> chooseSingleTarget()
            }

            setChosenAttack(abilitySlot)
            calculateAttack()
            // applyAttack() is called by the client, as soon as it is necessary
            // e.g. when the Battle Animation finishes
        }

        private fun chooseSingleTarget()
        {
            var foundTarget = false
            var target: AGuardian
            while (!foundTarget)
            {
                target = queue.combatTeamLeft.getRandomFitMember()
                if (target.stats.isFit)
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








    /** Null Implementation */
    private class NullEventHandler : EventHandler()
    {
        override fun onGuardianDefeated(guardian: AGuardian)
        { info("BattleSystem.NullEventHandler") { "onGuardianDefeated()" } }

        override fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, rep: AttackCalculationReport)
        { info("BattleSystem.NullEventHandler") { "onAttack()" } }

        override fun onAreaAttack(attacker: AGuardian, targets: ArrayMap<Int, AGuardian>, ability: Ability, reports: Array<AttackCalculationReport>)
        { info("BattleSystem.NullEventHandler") { "onAreaAttack()" } }

        override fun onDefense(defensiveGuardian: AGuardian)
        { info("BattleSystem.NullEventHandler") { "onDefense()" } }

        override fun onPlayersTurn()
        { info("BattleSystem.NullEventHandler") { "onBattleEnds()" } }

        override fun onBattleEnds(winnerSide: Boolean)
        { info("BattleSystem.NullEventHandler") { "onBattleEnds()" } }

        override fun onDoingNothing(guardian: AGuardian)
        { info("BattleSystem.NullEventHandler") { "onDoingNothing()" } }

        override fun onApplyStatusEffect(guardian: AGuardian)
        { info("BattleSystem.NullEventHandler") { "onApplyStatusEffect()" } }

        override fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
        { info("BattleSystem.NullEventHandler") { "onGuardianSubstituted()" } }

        override fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
        { info("BattleSystem.NullEventHandler") { "onReplacingDefeatedGuardian()" } }

        override fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
        { info("BattleSystem.NullEventHandler") { "onBanning()" } }

        override fun onBanningFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
        { info("BattleSystem.NullEventHandler") { "onBanningFailure()" } }

        override fun onBanningSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
        { info("BattleSystem.NullEventHandler") { "onBanningSuccess()" } }
    }
}
