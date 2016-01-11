package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by georg on 11.01.16.
 */
public class BattlePositionQueue {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<Integer> positions;
    private int pointer;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattlePositionQueue(int size) {
        positions = new Array<Integer>();
        for(int i=0;i<size;i++) positions.add(i);
        pointer = 0;
    }
    /* ............................................................................... METHODS .. */

    /**
     *
     * @param pos
     * @return pointer to the active position
     */
    public int remove(int pos) {
        if(!positions.contains(new Integer(BatPos.convertFromCounterToPosition(pos)), false))
            return BatPos.convertFromCounterToPosition(positions.get(pointer));
        positions.removeValue(new Integer(BatPos.convertFromCounterToPosition(pos)), false);
        pointer %= positions.size;
        return BatPos.convertFromCounterToPosition(positions.get(pointer));
    }

    public int getNext() {
        pointer++;
        pointer %= positions.size;
        return BatPos.convertFromCounterToPosition(positions.get(pointer));
    }

    public int getPrevious() {
        pointer--;
        if(pointer < 0) pointer = positions.size-1;
        return BatPos.convertFromCounterToPosition(positions.get(pointer));
    }
    /* ..................................................................... GETTERS & SETTERS .. */

    final static class BatPos {
        private static int HERO_MID = 0;    // Middle Position on the left
        private static int HERO_TOP = 2;
        private static int HERO_BOT = 1;
        private static int OPPO_MID = 3;    // Middle Position on the right
        private static int OPPO_TOP = 5;
        private static int OPPO_BOT = 4;
        private static int MID = 0;
        private static int TOP = 2;
        private static int BOT = 1;
        private static int[] positions = {0,2,1};
        private static int convertFromCounterToPosition(int counter) {
            return positions[counter];
        }
    }
}
