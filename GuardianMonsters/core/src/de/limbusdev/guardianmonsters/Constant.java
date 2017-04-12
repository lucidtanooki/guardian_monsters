package de.limbusdev.guardianmonsters;

import de.limbusdev.guardianmonsters.model.gamestate.ForSerializationOnly;
import de.limbusdev.guardianmonsters.utils.DebugMode;

/**
 * Static container for Settings. Every parameter used in the game should be configured here
 * @author Georg Eckert 2017
 */
public final class Constant {

    @ForSerializationOnly
    private Constant() {}

    public static final boolean LEFT = true;
    public static final boolean RIGHT = false;

    // .................................................................................. ATTRIBUTES
    public static final int TILE_SIZE = 16;
    public static final int PIXELS_PER_METER = 1;
    public static final int RES_X = 428;
    public static final int RES_Y = 240;
    public static final int WIDTH = 428;
    public static final int HEIGHT = 240;
    public static final int COL = 8;
    public static final int ROW = 8;
    public static final int ONE_STEPDURATION_MS = 5;
    public static final int ONE_STEP_DURATION_PERSON = 20;
    public static final int zoom = 1;

    // ................................................................................... DEBUGGING
    public static final boolean DEBUGGING_ON = true;
    public final static DebugMode DEBUG_MODE = DebugMode.METAMORPHOSIS;
    public final static int startMap = 25;
    public final static int startX = 1, startY = 1;

    // ............................................................................ BATTLE BALANCING
    public final static float LVL_EXPONENT = 1.5f;
    public final static float BASE_EXP = 1000f;



}
