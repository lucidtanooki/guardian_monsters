package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;


/**
 * Singleton
 * Created by Georg Eckert 2017
 */
public interface L18N {

    I18NBundle l18n(int type);

    I18NBundle l18nMap(int mapID);

    BitmapFont getFont();

    void dispose();
}
