package org.limbusdev.monsterworld.utils;

/**
 * Created by georg on 21.11.15.
 */
public class UnitConverter {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static int pixelsToMeters(int pixels) {
        return pixels/ GlobPref.PIXELS_PER_METER;
    }

    public static int metersToPixels(int meters) {
        return meters* GlobPref.PIXELS_PER_METER;
    }

    public static int tilesToPixels(int tiles) {
        return tiles* GlobPref.TILE_SIZE;
    }

    public static int pixelsToTiles(int tiles) {
        return GlobPref.TILE_SIZE/tiles;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
