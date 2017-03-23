package de.limbusdev.guardianmonsters.utils;

/**
 * Created by georg on 21.11.15.
 */
public class UnitConverter {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static int pixelsToMeters(int pixels) {
        return pixels/ de.limbusdev.guardianmonsters.Constant.PIXELS_PER_METER;
    }

    public static int metersToPixels(int meters) {
        return meters* de.limbusdev.guardianmonsters.Constant.PIXELS_PER_METER;
    }

    public static int tilesToPixels(int tiles) {
        return tiles* de.limbusdev.guardianmonsters.Constant.TILE_SIZE;
    }

    public static int pixelsToTiles(int tiles) {
        return de.limbusdev.guardianmonsters.Constant.TILE_SIZE/tiles;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
