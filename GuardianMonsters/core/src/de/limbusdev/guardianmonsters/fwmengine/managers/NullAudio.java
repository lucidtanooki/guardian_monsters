package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Created by georg on 14.11.16.
 */

public class NullAudio implements Audio {
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
    public Action getMuteAudioAction() {
        return Actions.sequence();
    }
}
