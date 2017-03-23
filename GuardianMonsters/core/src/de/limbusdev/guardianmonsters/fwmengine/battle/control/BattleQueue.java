package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.monsters.Team;

/**
 * BattleQueue
 *
 * @author Georg Eckert 2017
 */

public class BattleQueue {
    private Array<Monster> currentRound;
    private Array<Monster> nextRound;

    public BattleQueue(Team left, Team right) {
        currentRound = new Array<>();
        nextRound = new Array<>();

        currentRound.addAll(left.values().toArray());
        currentRound.sort(new MonsterSpeedComparator());
    }

    public Monster next() {
        Monster next = currentRound.pop();
        nextRound.insert(0,next);
        if(currentRound.size == 0) {
            Array<Monster> tmp = currentRound;
            currentRound = nextRound;
            nextRound = currentRound;
            currentRound.sort(new MonsterSpeedComparator());
        }
        return next;
    }

    public Monster peekNext() {
        return currentRound.get(currentRound.size-1);
    }

    @Override
    public String toString() {
        String text = "";

        text += "=== Next Round    ===\n";
        for(Monster m : nextRound) text += m.toString() + "\n";
        text += "=== Current Round ===\n";
        for(Monster m : currentRound) text += m.toString() + "\n";
        text += "---------------------\n";
        text += "Next: " + peekNext().toString();

        return text;
    }
}
