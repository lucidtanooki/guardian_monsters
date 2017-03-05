package de.limbusdev.guardianmonsters.data;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * @author Georg Eckert 2017
 */

public class SkinAssets {

    public static final String defaultSkin      = "scene2d/defaultSkin";
    public static final String battleSkin       = "scene2d/battleSkin";
    public static final String inventorySkin    = "scene2d/inventorySkin";
    public static final String defaultFont      = "fonts/PixelOperator-Bold.ttf";

    public static String attackButtonStyle(Element element) {
        return "tb-attack-" + element.toString().toLowerCase();
    }



}
