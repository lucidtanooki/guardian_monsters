package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;


/**
 * Singleton
 * Created by Georg Eckert 2017
 */
public interface L18N {

    I18NBundle i18n(String type);

    I18NBundle i18nMap(int mapID);

    BitmapFont Font();

    I18NBundle General();

    I18NBundle Abilities();

    I18NBundle Guardians();

    I18NBundle Elements();

    I18NBundle Inventory();

    I18NBundle Battle();

    // Short Accessors
    String Battle(String key);
    String Battle(String key, Object... args);
    String Inventory(String key);
    String General(String key);

    /**
     * Returns the nickname of the given Guardian, if it has one. Otherwise the localized species
     * name is used.
     * @param guardian  Guardian in question
     * @return          nickname if available, localized species name otherwise
     */
    String getGuardianNicknameIfAvailable(AGuardian guardian);

    String getLocalizedGuardianName(AGuardian guardian);

    String getLocalizedGuardianName(int speciesID, int form);

    String getLocalizedGuardianDescription(int speciesID);

    String getLocalizedAbilityName(String abilityID);

    void dispose();
}
