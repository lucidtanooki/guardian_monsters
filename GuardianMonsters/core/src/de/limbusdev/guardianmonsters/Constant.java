package de.limbusdev.guardianmonsters;

import de.limbusdev.guardianmonsters.utils.DebugMode;

/**
 * Static container for Settings. Every parameter used in the game should be configured here
 * @author Georg Eckert 2017
 */
public interface Constant
{
    boolean LEFT = true;
    boolean RIGHT = false;

    int TILE_SIZE = 16;
    float TILE_SIZEf = 16f;
    int PIXELS_PER_METER = 1;
    int RES_X = 428;
    int RES_Y = 240;
    int WIDTH = 428;
    int HEIGHT = 240;
    float WIDTHf = 428f;
    float HEIGHTf = 240f;
    int COL = 8;
    int ROW = 8;
    float COLf = 8f;
    float ROWf = 8f;
    int WALKING_SPEED_PLAYER = 9;
    int WALKING_SPEED_AI = 8;
    int zoom = 1;

    // ............................................................................................. DEBUGGING
    boolean DEBUGGING_ON = true;
    DebugMode DEBUG_MODE = DebugMode.WORLD;
    int startMap = 25;
    int startX = 1, startY = 1;
}
