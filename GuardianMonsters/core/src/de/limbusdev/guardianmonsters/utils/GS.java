package de.limbusdev.guardianmonsters.utils;


/*
 * Copyright (c) 2016 by Georg Eckert
 *
 * Licensed under GPL 3.0 https://www.gnu.org/licenses/gpl-3.0.en.html
 */

import de.limbusdev.guardianmonsters.enums.DebugMode;

/**
 * Static container for Settings. Every parameter used in the game should be configured here
 * Created by georg on 21.11.15.
 */
public class GS {
    /* ............................................................................ ATTRIBUTES .. */
    public static final int TILE_SIZE = 16;
    public static final int PIXELS_PER_METER = 1;
    public static final int RES_X = 1280;
    public static final int RES_Y = 720;
    public static final int COL = RES_X/80;
    public static final int ROW = RES_X/80;
    public static final int ONE_STEPDURATION_MS = 5;
    public static final int ONE_STEP_DURATION_PERSON = 20;
    public static final int zoom = 3;
    public static final boolean DEBUGGING_ON = false;
    public static final int MONSTER_SPRITES = 22;
    public static final boolean JoyStick = true;
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */

    // ................................................................................... DEBUGGING
    public final static boolean SKIP_START_MENU = true;
    public final static DebugMode DEBUG_MODE = DebugMode.RELEASE;

}
