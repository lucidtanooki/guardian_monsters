package de.limbusdev.guardianmonsters.utils

import de.limbusdev.guardianmonsters.Constant

object UnitConverter
{
    fun pixelsToMeters(pixels: Int): Int =  pixels / Constant.PIXELS_PER_METER

    fun metersToPixels(meters: Int): Int = meters * Constant.PIXELS_PER_METER

    fun tilesToPixels(tiles: Int): Int = tiles * Constant.TILE_SIZE

    fun pixelsToTiles(tiles: Int): Int = Constant.TILE_SIZE / tiles
}
