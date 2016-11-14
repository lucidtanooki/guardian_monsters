package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by georg on 14.11.16.
 */

public class NullL18N implements L18N {
    @Override
    public I18NBundle l18n() {
        return null;
    }

    @Override
    public BitmapFont getFont() {
        return null;
    }
}
