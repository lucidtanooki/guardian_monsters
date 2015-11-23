package org.limbusdev.monsterworld.utils;

/**
 * Created by georg on 21.11.15.
 */
public class UnitConverter {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static int pixelsToMeters(int pixels) {
        return pixels/ GlobalSettings.PIXELS_PER_METER;
    }

    public static int metersToPixels(int meters) {
        return meters*GlobalSettings.PIXELS_PER_METER;
    }

    public static int tilesToPixels(int tiles) {
        return tiles*GlobalSettings.TILE_SIZE;
    }

    public static int pixelsToTiles(int tiles) {
        return GlobalSettings.TILE_SIZE/tiles;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
