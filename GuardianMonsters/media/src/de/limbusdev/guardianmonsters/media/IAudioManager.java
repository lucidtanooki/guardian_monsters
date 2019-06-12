package de.limbusdev.guardianmonsters.media;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Created by Georg Eckert on 14.11.16.
 */

public interface IAudioManager
{

    void playSound(String path);
    void playMusic(String path);
    void playLoopMusic(String path);
    void stopMusic(String path);
    void dispose();
    Action createMuteAction(String path);
    Action getFadeInMusicAction(String path);
    Action createPlayMusicAction(String path);
    Action createStopMusicAction(String path);
    Action createEndOfBattleMusicSequence();

    /**
     * Stops the currently playing music
     */
    void stopMusic();
}
