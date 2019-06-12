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
        eventHandler: EventHandler = NullEventHandler(), // Replace with real Callbacks, if not debugging
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

    private var eventHandler            : EventHandler = eventHandler


    // ................................................................................ Constructors
    init
    {
        defaultAiPlayer = DefaultAIPlayer()

        queue = BattleQueue(left, right)

        result = BattleResult(left, Array())

        if (eventHandler::class == NullEventHandler::class)
        { println("No EventHandler set. NullEventHandler used.") }
    }


    // .............................................................................. Battle Methods
    /** Returns the currently active Guardian. The one who's turn it is. */
    val activeGuardian: AGuardian get() = queue.peekNext()

    /** Attacks the given Guardian with the currently active Guardian */
    private fun attack(target: AGuardian?, attack: Int)
    {
        checkNotNull(target) { "$TAG: Attack Target is null!" }

        // Throw exception if target or attack are unset
        check(choiceComplete) { "$TAG you forgot to set the ${if (targetChosen) "attack" else "target"}" }

        // Calculate Ability
        val aID = activeGuardian.abilityGraph.activeAbilities.get(attack)
        val ability = GuardiansServiceLocator.abilities.getAbility(aID)

        latestAttackReport = BattleCalculator.calcAttack(activeGuardian, target, aID)
        eventHandler.onAttack(activeGuardian, target, ability, latestAttackReport!!)
    }

    private fun attackArea(targets: ArrayMap<Int, AGuardian>?, attack: Int)
    {
        checkNotNull(targets) { "$TAG: Area Attack Target is null!" }

        check(choiceComplete) { "$TAG you forgot to set area, attack or targets." }

        // Calculate Ability
        val aID = activeGuardian.abilityGraph.activeAbilities[attack]
        val ability = GuardiansServiceLocator.abilities.getAbility(aID)

        latestAreaAttackReports.clear()
        for (g in targets.values())
        {
            val report = BattleCalculator.calcAttack(activeGuardian, g, aID)
            latestAreaAttackReports.add(report)
        }
        eventHandler.onAreaAttack(activeGuardian, targets, ability, latestAreaAttackReports)
    }

    /** Puts a Guardian back in the queue, after it was KO */
    fun revive(guardian: AGuardian) { queue.revive(guardian) }

    /** Whether this application defeated a guardian */
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

    fun applyStatusEffect()
    {
        if (activeGuardian.stats.statusEffect != StatusEffect.HEALTHY)
        {
            BattleCalculator.applyStatusEffect(activeGuardian)
            eventHandler.onApplyStatusEffect(activeGuardian)
            checkKO()
        }
    }

    fun attack()
    {
        // if status effect prevents normal attack calculation
        when(activeGuardian.stats.statusEffect)
        {
            StatusEffect.PETRIFIED ->
            {
                doNothing()
                return
            }
            StatusEffect.LUNATIC ->
            {
                // End Lunatic Status with a chance of 20%
                if (MathUtils.randomBoolean(0.2f))
                {
                    activeGuardian.stats.statusEffect = StatusEffect.HEALTHY
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
            else -> {}
        }

        // if no status effect prevents normal attack calculation
        if (areaChosen) { attackArea(chosenArea, chosenAttack) }
        else            { attack(chosenTarget, chosenAttack) }
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
                OPPONENT -> letAITakeTurn()              // It's AI's turn
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

    /** Checks if a monster has been defeated during the last attack */
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
                val guardian = it.next()
                if (guardian.stats.isKO)
                {
                    when(queue.getTeamSideFor(guardian))
                    {
                        HERO ->     // If defeated Guardian is from Hero's Team
                        {
                            it.remove()
                            eventHandler.onGuardianDefeated(guardian)
                        }
                        OPPONENT -> // If defeated Guardian is from Opponent's Team
                        {
                            giveEXPtoWinners(guardian)
                            if (queue.right.teamKO())
                            {
                                // If opponent's whole team is defeated, call event handler
                                it.remove()
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
        check(queue.peekNextSide() != Constant.HERO)
        { "$TAG AI can't take turn. The first monster in queue is not in it's team." }

        defaultAiPlayer.turn()
    }


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

        check(queue.combatTeamRight.countFitMembers() > 1)
        { "Banning Guardians is possible only, when there is only 1 Guardian left." }

        val fieldPos = queue.getFieldPositionFor(bannedGuardian)

        eventHandler.onBanning(bannedGuardian, item, fieldPos)
    }

    fun banWildGuardian(item: ChakraCrystalItem)
    {
        check(isWildEncounter) { "Guardians can be banned in wild encounters only!" }

        check(queue.combatTeamRight.countFitMembers() > 1)
        { "Banning Guardians is possible only, when there is only 1 Guardian left." }

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
     * @param eventHandler
     */
    fun setCallbacks(eventHandler: EventHandler)
    {
        this.eventHandler = eventHandler
    }


    // ............................................................................... Inner Classes
    abstract class EventHandler
    {
        open fun onGuardianDefeated(m: AGuardian) {}
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

    /** Null Implementation */
    private class NullEventHandler : EventHandler() { }


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

    private inner class DefaultAIPlayer : AIPlayer
    {
        override fun turn()
        {
            println("\n### AI's turn ###")
            val m = activeGuardian

            if (activeGuardian.stats.statusEffect == StatusEffect.PETRIFIED)
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
