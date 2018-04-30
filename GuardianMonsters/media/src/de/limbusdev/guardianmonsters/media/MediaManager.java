package de.limbusdev.guardianmonsters.media;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage;


/**
 * The MediaManager give access to all media resources
 * MediaManager implements the Design Pattern Singleton
 * Created by Georg Eckert on 21.11.15.
 */
public class MediaManager implements IMediaManager
{
    /* ............................................................................ ATTRIBUTES .. */
    private AnnotationAssetManager assets;

    private Array<String> bgs;
    private Array<String> maleSprites, femaleSprites;
    private Array<Animation<AtlasRegion>> animatedTiles;

    
    /* ................,........................................................... CONSTRUCTOR .. */

    public MediaManager()
    {
        this.assets = new AnnotationAssetManager(new InternalFileHandleResolver());
        assets.load(AssetPath.Spritesheet.class);
        assets.load(AssetPath.Textures.class);
        assets.finishLoading();


        this.maleSprites = new Array<>();
        for(int i=1;i<=9;i++)this.maleSprites.add("spritesheets/person" + i + "m.pack");
        for(String s : maleSprites) assets.load(s, TextureAtlas.class);
        this.femaleSprites = new Array<>();
        for(int i=1;i<=2;i++)this.femaleSprites.add("spritesheets/person" + i + "f.pack");
        for(String s : femaleSprites) assets.load(s, TextureAtlas.class);


        bgs = new Array<>();
        bgs.add("grass");
        bgs.add("cave");
        bgs.add("forest");


        // Animated Tiles
        animatedTiles = new Array<>();

        assets.finishLoading();

        String animations = AssetPath.Spritesheet.ANIMATIONS;
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("water"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterine"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterinw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterise"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterisw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watern"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterne"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waternw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waters"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("waterse"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watersw"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
                .findRegions("watere"), Animation.PlayMode.LOOP));
        animatedTiles.add(new Animation<>(1f, assets.get(animations, TextureAtlas.class)
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
                atlas = assets.get(AssetPath.Spritesheet.HERO);break;
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
    public ArrayMap<SkyDirection, Animation<AtlasRegion>> getPersonAnimationSet(TextureAtlas textureAtlas)
    {
        ArrayMap<SkyDirection, Animation<AtlasRegion>> animations = new ArrayMap<>();

        animations.put(SkyDirection.N, new Animation<>(.15f, textureAtlas.findRegions("n"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.E, new Animation<>(.15f, textureAtlas.findRegions("e"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.S, new Animation<>(.15f, textureAtlas.findRegions("s"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.W, new Animation<>(.15f, textureAtlas.findRegions("w"), Animation.PlayMode.LOOP));
        animations.put(SkyDirection.NSTOP, new Animation<>(.15f, textureAtlas.findRegions("n").get(0)));
        animations.put(SkyDirection.ESTOP, new Animation<>(.15f, textureAtlas.findRegions("e").get(0)));
        animations.put(SkyDirection.SSTOP, new Animation<>(.15f, textureAtlas.findRegions("s").get(0)));
        animations.put(SkyDirection.WSTOP, new Animation<>(.15f, textureAtlas.findRegions("w").get(0)));

        return animations;
    }

    public Texture getTexture(String path) {
        return assets.get(path);
    }

    @Override
    public TextureAtlas.AtlasRegion getMonsterSprite(int index, int form) {
        TextureAtlas monsterSprites = assets.get(AssetPath.Spritesheet.GUARDIANS, TextureAtlas.class);
        TextureAtlas.AtlasRegion sprite = monsterSprites.findRegion(Integer.toString(index), form);
        if(sprite == null) {
            sprite = monsterSprites.findRegion("0");
        }

        return sprite;
    }

    @Override
    public TextureAtlas.AtlasRegion getMonsterMiniSprite(int index, int form) {
        TextureAtlas monsterSprites = assets.get(AssetPath.Spritesheet.GUARDIANS_MINI, TextureAtlas.class);
        TextureAtlas.AtlasRegion sprite = monsterSprites.findRegion(Integer.toString(index), form);
        if(sprite == null) {
            sprite = monsterSprites.findRegion("0");
        }

        return sprite;
    }

    @Override
    public Image getMonsterFace(int id, int form) {
        TextureRegion region = assets.get(AssetPath.Spritesheet.GUARDIANS_PREVIEW, TextureAtlas.class)
            .findRegion(Integer.toString(id),form);
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

    public Animation<AtlasRegion> getAttackAnimation(String attack)
    {
        TextureAtlas atlas = assets.get("spritesheets/battleAnimations.pack", TextureAtlas.class);
        Animation<AtlasRegion> anim;
        if(atlas.findRegions(attack).size == 0) {
            anim = new Animation<>(1f / 12f, atlas.findRegions("att_kick"), Animation.PlayMode.NORMAL);
        } else {
            anim = new Animation<>(1f / 12f, atlas.findRegions(attack), Animation.PlayMode.NORMAL);
        }
        return anim;
    }

    @Override
    public Animation<AtlasRegion> getStatusEffectAnimation(String statusEffect)
    {
        TextureAtlas atlas = assets.get("spritesheets/statusEffectAnimations.pack", TextureAtlas.class);
        Animation<AtlasRegion> anim;

        if(atlas.findRegions("status_effect_" + statusEffect).size == 0) {
            anim = new Animation<>(1f / 12f, atlas.findRegions("status_effect_healthy"), Animation.PlayMode.LOOP);
        } else {
            anim = new Animation<>(1f / 12f, atlas.findRegions("status_effect_" + statusEffect), Animation.PlayMode.LOOP);
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

    public Animation<AtlasRegion> getObjectAnimation(String id)
    {
        TextureAtlas atlas = assets.get(AssetPath.Spritesheet.ANIMATIONS, TextureAtlas.class);
        Array<AtlasRegion> regions = atlas.findRegions(id);
        Animation<AtlasRegion> anim = new Animation<>(1f, regions);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    @Override
    public Image getMetamorphosisBackground()
    {
        TextureAtlas atlas = getTextureAtlas(AssetPath.Spritesheet.BATTLE_BG);
        Image img = new Image(atlas.findRegion("metamorph_bg"));
        return img;
    }

    @Override
    public AnimatedImage getMetamorphosisAnimation()
    {
        Animation animation = new Animation(.15f,getTextureAtlas(AssetPath.Spritesheet.ANIMATIONS_BIG).findRegions("metamorphosis"));
        AnimatedImage metamorphosisAnimation = new AnimatedImage(animation);
        metamorphosisAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        return metamorphosisAnimation;
    }

}
