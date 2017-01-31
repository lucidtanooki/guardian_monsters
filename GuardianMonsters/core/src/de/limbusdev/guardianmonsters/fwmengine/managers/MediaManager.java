package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.limbusdev.guardianmonsters.enums.TextureAtlasType;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.AnimatedSprite;


/**
 * The MediaManager give access to all media resources
 * MediaManager implements the Design Pattern Singleton
 * Created by Georg Eckert on 21.11.15.
 */
public class MediaManager implements Media {
    /* ............................................................................ ATTRIBUTES .. */
    private AssetManager assets;

    // file names
    private String animations;
    private String monsterSpriteSheetFile;
    private String monsterMiniSpriteSheetFile;
    private String heroSpritesheetFile;

    private Array<String> bgs;
    private Array<String> maleSprites, femaleSprites;
    private Array<Animation> animatedTiles;

    
    /* ................,........................................................... CONSTRUCTOR .. */

    public MediaManager(
        Array<String> texturePackPaths,
        Array<String> texturePaths,
        String monsterSpriteSheetPath,
        String monsterMiniSpriteSheetPath,
        String heroSpriteSheetPath,
        String animationsSpriteSheetPath
    ){

        this.assets = new AssetManager();

        for(String s : texturePackPaths) {
            assets.load(s, TextureAtlas.class);
        }
        for(String s : texturePaths) {
            assets.load(s, Texture.class);
        }

        this.monsterMiniSpriteSheetFile = monsterMiniSpriteSheetPath;
        this.monsterSpriteSheetFile = monsterSpriteSheetPath;
        this.heroSpritesheetFile = heroSpriteSheetPath;
        this.animations = animationsSpriteSheetPath;

        this.maleSprites = new Array<String>();
        for(int i=1;i<=3;i++)this.maleSprites.add("spritesheets/person" + i + "m.pack");
        for(String s : maleSprites) assets.load(s, TextureAtlas.class);
        this.femaleSprites = new Array<String>();


        bgs = new Array<String>();
        bgs.add("grass");
        bgs.add("cave");
        bgs.add("forest");


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
        animatedTiles.add(new Animation(.4f, assets.get(animations, TextureAtlas.class)
            .findRegions("flower1"), Animation.PlayMode.LOOP));
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

    public Texture getTexture(String path) {
        return assets.get(path);
    }

    public TextureAtlas.AtlasRegion getMonsterSprite(int index) {
        TextureAtlas monsterSprites = assets.get(monsterSpriteSheetFile, TextureAtlas.class);
        TextureAtlas.AtlasRegion sprite = monsterSprites.findRegion(Integer.toString(index));
        if(sprite == null) {
            sprite = monsterSprites.findRegion("0");
        }

        return sprite;
    }

    public TextureAtlas.AtlasRegion getMonsterMiniSprite(int index) {
        TextureAtlas monsterSprites = assets.get(monsterMiniSpriteSheetFile, TextureAtlas.class);
        TextureAtlas.AtlasRegion sprite = monsterSprites.findRegion(Integer.toString(index));
        if(sprite == null) {
            sprite = monsterSprites.findRegion("0");
        }

        return sprite;
    }

    public TextureRegion getBackgroundTexture(int index) {
        return assets.get("spritesheets/battleBacks.pack", TextureAtlas.class).findRegion(bgs.get(index));
    }

    public TextureAtlas getTextureAtlas(String path) {
        return assets.get(path, TextureAtlas.class);
    }

    public Animation getAttackAnimation(String attack) {
        TextureAtlas atlas = assets.get("spritesheets/battleAnimations.pack", TextureAtlas.class);
        Animation anim;
        if(atlas.findRegions(attack).size == 0) {
            anim = new Animation(1f / 12f, atlas.findRegions("att_kick"), Animation.PlayMode.NORMAL);
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

    public Animation getObjectAnimation(String id) {
        Animation anim = new Animation(1f, assets.get(animations, TextureAtlas.class)
            .findRegions(id), Animation.PlayMode.LOOP);
        return anim;
    }

}
