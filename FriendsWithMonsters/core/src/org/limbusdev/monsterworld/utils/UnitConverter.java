package org.limbusdev.monsterworld.utils;

/**
 * Created by georg on 21.11.15.
 */
public class UnitConverter {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static float pixelsToMeters(float pixels) {
        return pixels/ GlobalSettings.PIXELS_PER_METER;
    }

    public static float metersToPixels(float meters) {
        return meters*GlobalSettings.PIXELS_PER_METER;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
