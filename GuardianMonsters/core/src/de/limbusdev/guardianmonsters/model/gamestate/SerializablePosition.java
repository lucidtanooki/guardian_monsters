package de.limbusdev.guardianmonsters.model.gamestate;

/**
 * SerializablePosition
 *
 * @author Georg Eckert 2017
 */

public class SerializablePosition {
    public int x, y, map;

    public SerializablePosition() {}

    public SerializablePosition(int x, int y, int map) {
        this.x = x;
        this.y = y;
        this.map = map;
    }
}
