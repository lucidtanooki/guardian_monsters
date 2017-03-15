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

    I18NBundle i18nGeneral();

    I18NBundle i18nAbilities();

    I18NBundle i18nMonsters();

    I18NBundle i18nElements();

    I18NBundle i18nInventory();

    I18NBundle i18nBattle();

    void dispose();
}
