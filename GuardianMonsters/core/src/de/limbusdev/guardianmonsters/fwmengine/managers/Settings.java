package de.limbusdev.guardianmonsters.fwmengine.managers;

/**
 * Interface for Settings Service.
 * Use for simply binary settings only. Not for game save data!
 * Created by georg on 14.11.16.
 */

public interface Settings {

    /**
     * Changes the given setting to the given value
     * @param pref  Preference to change
     * @param on    new value of preference
     */
    public void setPref(String pref, boolean on);

    /**
     * Returns the current value of the preference in question
     * @param pref  preference we want to know the value of
     * @param def   default value, when no value is set so far
     * @return      current settings true=on, false=off
     */
    public boolean getPref(String pref, boolean def);
}
