package de.limbusdev.guardianmonsters.data;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * Created by georg on 14.11.16.
 */

public class SkinAssets {

    public static final String uiskin = "scene2d/uiskin.atlas";
    public static final String uipack = "scene2d/UI.pack";
    public static final String defaultFont = "fonts/PixelOperator-Bold.ttf";

    public static String attackButtonStyle(Element element) {
        return "tb-attack-" + element.toString().toLowerCase();
    }



}
