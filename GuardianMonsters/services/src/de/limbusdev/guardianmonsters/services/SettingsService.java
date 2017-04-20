package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by georg on 14.11.16.
 */

public class SettingsService implements Settings {

    @Override
    public void setPref(String pref, boolean on) {
        Preferences prefs = Gdx.app.getPreferences("simpleSettings");
        prefs.putBoolean(pref,on);
        prefs.flush();
    }

    @Override
    public boolean getPref(String pref, boolean def) {
        Preferences prefs = Gdx.app.getPreferences("simpleSettings");
        return prefs.getBoolean(pref,def);
    }
}
