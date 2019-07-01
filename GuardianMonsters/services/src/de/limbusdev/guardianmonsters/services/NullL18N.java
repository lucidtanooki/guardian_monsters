package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

public class NullL18N implements L18N
{
    @Override
    public I18NBundle i18n(String type)
    {
        return null;
    }

    @Override
    public I18NBundle i18nMap(int mapID)
    {
        return null;
    }

    @Override
    public BitmapFont Font() {
        return null;
    }

    @Override
    public I18NBundle General() {
        return null;
    }

    @Override
    public I18NBundle Abilities() {
        return null;
    }

    @Override
    public I18NBundle Guardians() {
        return null;
    }

    @Override
    public I18NBundle Elements() {
        return null;
    }

    @Override
    public I18NBundle Inventory() {
        return null;
    }

    @Override
    public I18NBundle Battle() {
        return null;
    }

    @Override
    public String Battle(String key) { return ""; }

    @Override
    public String Battle(String key, Object... args) { return ""; }

    @Override
    public String Inventory(String key) { return ""; }

    @Override
    public String General(String key) { return ""; }

    @Override
    public String getGuardianNicknameIfAvailable(AGuardian guardian)
    {
        return "NullL18N-Dummy";
    }

    @Override
    public String getLocalizedGuardianName(AGuardian guardian)
    {
        return null;
    }

    @Override
    public String getLocalizedGuardianName(int guardianID, int form)
    {
        return null;
    }

    @Override
    public String getLocalizedGuardianDescription(int speciesID) {
        return "NullL18N: dummy description";
    }

    @Override
    public String getLocalizedAbilityName(String abilityID)
    {
        return null;
    }

    @Override
    public void dispose() {
        // DO Nothing
    }
}
