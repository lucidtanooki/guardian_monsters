package org.limbusdev.monsterworld.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.MusicType;
import org.limbusdev.monsterworld.enums.TextureAtlasType;

import java.util.HashMap;

/**
 * Created by georg on 21.11.15.
 */
public class MediaManager {
    /* ............................................................................ ATTRIBUTES .. */
    private AssetManager assets;

    // file names
    private String mainMenuBGImgFile = "";
    private String heroSpritesheetFile = "spritesheets/hero.pack";
    private Array<String> bgMusicTown;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MediaManager() {
        this.assets = new AssetManager();
        assets.load(this.heroSpritesheetFile, TextureAtlas.class);

        // Music
        bgMusicTown = new Array<String>();
        bgMusicTown.add("music/town_loop_1.wav");
        for(String s : bgMusicTown) assets.load(s, Music.class);
        assets.finishLoading();
    }
    /* ............................................................................... METHODS .. */

    public void dispose() {
        this.assets.dispose();
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public TextureAtlas getTextureAtlasType(TextureAtlasType type) {
        TextureAtlas atlas;
        switch(type) {
            case HERO:
                atlas = assets.get(heroSpritesheetFile);break;
            default:
                atlas = null;
                System.err.println("TextureAtlasType " + type + " not found.");
                break;
        }
        return atlas;
    }

    public Music getBGMusic(MusicType type, int index) {
        Music music = null;
        switch(type) {
            case TOWN: music = assets.get(bgMusicTown.get(index));break;
        }
        return music;
    }
}
