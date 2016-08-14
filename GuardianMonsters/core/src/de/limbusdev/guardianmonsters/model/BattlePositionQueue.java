package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 11.01.16.
 */
public class BattlePositionQueue {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<Integer> positions;
    private int pointer;
    private int size;
    private boolean leftSide;
    /* ........................................................................... CONSTRUCTOR .. */
    public BattlePositionQueue(int size, boolean leftSide) {
        this.size = size;
        positions = new Array<Integer>();
        for(int i=0;i<size;i++) positions.add(i);
        pointer = 0;
        this.leftSide = leftSide;
    }
    /* ............................................................................... METHODS .. */

    /**
     *
     * @param pos
     * @return pointer to the active position
     */
    public int remove(int pos) {
        if(!leftSide)pos -=3;
        if(!positions.contains(new Integer(BatPos.convertFromCounterToPosition(pos)), false)) {
            try {
                return BatPos.convertFromCounterToPosition(positions.get(pointer));
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        positions.removeValue(new Integer(BatPos.convertFromCounterToPosition(pos)), false);

        int returnValue;

        if(positions.size<=0) return -1;
        pointer %= positions.size;
        if(size==2) returnValue = BatPos.convertFromCounterToPosition2(positions.get(pointer));
        else returnValue = BatPos.convertFromCounterToPosition(positions.get(pointer));

        if(!leftSide) return returnValue +3;
        else return returnValue;
    }

    public int getNext() {
        pointer++;
        pointer %= positions.size;

        int returnValue;

        if(size==2) returnValue = BatPos.convertFromCounterToPosition2(positions.get(pointer));
        else returnValue = BatPos.convertFromCounterToPosition(positions.get(pointer));

        if(!leftSide) return returnValue +3;
        else return returnValue;
    }

    public int getPrevious() {
        pointer--;
        if(pointer < 0) pointer = positions.size-1;

        int returnValue;

        if(size==2) returnValue = BatPos.convertFromCounterToPosition2(positions.get(pointer));
        else returnValue = BatPos.convertFromCounterToPosition(positions.get(pointer));

        if(!leftSide) return returnValue +3;
        else return returnValue;
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
