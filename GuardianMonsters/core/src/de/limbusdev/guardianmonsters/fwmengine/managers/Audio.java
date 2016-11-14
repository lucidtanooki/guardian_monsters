package de.limbusdev.guardianmonsters.fwmengine.managers;

/**
 * Created by georg on 14.11.16.
 */

public interface Audio {

    public void playSound(String path);
    public void playMusic(String path);
    public void playLoopMusic(String path);
    public void stopMusic(String path);
}
