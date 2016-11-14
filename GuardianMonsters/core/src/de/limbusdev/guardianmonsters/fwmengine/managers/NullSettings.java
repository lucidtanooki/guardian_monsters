package de.limbusdev.guardianmonsters.fwmengine.managers;

/**
 * Does nothing
 * Created by georg on 14.11.16.
 */

public class NullSettings implements Settings {

    @Override
    public void setPref(String pref, boolean on) {
        ;
    }

    @Override
    public boolean getPref(String pref, boolean def) {
        return def;
    }
}
