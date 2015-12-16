package org.limbusdev.monsterworld.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.MusicType;
import org.limbusdev.monsterworld.enums.TextureAtlasType;
import org.limbusdev.monsterworld.utils.GlobalSettings;

import java.util.HashMap;

/**
 * Created by georg on 21.11.15.
 */
public class MediaManager {
    /* ............................................................................ ATTRIBUTES .. */
    private AssetManager assets;

    // file names
    private String mainMenuBGImgFile = "spritesheets/logo.png";
    private String mainMenuBGImgFile2 = "backgrounds/preview.png";
    private String heroSpritesheetFile = "spritesheets/hero.pack";
    private String monsterSpriteSheetFile = "spritesheets/monsters.pack";
    private Array<String> bgs;
    private Array<String> bgMusicTown;
    public  Skin skin;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MediaManager() {
        this.assets = new AssetManager();
        assets.load(this.heroSpritesheetFile, TextureAtlas.class);
        assets.load(this.mainMenuBGImgFile, Texture.class);
        assets.load(this.mainMenuBGImgFile2, Texture.class);

        // Music
        bgMusicTown = new Array<String>();
        bgMusicTown.add("music/town_loop_1.wav");
        bgMusicTown.add("music/town_loop_2.ogg");
        for(String s : bgMusicTown) assets.load(s, Music.class);

        // Monsters
        assets.load(monsterSpriteSheetFile, TextureAtlas.class);

        bgs = new Array<String>();
        bgs.add("backgrounds/grass.png");
        for(String s : bgs) assets.load(s, Texture.class);

        // Fonts ............................................................................. FONTS
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal
                ("fonts/PixelOperator-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator
                .FreeTypeFontParameter();
        param.color = Color.BLACK;
        param.size = 18;
        param.magFilter = Texture.TextureFilter.Nearest;
        param.minFilter = Texture.TextureFilter.Linear;
        BitmapFont font18 = gen.generateFont(param);
        gen.dispose();

        this.skin = new Skin();

        skin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/uiskin.atlas")));
        skin.add("default-font", font18);

        skin.load(Gdx.files.internal("scene2d/uiskin.json"));


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

    public Texture getMainMenuBGImg() {
        return assets.get(mainMenuBGImgFile);
    }
    public Texture getMainMenuBGImg2() {
        return assets.get(mainMenuBGImgFile2);
    }

    public Music getBGMusic(MusicType type, int index) {
        Music music = null;
        switch(type) {
            case TOWN: music = assets.get(bgMusicTown.get(index));break;
        }
        return music;
    }

    public TextureAtlas.AtlasRegion getMonsterSprite(int index) {
        return assets.get(
                monsterSpriteSheetFile,
                TextureAtlas.class).findRegion(Integer.toString(index), 1);
    }

    public Texture getBackgroundTexture(int index) {
        return assets.get(bgs.get(index));
    }


}
