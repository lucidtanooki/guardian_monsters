package de.limbusdev.guardianmonsters.utils;

import de.limbusdev.guardianmonsters.enums.DebugMode;

/**
 * Static container for Settings. Every parameter used in the game should be configured here
 * Created by Georg Eckert on 21.11.15.
 */
public class GS {
    /* ............................................................................ ATTRIBUTES .. */
    public static final int TILE_SIZE = 16;
    public static final int PIXELS_PER_METER = 1;
    public static final int RES_X = 1280;
    public static final int RES_Y = 720;
    public static final int WIDTH = 428;
    public static final int HEIGHT = 240;
    public static final int COL = RES_X/80;
    public static final int ROW = RES_X/80;
    public static final int ONE_STEPDURATION_MS = 5;
    public static final int ONE_STEP_DURATION_PERSON = 20;
    public static final int zoom = 3;
    public static final int MONSTER_SPRITES = 100;

    // ................................................................................... DEBUGGING
    public static final boolean DEBUGGING_ON = true;
    public final static DebugMode DEBUG_MODE = DebugMode.ATTACK_PARSING;
    public final static int startMap = 251;


}
