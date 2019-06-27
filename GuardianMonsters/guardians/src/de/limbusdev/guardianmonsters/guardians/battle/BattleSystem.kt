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
import de.limbusdev.utils.log
import de.limbusdev.utils.logDebug
import de.limbusdev.utils.logInfo
import de.limbusdev.utils.logWarning
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

        if (eventHandler is NullEventHandler) { logWarning(TAG) { "No EventHandler set. NullEventHandler used." } }
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

    fun finishBattleByBanning()
    {
        queue.resetTeamsModifiedStats(HERO)
        queue.resetTeamsModifiedStats(OPPONENT)
        eventHandler.onBattleEnds(Constant.LEFT)
    }

    /**
     * The Computer Player takes his turn and chooses his attack and the target to be attacked.
     * This is possible only, when the first monster in queue is of AI's team.
     */
    private fun onAIPlayersTurn()
    {
        logDebug(TAG) { "onAIPlayersTurn()" }

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
        val aID = activeGuardian.abilityGraph.activeAbilities[attack]
        checkNotNull(aID) { "The chosen Ability's slot returns null. This should not happen." }
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
        checkNotNull(aID) { "The chosen Ability's slot returns null. This should not happen." }
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


                            // If Opponent has additional fit team member, replace defeated
                            val additional = queue.right.values().any { it.stats.isFit && !queue.combatTeamRight.isMember(it) }
                            if (additional)
                            {
                                randomlyReplaceDefeatedGuardian(OPPONENT, guardian)
                            }
                            else
                            {
                                iterator.remove()
                                eventHandler.onGuardianDefeated(guardian)
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

        // If there is only one Guardian on the battle field, banning is allowed.
        // If banning succeeds, all other wild Guardians will run away and the battle is won.
        // Successful banning always ends the battle.

        // Will banning succeed?
        val banSucceeds = BattleCalculator.banSucceeds(bannedGuardian, item)

        val banCallback: () -> Unit
        if(banSucceeds)
        {
            // Successful banning always ends the battle
            banCallback = { eventHandler.onBanningSuccess(bannedGuardian, item, fieldPos) }
        }
        else
        {
            // Continue Battle with next Guardian
            banCallback = { eventHandler.onBanningFailure(bannedGuardian, item, fieldPos) }
        }

        eventHandler.onBanning(bannedGuardian, item, fieldPos, banCallback)
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
        open fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, report: AttackCalculationReport) {}
        open fun onAreaAttack(attacker: AGuardian, targets: ArrayMap<Int, AGuardian>, ability: Ability, reports: Array<AttackCalculationReport>) {}
        open fun onDefense(defensiveGuardian: AGuardian) {}
        open fun onPlayersTurn() {}
        open fun onBattleEnds(winnerSide: Boolean) {}
        open fun onDoingNothing(guardian: AGuardian) {}
        open fun onApplyStatusEffect(guardian: AGuardian) {}
        open fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int) {}
        open fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int) {}
        open fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int, continueBanning: () -> Unit) {}
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
            log() {"""

                        +++---------------------------------------------------------------------+++
                        |||                           AI's turn                                 |||
                        +++---------------------------------------------------------------------+++
                    """.trimIndent()}

            val guardian = activeGuardian

            val abilitySlot = guardian.abilityGraph.getRandomActiveAbilitySlot()
            val aID: Ability.aID = guardian.abilityGraph.getRandomActiveAbility()
            checkNotNull(aID) { "The chosen Ability's slot returns null. This should not happen." }
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
        companion object { const val TAG = "BattleSystem.NullEventHandler" }
        override fun onGuardianDefeated(guardian: AGuardian)
        { logInfo(TAG) { "onGuardianDefeated()" } }

        override fun onAttack(attacker: AGuardian, target: AGuardian, ability: Ability, report: AttackCalculationReport)
        { logInfo(TAG) { "onAttack()" } }

        override fun onAreaAttack(attacker: AGuardian, targets: ArrayMap<Int, AGuardian>, ability: Ability, reports: Array<AttackCalculationReport>)
        { logInfo(TAG) { "onAreaAttack()" } }

        override fun onDefense(defensiveGuardian: AGuardian)
        { logInfo(TAG) { "onDefense()" } }

        override fun onPlayersTurn()
        { logInfo(TAG) { "onBattleEnds()" } }

        override fun onBattleEnds(winnerSide: Boolean)
        { logInfo(TAG) { "onBattleEnds()" } }

        override fun onDoingNothing(guardian: AGuardian)
        { logInfo(TAG) { "onDoingNothing()" } }

        override fun onApplyStatusEffect(guardian: AGuardian)
        { logInfo(TAG) { "onApplyStatusEffect()" } }

        override fun onGuardianSubstituted(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
        {logInfo(TAG) { "onGuardianSubstituted()" } }

        override fun onReplacingDefeatedGuardian(substituted: AGuardian, substitute: AGuardian, fieldPos: Int)
        { logInfo(TAG) { "onReplacingDefeatedGuardian()" } }

        override fun onBanning(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int, continueBanning: () -> Unit)
        { logInfo(TAG) { "onBanning()" } }

        override fun onBanningFailure(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
        { logInfo(TAG) { "onBanningFailure()" } }

        override fun onBanningSuccess(bannedGuardian: AGuardian, crystal: ChakraCrystalItem, fieldPos: Int)
        { logInfo(TAG) { "onBanningSuccess()" } }
    }
}
