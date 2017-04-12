package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.AnimatedImage;
import de.limbusdev.guardianmonsters.fwmengine.world.model.SkyDirection;


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
    private String monsterFaceSpriteSheetFile;

    private Array<String> bgs;
    private Array<String> maleSprites, femaleSprites;
    private Array<Animation<AtlasRegion>> animatedTiles;

    
    /* ................,........................................................... CONSTRUCTOR .. */

    public MediaManager(
        Array<String> texturePackPaths,
        Array<String> texturePaths,
        String monsterSpriteSheetPath,
        String monsterMiniSpriteSheetPath,
        String heroSpriteSheetPath,
        String animationsSpriteSheetPath,
        String monsterFaceSpriteSheetPath
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
        this.monsterFaceSpriteSheetFile = monsterFaceSpriteSheetPath;

        this.maleSprites = new Array<String>();
        for(int i=1;i<=9;i++)this.maleSprites.add("spritesheets/person" + i + "m.pack");
        for(String s : maleSprites) assets.load(s, TextureAtlas.class);
        this.femaleSprites = new Array<String>();
        for(int i=1;i<=2;i++)this.femaleSprites.add("spritesheets/person" + i + "f.pack");
        for(String s : femaleSprites) assets.load(s, TextureAtlas.class);


        bgs = new Array<String>();
        bgs.add("grass");
        bgs.add("cave");
        bgs.add("forest");


        // Animated Tiles
        animatedTiles = new Array<Animation<AtlasRegion>>();

        assets.finishLoading();

        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("water"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterine"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterinw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterise"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterisw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watern"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterne"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waternw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waters"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterse"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watersw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watere"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<AtlasRegion>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterw"), Animation.PlayMode.LOOP));
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

    @Override
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(boolean gender, int index) {
        TextureAtlas textureAtlas = getPersonTextureAtlas(gender, index);
        return getPersonAnimationSet(textureAtlas);
    }

    @Override
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(String name) {

        TextureAtlas textureAtlas;
        if(name.equals("hero")) {
            textureAtlas = getTextureAtlasType(TextureAtlasType.HERO);
        } else {
            textureAtlas = getPersonTextureAtlas(true,1);
        }

        return getPersonAnimationSet(textureAtlas);
    }

    @Override
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(TextureAtlas textureAtlas) {
        ArrayMap<SkyDirection, Animation<AtlasRegion>> animations = new ArrayMap<SkyDirection,Animation<AtlasRegion>>();

        animations.put(SkyDirection.N, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("n"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.E, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("e"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.S, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("s"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.W, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("w"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.NSTOP, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("n").get(0)));
        animations.put(SkyDirection.ESTOP, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("e").get(0)));
        animations.put(SkyDirection.SSTOP, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("s").get(0)));
        animations.put(SkyDirection.WSTOP, new Animation<AtlasRegion>(.15f, textureAtlas.findRegions("w").get(0)));

        return animations;
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

    @Override
    public Image getMonsterFace(int id) {
        TextureRegion region = assets.get(monsterFaceSpriteSheetFile, TextureAtlas.class).findRegion(Integer.toString(id));
        Image faceImg = new Image(region);
        faceImg.setSize(24,23);
        return faceImg;
    }

    public TextureRegion getBackgroundTexture(int index) {
        return assets.get("spritesheets/battleBacks.pack", TextureAtlas.class).findRegion(bgs.get(index));
    }

    public TextureAtlas getTextureAtlas(String path) {
        return assets.get(path, TextureAtlas.class);
    }

    public Animation<AtlasRegion> getAttackAnimation(String attack) {
        TextureAtlas atlas = assets.get("spritesheets/battleAnimations.pack", TextureAtlas.class);
        Animation<AtlasRegion> anim;
        if(atlas.findRegions(attack).size == 0) {
            anim = new Animation<AtlasRegion>(1f / 12f, atlas.findRegions("att_kick"), Animation.PlayMode.NORMAL);
        } else {
            anim = new Animation<AtlasRegion>(1f / 12f, atlas.findRegions(attack), Animation.PlayMode.NORMAL);
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
     * @param index
     * @return
     */
    public Animation<AtlasRegion> getTileAnimation(int index) {
        return animatedTiles.get(index);
    }

    public Animation<AtlasRegion> getObjectAnimation(String id) {
        TextureAtlas atlas = assets.get(animations, TextureAtlas.class);
        Array<AtlasRegion> regions = atlas.findRegions(id);
        Animation<AtlasRegion> anim = new Animation<>(1f, regions);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    @Override
    public Image getMetamorphosisBackground() {
        TextureAtlas atlas = getTextureAtlas(TextureAssets.battleBackgrounds);
        Image img = new Image(atlas.findRegion("metamorph_bg"));
        return img;
    }

    @Override
    public AnimatedImage getMetamorphosisAnimation() {
        Animation animation = new Animation(.15f,getTextureAtlas(TextureAssets.bigAnimations).findRegions("metamorphosis"));
        AnimatedImage metamorphosisAnimation = new AnimatedImage(animation);
        metamorphosisAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        return metamorphosisAnimation;
    }

}
