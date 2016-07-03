package org.limbusdev.monsterworld.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.MusicType;
import org.limbusdev.monsterworld.enums.SFXType;
import org.limbusdev.monsterworld.enums.TextureAtlasType;

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
    private String battleUISpriteSheetFile = "spritesheets/battleUI.pack";
    private String UISpriteSheetFile = "spritesheets/UI.pack";
    private String logosSpriteSheetFile = "spritesheets/logos.pack";
    private String animations = "spritesheets/animations.pack";
    private String SFXdir = "sfx/hits/";
    private Array<String> sfxHits;
    private Array<String> bgs;
    private Array<String> bgMusicTown;
    private Array<String> battleMusic;
    private Array<String> maleSprites, femaleSprites;
    private Array<Animation> animatedTiles;
    public  Skin skin;
    
    /* ........................................................................... CONSTRUCTOR .. */
    public MediaManager() {
        this.assets = new AssetManager();
        assets.load(this.heroSpritesheetFile, TextureAtlas.class);
        assets.load(this.battleUISpriteSheetFile, TextureAtlas.class);
        assets.load(this.UISpriteSheetFile, TextureAtlas.class);
        assets.load(this.animations, TextureAtlas.class);
        assets.load(this.logosSpriteSheetFile, TextureAtlas.class);
        assets.load(this.mainMenuBGImgFile, Texture.class);
        assets.load(this.mainMenuBGImgFile2, Texture.class);

        this.maleSprites = new Array<String>();
        for(int i=1;i<=3;i++)this.maleSprites.add("spritesheets/person" + i + "m.pack");
        for(String s : maleSprites) assets.load(s, TextureAtlas.class);
        this.femaleSprites = new Array<String>();

        // Music
        bgMusicTown = new Array<String>();
        bgMusicTown.add("music/town_loop_1.wav");
        bgMusicTown.add("music/town_loop_2.ogg");
        for(String s : bgMusicTown) assets.load(s, Music.class);

        battleMusic = new Array<String>();
        battleMusic.add("music/battle_1.mp3");
        for(String s : battleMusic) assets.load(s, Music.class);

        // SFX
        sfxHits = new Array<String>();
        for(int i=1;i<=18;i++) sfxHits.add(SFXdir + i + ".ogg");
        for(String s : sfxHits) assets.load(s, Sound.class);

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
        param.size = 32;
        param.magFilter = Texture.TextureFilter.Nearest;
        param.minFilter = Texture.TextureFilter.Linear;
        BitmapFont font32 = gen.generateFont(param);
        param.color = Color.WHITE;
        param.size = 32;
        param.magFilter = Texture.TextureFilter.Nearest;
        param.minFilter = Texture.TextureFilter.Linear;
        BitmapFont font32w = gen.generateFont(param);
        gen.dispose();

        this.skin = new Skin();

        skin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/uiskin.atlas")));
        skin.add("default-font", font32);
        skin.add("white", font32w);

        skin.load(Gdx.files.internal("scene2d/uiskin.json"));

        // Animated Tiles
        animatedTiles = new Array<Animation>();

        assets.finishLoading();

        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("water"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterine"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterinw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterise"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterisw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watern"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterne"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waternw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waters"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterse"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watersw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watere"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(.5f, assets.get(animations, TextureAtlas.class)
                .findRegions("fire2"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(.5f, assets.get(animations, TextureAtlas.class)
                .findRegions("fire1"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(.5f, assets.get(animations, TextureAtlas.class)
                .findRegions("lamp1"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation(.4f, assets.get(animations, TextureAtlas.class)
                .findRegions("light1"), Animation.PlayMode.LOOP));
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

    /**
     * Texture Atlas for a person
     * @param male  true=male, false=female
     * @param index
     * @return
     */
    public TextureAtlas getPersonTextureAtlas(boolean male, int index) {
        TextureAtlas atlas;
        if(male) {
            atlas = assets.get(maleSprites.get(index),TextureAtlas.class);
        } else {
            atlas = assets.get(femaleSprites.get(index),TextureAtlas.class);
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
            case BATTLE: music = assets.get(battleMusic.get(index));break;
        }
        return music;
    }

    public TextureAtlas.AtlasRegion getMonsterSprite(int index) {
        return assets.get(monsterSpriteSheetFile,
                TextureAtlas.class).findRegion(Integer.toString(index), 1);
    }

    public Texture getBackgroundTexture(int index) {
        return assets.get(bgs.get(index));
    }

    public TextureAtlas getBattleUITextureAtlas() {
        return assets.get(battleUISpriteSheetFile, TextureAtlas.class);
    }

    public TextureAtlas getUITextureAtlas() {
        return assets.get(UISpriteSheetFile, TextureAtlas.class);
    }

    public TextureAtlas getLogosTextureAtlas() {
        return assets.get(logosSpriteSheetFile, TextureAtlas.class);
    }

    public Sound getSFX(SFXType sfxType, int index) {
        Sound sound = null;
        switch(sfxType) {
            case HIT: sound = assets.get(sfxHits.get(index));
        }

        return sound;
    }

    /**
     * 0 - Water
     * 1 - Water Inner NE
     * 2 - Water Inner NW
     * 3 - Water Inner SE
     * 4 - Water Inner SW
     * 5 - Water N
     * 6 - Water NE
     * 7 - Water NW
     * 8 - Water S
     * 9 - Water SE
     * 10 - Water SW
     * 11 - Water E
     * 12 - Water W
     * 13 - Fire 2
     * 14 - Fire 1
     * 15 - Lamp 1
     * 16 - Light 1
     * @param index
     * @return
     */
    public Animation getTileAnimation(int index) {
        return animatedTiles.get(index);
    }


}
