package de.limbusdev.guardianmonsters.media

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import net.dermetfan.gdx.assets.AnnotationAssetManager

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage


/**
 * The MediaManager give access to all media resources.
 *
 * @author Georg Eckert 2015
 */
class MediaManager : IMediaManager
{
    // .................................................................................. Properties
    private val assets: AnnotationAssetManager

    private val backgrounds: Array<String> = Array()
    private val maleSprites: Array<String> = Array()
    private val femaleSprites: Array<String> = Array()
    private val animatedTiles: Array<Animation<TextureRegion>>


    // ................................................................................ Constructors
    init
    {
        // Create Annotation Asset Manager
        assets = AnnotationAssetManager(InternalFileHandleResolver())

        // Load Assets from Annotations in the following files
        assets.load(AssetPath.Spritesheet::class.java)
        assets.load(AssetPath.Textures::class.java)

        // Finish loading before proceeding
        assets.finishLoading()

        // Load Background Assets
        backgrounds.add("grass")
        backgrounds.add("cave")
        backgrounds.add("forest")

        // Load Person Sprites
        for(i in 1..9) { maleSprites.add("spritesheets/person${i}m.pack") }
        for(i in 1..2) { femaleSprites.add("spritesheets/person${i}f.pack") }

        maleSprites.forEach { s -> assets.load(s, TextureAtlas::class.java) }
        femaleSprites.forEach { s -> assets.load(s, TextureAtlas::class.java) }


        // Animated Tiles
        animatedTiles = Array()

        assets.finishLoading()

        val animations = AssetPath.Spritesheet.ANIMATIONS
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("water"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterine"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterinw"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterise"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterisw"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("watern"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterne"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waternw"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waters"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterse"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("watersw"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("watere"), Animation.PlayMode.LOOP))
        animatedTiles.add(Animation(1f, assets.get(animations, TextureAtlas::class.java)
                .findRegions("waterw"), Animation.PlayMode.LOOP))
    }


    // .......................................................................... Textures & Sprites
    override fun getTexture(path: String): Texture = assets.get(path)

    override fun getMonsterSprite(index: Int, form: Int): TextureRegion
    {
        val monsterSprites = assets.get(AssetPath.Spritesheet.GUARDIANS, TextureAtlas::class.java)
        val sprite: AtlasRegion? = monsterSprites.findRegion(index.toString(), form)

        return sprite ?: monsterSprites.findRegion("0")
    }

    override fun getMonsterMiniSprite(index: Int, form: Int): TextureRegion
    {
        val monsterSprites = assets.get(AssetPath.Spritesheet.GUARDIANS_MINI, TextureAtlas::class.java)
        val sprite: AtlasRegion? = monsterSprites.findRegion(Integer.toString(index), form)

        return sprite ?: monsterSprites.findRegion("0")
    }

    override fun getItemDrawable(nameID: String): Drawable
    {
        val itemAtlas = assets.get(AssetPath.Spritesheet.ITEMS, TextureAtlas::class.java)
        val sprite = itemAtlas.findRegion(nameID)

        return TextureRegionDrawable(sprite)
    }

    override fun getMonsterFace(id: Int, form: Int): Image
    {
        val region = assets.get(AssetPath.Spritesheet.GUARDIANS_PREVIEW, TextureAtlas::class.java)
                .findRegion(Integer.toString(id), form)
        val faceImg = Image(region)
        faceImg.setSize(24f, 23f)
        return faceImg
    }

    override fun getBackgroundTexture(index: Int): TextureRegion
    {
        return assets.get("spritesheets/battleBacks.pack", TextureAtlas::class.java).findRegion(backgrounds.get(index))
    }

    override fun getMetamorphosisBackground(): Image
    {
        val atlas = getTextureAtlas(AssetPath.Spritesheet.BATTLE_BG)
        return Image(atlas.findRegion("metamorph_bg"))
    }


    // ....................................................................................... Atlas
    override fun getTextureAtlas(path: String): TextureAtlas
    {
        return assets.get(path, TextureAtlas::class.java)
    }

    override fun getTextureAtlasType(type: TextureAtlasType): TextureAtlas
    {
        return when (type)
        {
            TextureAtlasType.HERO -> assets.get<TextureAtlas>(AssetPath.Spritesheet.HERO)
            else ->
            {
                println("Error: TextureAtlasType $type not found.")
                return TextureAtlas()
            }
        }
    }

    override fun getPersonTextureAtlas(male: Boolean, index: Int): TextureAtlas
    {
        return when(male)
        {
            true  -> assets.get(maleSprites.get(index), TextureAtlas::class.java)
            false -> assets.get(femaleSprites.get(index), TextureAtlas::class.java)
        }
    }


    // .................................................................................. Animations
    override fun getMetamorphosisAnimation(): AnimatedImage
    {
        val atlas = getTextureAtlas(AssetPath.Spritesheet.ANIMATIONS_BIG)
        val animation: Animation<TextureRegion> = Animation(.15f, atlas.findRegions("metamorphosis"))
        val metamorphosisAnimation = AnimatedImage(animation)
        metamorphosisAnimation.setPlayMode(Animation.PlayMode.NORMAL)
        return metamorphosisAnimation
    }

    override fun getSummoningAnimation(): Animation<TextureRegion>
    {
        val atlas = assets.get(AssetPath.Spritesheet.ANIMATIONS_SUMMON_BAN, TextureAtlas::class.java)
        return Animation(1f / 12f, atlas.findRegions("ban-circle"), Animation.PlayMode.REVERSED)
    }

    override fun getBanningAnimation(): Animation<TextureRegion>
    {
        val anim = getSummoningAnimation()
        anim.playMode = Animation.PlayMode.NORMAL
        return anim
    }

    override fun getAttackAnimation(attack: String): Animation<TextureRegion>
    {
        val atlas = assets.get("spritesheets/battleAnimations.pack", TextureAtlas::class.java)
        return if (atlas.findRegions(attack).size == 0)
        {
            Animation(1f / 12f, atlas.findRegions("att_kick"), Animation.PlayMode.NORMAL)
        }
        else
        {
            Animation(1f / 12f, atlas.findRegions(attack), Animation.PlayMode.NORMAL)
        }
    }

    override fun getStatusEffectAnimation(statusEffect: Enum<*>): Animation<TextureRegion>
    {
        val seString = statusEffect.toString().toLowerCase()
        val atlas = assets.get("spritesheets/statusEffectAnimations.pack", TextureAtlas::class.java)

        return if (atlas.findRegions("status_effect_$seString").size == 0)
        {
            Animation(1f / 12f, atlas.findRegions("status_effect_healthy"), Animation.PlayMode.LOOP)
        }
        else
        {
            Animation(1f / 12f, atlas.findRegions("status_effect_$seString"), Animation.PlayMode.LOOP)
        }
    }


    // .............................................................................. Map Animations
    override fun getPersonAnimationSet(gender: Boolean, index: Int): ArrayMap<SkyDirection, Animation<TextureRegion>>
    {
        val textureAtlas = getPersonTextureAtlas(gender, index)
        return getPersonAnimationSet(textureAtlas)
    }

    override fun getPersonAnimationSet(name: String): ArrayMap<SkyDirection, Animation<TextureRegion>>
    {
        val textureAtlas = if (name == "hero")
        {
            getTextureAtlasType(TextureAtlasType.HERO)
        }
        else
        {
            getPersonTextureAtlas(true, 1)
        }

        return getPersonAnimationSet(textureAtlas)
    }

    override fun getPersonAnimationSet(textureAtlas: TextureAtlas): ArrayMap<SkyDirection, Animation<TextureRegion>>
    {
        val animations = ArrayMap<SkyDirection, Animation<TextureRegion>>()

        animations.put(SkyDirection.N, Animation(.15f, textureAtlas!!.findRegions("n"), Animation.PlayMode.LOOP))
        animations.put(SkyDirection.E, Animation(.15f, textureAtlas.findRegions("e"), Animation.PlayMode.LOOP))
        animations.put(SkyDirection.S, Animation(.15f, textureAtlas.findRegions("s"), Animation.PlayMode.LOOP))
        animations.put(SkyDirection.W, Animation(.15f, textureAtlas.findRegions("w"), Animation.PlayMode.LOOP))
        animations.put(SkyDirection.NSTOP, Animation(.15f, textureAtlas.findRegions("n").get(0)))
        animations.put(SkyDirection.ESTOP, Animation(.15f, textureAtlas.findRegions("e").get(0)))
        animations.put(SkyDirection.SSTOP, Animation(.15f, textureAtlas.findRegions("s").get(0)))
        animations.put(SkyDirection.WSTOP, Animation(.15f, textureAtlas.findRegions("w").get(0)))

        return animations
    }

    override fun getTileAnimation(index: Int): Animation<TextureRegion> = animatedTiles.get(index)

    override fun getObjectAnimation(id: String): Animation<TextureRegion>
    {
        val atlas = assets.get(AssetPath.Spritesheet.ANIMATIONS, TextureAtlas::class.java)
        val regions = atlas.findRegions(id)
        val anim = Animation<TextureRegion>(1f, regions)
        anim.playMode = Animation.PlayMode.LOOP
        return anim
    }


    // .................................................................................. Management
    override fun dispose() { assets.dispose() }
}
