package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

public class NullL18N implements L18N
{
    @Override
    public I18NBundle l18n(String type)
    {
        return null;
    }

    @Override
    public I18NBundle l18nMap(int mapID)
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
    public String getLocalizedGuardianName(Guardian guardian)
    {
        return null;
    }

    @Override
    public String getLocalizedGuardianName(int guardianID)
    {
        return null;
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
