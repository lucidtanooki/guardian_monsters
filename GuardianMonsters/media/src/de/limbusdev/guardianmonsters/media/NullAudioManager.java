package de.limbusdev.guardianmonsters.media;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Created by georg on 14.11.16.
 */

public class NullAudioManager implements IAudioManager
{
    @Override
    public void playSound(String path) {

    }

    @Override
    public void playMusic(String path) {

    }

    @Override
    public void playLoopMusic(String path) {

    }

    @Override
    public void stopMusic(String path) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public Action createMuteAction(String path) {
        return Actions.sequence();
    }

    @Override
    public Action getFadeInMusicAction(String path) {
        return Actions.sequence();
    }

    @Override
    public Action createPlayMusicAction(String path)  {
        return Actions.sequence();
    }

    @Override
    public Action createStopMusicAction(String path)  {
        return Actions.sequence();
    }

    @Override
    public Action createEndOfBattleMusicSequence()   {
        return Actions.sequence();
    }

    @Override
    public void stopMusic() {}
}
