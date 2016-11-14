package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.limbusdev.guardianmonsters.enums.TextureAtlasType;


/**
 * The MediaManager give access to all media resources
 * MediaManager implements the Design Pattern Singleton
 * Created by Georg Eckert on 21.11.15.
 */
public class MediaManager implements Media {
    /* ............................................................................ ATTRIBUTES .. */
    private AssetManager assets;

    // file names
    private String mainMenuBGImgFile = "spritesheets/GM_logo.png";
    private String mainMenuBGImgFile2 = "spritesheets/main_logo_bg.png";
    private String heroSpritesheetFile = "spritesheets/hero.pack";
    private String monsterSpriteSheetFile = "spritesheets/monsters.pack";
    private String battleUISpriteSheetFile = "spritesheets/battleUI.pack";
    private String UISpriteSheetFile = "spritesheets/UI.pack";
    private String logosSpriteSheetFile = "spritesheets/logos.pack";
    private String animations = "spritesheets/animations.pack";
    private String battleAnimations = "spritesheets/battleAnimations.pack";
    private Array<String> bgs;
    private Array<String> maleSprites, femaleSprites;
    private Array<Animation> animatedTiles;
    private Skin skin, battleSkin, inventorySkin;

    
    /* ................,........................................................... CONSTRUCTOR .. */

    public MediaManager() {
        this.assets = new AssetManager();
        assets.load(this.heroSpritesheetFile, TextureAtlas.class);
        assets.load(this.battleUISpriteSheetFile, TextureAtlas.class);
        assets.load(this.UISpriteSheetFile, TextureAtlas.class);
        assets.load(this.animations, TextureAtlas.class);
        assets.load(this.battleAnimations, TextureAtlas.class);
        assets.load(this.logosSpriteSheetFile, TextureAtlas.class);
        assets.load(this.mainMenuBGImgFile, Texture.class);
        assets.load(this.mainMenuBGImgFile2, Texture.class);

        this.maleSprites = new Array<String>();
        for(int i=1;i<=3;i++)this.maleSprites.add("spritesheets/person" + i + "m.pack");
        for(String s : maleSprites) assets.load(s, TextureAtlas.class);
        this.femaleSprites = new Array<String>();

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
        skin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/UI.pack")));
        skin.add("default-font", font32);
        skin.add("white", font32w);
        skin.load(Gdx.files.internal("scene2d/uiskin.json"));

        this.battleSkin = new Skin();
        battleSkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/battleUI.pack")));
        battleSkin.add("default-font", font32);
        battleSkin.add("white", font32w);
        battleSkin.load(Gdx.files.internal("scene2d/battleuiskin.json"));

        this.inventorySkin = new Skin();
        inventorySkin.addRegions(new TextureAtlas(Gdx.files.internal("scene2d/inventoryUI.pack")));
        inventorySkin.add("default-font", font32);
        inventorySkin.add("white", font32w);
        inventorySkin.load(Gdx.files.internal("scene2d/inventoryUIskin.json"));

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
        assets.dispose();
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

    public Animation getAttackAnimation(String attack) {
        TextureAtlas atlas = assets.get("spritesheets/battleAnimations.pack", TextureAtlas.class);
        Animation anim;
        if(atlas.findRegions(attack).size == 0) {
            anim = new Animation(1f / 12f, atlas.findRegions("Kick"), Animation.PlayMode.NORMAL);
        } else {
            anim = new Animation(1f / 12f, atlas.findRegions(attack), Animation.PlayMode.NORMAL);
        }
        return anim;
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


    public Skin getBattleSkin() {
        return battleSkin;
    }

    public Skin getInventorySkin() {
        return inventorySkin;
    }

    public Skin getSkin() {
        return skin;
    }
}
