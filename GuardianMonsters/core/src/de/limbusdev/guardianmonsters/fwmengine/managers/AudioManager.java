package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 14.11.16.
 */

public class AudioManager implements Audio {

    private String currentlyPlayingBGMusic;

    private AssetManager assets;

    public AudioManager(Array<String> sfxPaths, Array<String> musicPaths) {
        assets = new AssetManager();
        for(String s : sfxPaths)
            assets.load(s,Sound.class);
        for(String s : musicPaths)
            assets.load(s,Music.class);
        assets.finishLoading();
    }

    @Override
    public void playSound(String path) {
        Sound sfx = assets.get(path,Sound.class);
        sfx.play();
    }

    @Override
    public void playMusic(String path) {
        Music music = assets.get(path,Music.class);

        // If there is music playing, check, whether it is the same, like the requested
        if(currentlyPlayingBGMusic != null && !path.equals(currentlyPlayingBGMusic)) {
            Music currentMusic = assets.get(currentlyPlayingBGMusic,Music.class);
            currentMusic.stop();
        }

        // Prevents Music from starting again, if it is already playing
        if(!music.isPlaying())
            music.play();

        // Store path of currently playing music
        currentlyPlayingBGMusic = path;

    }

    @Override
    public void playLoopMusic(String path) {
        Music music = assets.get(path,Music.class);
        music.setVolume(1);
        music.setLooping(true);

        playMusic(path);
    }

    @Override
    public void stopMusic(String path) {
        Music music = assets.get(path,Music.class);
        music.stop();
        currentlyPlayingBGMusic = null;
    }

    @Override
    public void stopMusic() {
        stopMusic(currentlyPlayingBGMusic);
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

    @Override
    public Action getMuteAudioAction() {
        // TODO sometimes the next battle is muted, because there was no time at the end of
        // the last battle to finish this action
        final Music muteable = assets.get(currentlyPlayingBGMusic, Music.class);
        Action muteAction = Actions.sequence(
            Actions.delay(.005f),
            Actions.repeat(100,Actions.run(new Runnable() {
            private int vol = 100;
            @Override
            public void run() {
                if(vol > 0) {
                    --vol;
                    muteable.setVolume(vol/100f);
                } else {
                    muteable.stop();
                    muteable.setVolume(1);
                }
            }
        })));
        return muteAction;
    }
}
