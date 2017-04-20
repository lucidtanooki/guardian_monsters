package de.limbusdev.guardianmonsters.media;

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
    Action getMuteAudioAction(String path);
    Action getFadeInMusicAction(String path);

    /**
     * Stops the currently playing music
     */
    void stopMusic();
}
