package de.limbusdev.guardianmonsters.media;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;

/**
 * @author Georg Eckert
 */
public class AudioManager implements IAudioManager
{

    private String currentlyPlayingBGMusic;

    private AnnotationAssetManager assets;

    public AudioManager()
    {
        assets = new AnnotationAssetManager(new InternalFileHandleResolver());
        assets.load(AssetPath.Audio.SFX.class);
        assets.load(AssetPath.Audio.Music.class);
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
    public Action getMuteAudioAction(String musicPath) {
        final Music muteable = assets.get(musicPath, Music.class);
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

    @Override
    public Action getFadeInMusicAction(String musicPath)
    {
        final Music music = assets.get(musicPath, Music.class);
        music.setLooping(true);
        Action fadeInAction = Actions.sequence(
            Actions.delay(.005f),
            Actions.repeat(100,Actions.run(new Runnable() {
                private int vol = 0;
                @Override
                public void run() {
                    music.play();
                    if(vol < 100) {
                        ++vol;
                        music.setVolume(vol/100f);
                    } else {
                        music.setVolume(100);
                    }
                }
            })));
        return fadeInAction;
    }
}
