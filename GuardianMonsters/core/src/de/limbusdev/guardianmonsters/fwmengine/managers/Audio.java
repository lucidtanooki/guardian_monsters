package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Created by georg on 14.11.16.
 */

public interface Audio {

    public void playSound(String path);
    public void playMusic(String path);
    public void playLoopMusic(String path);
    public void stopMusic(String path);
    public void dispose();
    Action getMuteAudioAction();
}
