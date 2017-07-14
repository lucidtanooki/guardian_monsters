package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.CombatTeam;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

import static de.limbusdev.guardianmonsters.Constant.LEFT;
import static de.limbusdev.guardianmonsters.Constant.RIGHT;

/**
 * BattleQueue
 *
 * @author Georg Eckert 2017
 */

public class BattleQueue extends Signal<BattleQueue.QueueSignal> {

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
    private Array<Guardian> currentRound;
    private Array<Guardian> nextRound;

    private Team left, right;

    public BattleQueue(Team left, Team right) {
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
    public Guardian next() {
        Guardian next = currentRound.pop();
        nextRound.insert(0,next);
        if(currentRound.size == 0) {
            Array<Guardian> tmp = currentRound;
            currentRound = nextRound;
            nextRound = currentRound;
            currentRound.sort(new MonsterSpeedComparator());
            dispatch(new QueueSignal(Message.NEWROUND));
        } else {
            dispatch(new QueueSignal(Message.NEXT));
        }

        System.out.println(toString());

        return next;
    }

    /**
     * Returns next in line
     * @return
     */
    public Guardian peekNext() {
        return currentRound.get(currentRound.size-1);
    }

    /**
     * Returns the side of the next monster in line
     * @return
     */
    public boolean peekNextSide() {
        if(left.isMember(peekNext())) return LEFT;
        else return RIGHT;
    }

    public Guardian exchangeNext(Guardian substitute) {
        CombatTeam combatTeam;
        if(peekNextSide() == LEFT) {
            combatTeam = combatTeamLeft;
        } else {
            combatTeam = combatTeamRight;
        }

        Guardian next = next();
        int position = combatTeam.getFieldPosition(next);
        combatTeam.exchange(position, substitute);

        nextRound.removeValue(next,false);
        nextRound.insert(0,substitute);
        return next;
    }

    @Override
    public String toString() {
        String text = "";

        text += "=== Next Round    ===\n";
        for(Guardian m : nextRound) text += m.toString() + "\n";
        text += "=== Current Round ===\n";
        for(Guardian m : currentRound) text += m.toString() + "\n";
        text += "---------------------\n";
        text += "Next: " + peekNext().toString();

        return text;
    }

    public Array<Guardian> getCurrentRound() {
        return currentRound;
    }

    public Array<Guardian> getNextRound() {
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

    public boolean getTeamSideFor(Guardian guardian) {
        if(left.isMember(guardian)) return LEFT;
        else return RIGHT;
    }

    public int getFieldPositionFor(Guardian guardian) {
        if(getTeamSideFor(guardian) == LEFT) {
            return combatTeamLeft.getFieldPosition(guardian);
        } else {
            return combatTeamRight.getFieldPosition(guardian);
        }
    }
}
