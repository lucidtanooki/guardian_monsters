package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 11.01.16.
 */
public class BattlePositionQueue {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<Integer> positions;
    private int pointer;
    private int size;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattlePositionQueue(int size) {
        this.size = size;
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
        if(!positions.contains(new Integer(BatPos.convertFromCounterToPosition(pos)), false)) {
            try {
                return BatPos.convertFromCounterToPosition(positions.get(pointer));
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        positions.removeValue(new Integer(BatPos.convertFromCounterToPosition(pos)), false);
        if(positions.size<=0) return -1;
        pointer %= positions.size;
        if(size==2) return BatPos.convertFromCounterToPosition2(positions.get(pointer));
        else return BatPos.convertFromCounterToPosition(positions.get(pointer));
    }

    public int getNext() {
        pointer++;
        pointer %= positions.size;

        if(size==2) return BatPos.convertFromCounterToPosition2(positions.get(pointer));
        else return BatPos.convertFromCounterToPosition(positions.get(pointer));
    }

    public int getPrevious() {
        pointer--;
        if(pointer < 0) pointer = positions.size-1;
        if(size==2) return BatPos.convertFromCounterToPosition2(positions.get(pointer));
        else return BatPos.convertFromCounterToPosition(positions.get(pointer));
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
        private static int[] positions2 = {0,1};
        private static int convertFromCounterToPosition(int counter) {
            return positions[counter];
        }
        private static int convertFromCounterToPosition2(int counter) {
            return positions2[counter];
        }
    }
}
