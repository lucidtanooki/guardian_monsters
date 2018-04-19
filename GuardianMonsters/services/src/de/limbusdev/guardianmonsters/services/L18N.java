package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;


/**
 * Singleton
 * Created by Georg Eckert 2017
 */
public interface L18N {

    I18NBundle l18n(String type);

    I18NBundle l18nMap(int mapID);

    BitmapFont Font();

    I18NBundle General();

    I18NBundle Abilities();

    I18NBundle Guardians();

    I18NBundle Elements();

    I18NBundle Inventory();

    I18NBundle Battle();

    String getLocalizedGuardianName(AGuardian guardian);

    String getLocalizedGuardianName(int speciesID, int form);

    String getLocalizedAbilityName(String abilityID);

    void dispose();
}
