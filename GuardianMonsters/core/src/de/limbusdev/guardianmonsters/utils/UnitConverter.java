package de.limbusdev.guardianmonsters.utils;

import de.limbusdev.guardianmonsters.Constant;

public class UnitConverter
{
    public static int pixelsToMeters(int pixels) {
        return pixels/ Constant.PIXELS_PER_METER;
    }

    public static int metersToPixels(int meters) {
        return meters * Constant.PIXELS_PER_METER;
    }

    public static int tilesToPixels(int tiles) {
        return tiles * Constant.TILE_SIZE;
    }

    public static int pixelsToTiles(int tiles) {
        return Constant.TILE_SIZE/tiles;
    }
}
