package de.limbusdev.guardianmonsters.guardians.battle;

import com.badlogic.gdx.utils.Array;

import java.util.Observable;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

import static de.limbusdev.guardianmonsters.guardians.Constant.HERO;
import static de.limbusdev.guardianmonsters.guardians.Constant.OPPONENT;

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
        AGuardian next = currentRound.pop();
        nextRound.insert(0,next);
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
        if(left.isMember(peekNext())) return HERO;
        else return OPPONENT;
    }

    public AGuardian exchangeNext(AGuardian substitute)
    {
        CombatTeam combatTeam;
        if(peekNextSide() == HERO) {
            combatTeam = combatTeamLeft;
        } else {
            combatTeam = combatTeamRight;
        }

        AGuardian next = next();
        int position = combatTeam.getFieldPosition(next);
        combatTeam.exchange(position, substitute);

        nextRound.removeValue(next,false);
        nextRound.insert(0,substitute);
        return next;
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

    public Team getLeft() {
        return left;
    }

    public Team getRight() {
        return right;
    }

    public boolean getTeamSideFor(AGuardian guardian) {
        if(left.isMember(guardian)) return HERO;
        else return OPPONENT;
    }

    public int getFieldPositionFor(AGuardian guardian) {
        if(getTeamSideFor(guardian) == HERO) {
            return combatTeamLeft.getFieldPosition(guardian);
        } else {
            return combatTeamRight.getFieldPosition(guardian);
        }
    }
}
