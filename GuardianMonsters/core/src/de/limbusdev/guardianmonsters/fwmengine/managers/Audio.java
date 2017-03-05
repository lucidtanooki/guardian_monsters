package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Created by Georg Eckert on 14.11.16.
 */

public interface Audio {

    void playSound(String path);
    void playMusic(String path);
    void playLoopMusic(String path);
    void stopMusic(String path);
    void dispose();
    Action getMuteAudioAction();
}
