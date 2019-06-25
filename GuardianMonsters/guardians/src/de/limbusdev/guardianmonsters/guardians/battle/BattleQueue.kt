package de.limbusdev.guardianmonsters.guardians.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import java.util.Observable

import de.limbusdev.guardianmonsters.guardians.Constant
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.utils.log

/**
 * BattleQueue
 *
 * @author Georg Eckert 2017
 */

class BattleQueue
(
        val left: Team,
        val right: Team
)
    : Observable()
{
    // ............................................................................... Inner Classes
    enum class Message { NEXT, NEW_ROUND }

    inner class QueueSignal(var message: Message)
    {
        var queue: BattleQueue = this@BattleQueue
    }


    // .................................................................................. Properties
    val combatTeamLeft  : CombatTeam
    val combatTeamRight : CombatTeam
    var currentRound    : Array<AGuardian> = Array(); private set
    var nextRound       : Array<AGuardian> = Array(); private set

    val randomCombatTeam: CombatTeam get() =
        if (MathUtils.randomBoolean()) { combatTeamLeft  }
        else                           { combatTeamRight }


    // ................................................................................ Constructors
    init
    {
        currentRound = Array()
        nextRound    = Array()

        combatTeamLeft  = CombatTeam(left)
        combatTeamRight = CombatTeam(right)
        currentRound.addAll(combatTeamLeft.values().toArray())
        currentRound.addAll(combatTeamRight.values().toArray())
        currentRound.sort(MonsterSpeedComparator())
    }


    // .............................................................................. Battle Methods
    /**
     * Takes the next monster in line and moves it to the next round
     * @return next in line
     */
    operator fun next(): AGuardian
    {
        // If current round is empty, swap with next round
        if (currentRound.size == 0)
        {
            val tmp = currentRound                          // temporarily store round in variable
            currentRound = nextRound                        // swap current for next round
            nextRound = tmp                                 // set previously current round as next
            currentRound.sort(MonsterSpeedComparator())     // sort round by Guardian speed
            setChanged()
            notifyObservers(QueueSignal(Message.NEW_ROUND))  // notify observers
        }

        val next = currentRound.pop()                       // get next Guardian in queue
        nextRound.insert(0, next)                           // put it in the next round

        // TODO does this make sense? To do it twice?
        if (currentRound.size == 0)
        {
            val tmp = currentRound
            currentRound = nextRound
            nextRound = tmp
            currentRound.sort(MonsterSpeedComparator())
            setChanged()
            notifyObservers(QueueSignal(Message.NEW_ROUND))
        }
        else
        {
            setChanged()
            notifyObservers(QueueSignal(Message.NEXT))
        }

        log() { toString() }

        return next
    }

    /**
     * Returns next in line
     * @return
     */
    fun peekNext(): AGuardian = currentRound.get(currentRound.size - 1)

    /**
     * Returns the side of the next monster in line
     * @return
     */
    fun peekNextSide(): Side
    {
        return if (left.isMember(peekNext())) { Constant.HERO }
        else                                  { Constant.OPPONENT }
    }

    fun exchangeActive(substitute: AGuardian): AGuardian
    {
        val combatTeam: CombatTeam

        val side = getTeamSideFor(currentRound.peek())
        combatTeam = when(side)
        {
            Side.LEFT  -> combatTeamLeft
            Side.RIGHT -> combatTeamRight
        }

        val activeGuardian = currentRound.peek()
        val position = combatTeam.getFieldPosition(activeGuardian)
        combatTeam.exchange(position, substitute)

        val queuePos = currentRound.indexOf(activeGuardian, false)
        currentRound.removeValue(activeGuardian, false)
        currentRound.insert(queuePos, substitute)

        setChanged()
        notifyObservers()
        return activeGuardian
    }

    fun revive(guardian: AGuardian)
    {
        nextRound.insert(nextRound.size - 1, guardian)
        setChanged()
        notifyObservers()
    }

    fun randomlyExchangeDefeated(defeated: AGuardian): AGuardian
    {
        val combatTeam: CombatTeam
        val team: Team

        when(getTeamSideFor(defeated))
        {
            Side.LEFT ->
            {
                combatTeam = combatTeamLeft
                team = left
            }
            Side.RIGHT ->
            {
                combatTeam = combatTeamRight
                team = right
            }
        }

        val position = combatTeam.getFieldPosition(defeated)
        if (currentRound.contains(defeated, false))  { currentRound.removeValue(defeated, false) }
        else                                         { nextRound.removeValue(defeated, false) }

        var substitute: AGuardian? = null
        var newGuardianFound = false
        val teamIterator = team.values().iterator()
        while (!newGuardianFound && teamIterator.hasNext())
        {
            val guardian = teamIterator.next()
            if (guardian.stats.isFit && !combatTeam.isMember(guardian))
            {
                substitute = guardian
                combatTeam.exchange(position, substitute)
                substitute = guardian
                newGuardianFound = true
            }
        }

        checkNotNull(substitute) { "Do not call randomlyExchangeDefeated() if there is no Guardian left in team." }

        nextRound.add(substitute)
        setChanged()
        notifyObservers()

        return substitute
    }

    override fun toString(): String
    {
        var text = ""

        text += "#==================== Next Round    ====================#\n"
        nextRound.forEach{ guardian -> text += "$guardian \n" }
        text += "#==================== Current Round ====================#\n"
        currentRound.forEach{ guardian -> text += "$guardian \n" }
        text += "#=======================================================#\n"
        text += "Next: ${peekNext()}"

        return text
    }

    fun getTeamSideFor(guardian: AGuardian): Side
    {
        return if (left.isMember(guardian)) { Constant.HERO }
        else                                { Constant.OPPONENT }
    }

    fun getFieldPositionFor(guardian: AGuardian): Int
    {
        return if (getTeamSideFor(guardian) == Constant.HERO)
        {
            combatTeamLeft.getFieldPosition(guardian)
        }
        else
        {
            combatTeamRight.getFieldPosition(guardian)
        }
    }

    fun resetModifiedStats(guardian: AGuardian)
    {
        guardian.stats.resetModifiedStats()
    }

    fun resetTeamsModifiedStats(side: Side)
    {
        val team: Team =
            if (side == Side.LEFT)     { left  }
            else /* side == RIGHT */   { right }

        team.values().forEach{ combatant -> resetModifiedStats(combatant)}
    }

    fun resetCombatTeamsModifiedStats(side: Boolean)
    {
        val team: CombatTeam = when(side)
        {
            Constant.LEFT    -> combatTeamLeft
            else /* RIGHT */ -> combatTeamRight
        }

        team.values().forEach{ combatant -> resetModifiedStats(combatant)}
    }
}
