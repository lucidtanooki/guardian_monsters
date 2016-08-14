package de.limbusdev.guardianmonsters.utils;

/**
 * Created by georg on 21.11.15.
 */
public class UnitConverter {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static int pixelsToMeters(int pixels) {
        return pixels/ GS.PIXELS_PER_METER;
    }

    public static int metersToPixels(int meters) {
        return meters* GS.PIXELS_PER_METER;
    }

    public static int tilesToPixels(int tiles) {
        return tiles* GS.TILE_SIZE;
    }

    public static int pixelsToTiles(int tiles) {
        return GS.TILE_SIZE/tiles;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
