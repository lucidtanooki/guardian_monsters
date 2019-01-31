package de.limbusdev.guardianmonsters.guardians.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.Observable;

import de.limbusdev.guardianmonsters.guardians.Constant;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * BattleQueue
 *
 * @author Georg Eckert 2017
 */

public class BattleQueue extends Observable
{

    public enum Message {
        NEXT, NEWROUND,
    }

    public class QueueSignal {
        public Message message;
        public BattleQueue queue;

        public QueueSignal(Message message) {
            this.queue = BattleQueue.this;
            this.message = message;
        }
    }

    private CombatTeam combatTeamLeft, combatTeamRight;
    private Array<AGuardian> currentRound;
    private Array<AGuardian> nextRound;

    private Team left, right;

    public BattleQueue(Team left, Team right)
    {
        this.left = left;
        this.right = right;

        currentRound = new Array<>();
        nextRound = new Array<>();

        combatTeamLeft = new CombatTeam(left);
        combatTeamRight = new CombatTeam(right);
        currentRound.addAll(combatTeamLeft.values().toArray());
        currentRound.addAll(combatTeamRight.values().toArray());
        currentRound.sort(new MonsterSpeedComparator());
    }

    /**
     * Takes the next monster in line and moves it to the next round
     * @return next in line
     */
    public AGuardian next()
    {
        if(currentRound.size == 0) {
            Array<AGuardian> tmp = currentRound;
            currentRound = nextRound;
            nextRound = tmp;
            currentRound.sort(new MonsterSpeedComparator());
            setChanged();
            notifyObservers(new QueueSignal(Message.NEWROUND));
        }

        AGuardian next = currentRound.pop();
        nextRound.insert(0, next);

        if(currentRound.size == 0) {
            Array<AGuardian> tmp = currentRound;
            currentRound = nextRound;
            nextRound = tmp;
            currentRound.sort(new MonsterSpeedComparator());
            setChanged();
            notifyObservers(new QueueSignal(Message.NEWROUND));
        } else {
            setChanged();
            notifyObservers(new QueueSignal(Message.NEXT));
        }

        System.out.println(toString());

        return next;
    }

    /**
     * Returns next in line
     * @return
     */
    public AGuardian peekNext()
    {
        return currentRound.get(currentRound.size-1);
    }

    /**
     * Returns the side of the next monster in line
     * @return
     */
    public boolean peekNextSide()
    {
        if(left.isMember(peekNext())) return Constant.HERO;
        else return Constant.OPPONENT;
    }

    public AGuardian exchangeActive(AGuardian substitute)
    {
        CombatTeam combatTeam;

        boolean side = getTeamSideFor(currentRound.peek());
        if(side == Constant.LEFT) {
            combatTeam = combatTeamLeft;
        } else {
            combatTeam = combatTeamRight;
        }

        AGuardian activeGuardian = currentRound.peek();
        int position = combatTeam.getFieldPosition(activeGuardian);
        combatTeam.exchange(position, substitute);

        int queuePos = currentRound.indexOf(activeGuardian, false);
        currentRound.removeValue(activeGuardian, false);
        currentRound.insert(queuePos, substitute);

        setChanged();
        notifyObservers();
        return activeGuardian;
    }

    public void revive(AGuardian guardian)
    {
        nextRound.insert(nextRound.size-1, guardian);
        setChanged();
        notifyObservers();
    }

    public AGuardian randomlyExchangeDefeated(AGuardian defeated)
    {
        CombatTeam combatTeam;
        Team team;

        boolean side = getTeamSideFor(defeated);
        if(side == Constant.LEFT) {
            combatTeam = combatTeamLeft;
            team = left;
        } else {
            combatTeam = combatTeamRight;
            team = right;
        }

        int position = combatTeam.getFieldPosition(defeated);
        if(currentRound.contains(defeated, false)) {
            currentRound.removeValue(defeated, false);
        } else {
            nextRound.removeValue(defeated, false);
        }

        AGuardian substitute = null;
        boolean newGuardianFound = false;
        Iterator<AGuardian> teamIterator = team.getSlots().values().iterator();
        while(!newGuardianFound && teamIterator.hasNext())
        {
            AGuardian guardian = teamIterator.next();
            if(guardian.getIndividualStatistics().isFit() && !combatTeam.isMember(guardian)) {
                substitute = guardian;
                combatTeam.exchange(position, substitute);
                substitute = guardian;
                newGuardianFound = true;
            }
        }


        if(substitute == null) {
            throw new IllegalStateException("Do not call randomlyExchangeDefeated() if there is no Guardian left in team.");
        } else {
            nextRound.add(substitute);
            setChanged();
            notifyObservers();
        }

        return substitute;
    }

    @Override
    public String toString()
    {
        String text = "";

        text += "#==================== Next Round    ====================#\n";
        for(AGuardian m : nextRound) text += m.toString() + "\n";
        text += "#==================== Current Round ====================#\n";
        for(AGuardian m : currentRound) text += m.toString() + "\n";
        text += "#=======================================================#\n";
        text += "Next: " + peekNext().toString();

        return text;
    }

    public Array<AGuardian> getCurrentRound() {
        return currentRound;
    }

    public Array<AGuardian> getNextRound() {
        return nextRound;
    }

    public CombatTeam getCombatTeamLeft() {
        return combatTeamLeft;
    }

    public CombatTeam getCombatTeamRight() {
        return combatTeamRight;
    }

    public CombatTeam getRandomCombatTeam()
    {
        if(MathUtils.randomBoolean()) {return combatTeamLeft;}
        else {return combatTeamRight;}
    }

    public Team getLeft() {
        return left;
    }

    public Team getRight()
    {
        return right;
    }

    public boolean getTeamSideFor(AGuardian guardian) {
        if(left.isMember(guardian)) return Constant.HERO;
        else return Constant.OPPONENT;
    }

    public int getFieldPositionFor(AGuardian guardian) {
        if(getTeamSideFor(guardian) == Constant.HERO) {
            return combatTeamLeft.getFieldPosition(guardian);
        } else {
            return combatTeamRight.getFieldPosition(guardian);
        }
    }

    public void resetModifiedStats(AGuardian guardian)
    {
        guardian.getIndividualStatistics().resetModifiedStats();
    }

    public void resetTeamsModifiedStats(boolean side)
    {
        Team team;

        if(side == Constant.LEFT) {
            team = left;
        } else /* side == RIGHT */ {
            team = right;
        }

        for(AGuardian combatant : team.getSlots().values())
        {
            resetModifiedStats(combatant);
        }
    }

    public void resetCombatTeamsModifiedStats(boolean side)
    {
        CombatTeam team;

        if(side == Constant.LEFT) {
            team = combatTeamLeft;
        } else /* side == RIGHT */ {
            team = combatTeamRight;
        }

        for(AGuardian combatant : team.values())
        {
            resetModifiedStats(combatant);
        }
    }
}
