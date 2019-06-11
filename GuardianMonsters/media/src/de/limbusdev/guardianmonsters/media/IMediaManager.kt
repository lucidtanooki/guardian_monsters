package de.limbusdev.guardianmonsters.media

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage


/**
 * IMediaManager defines the API of the Asset Access Layer.
 *
 * @author Georg Eckert 2016
 */
interface IMediaManager
{
    // .......................................................................... Textures & Sprites
    fun getTexture(path: String): Texture

    fun getMonsterSprite(index: Int, form: Int): TextureRegion

    fun getMonsterMiniSprite(index: Int, form: Int): TextureRegion

    fun getItemDrawable(nameID: String): Drawable

    fun getMonsterFace(id: Int, form: Int): Image

    fun getBackgroundTexture(index: Int): TextureRegion

    /** Returns the background image for the metamorphosis animation. */
    fun getMetamorphosisBackground(): Image


    // ....................................................................................... Atlas
    fun getTextureAtlas(path: String): TextureAtlas

    fun getTextureAtlasType(type: TextureAtlasType): TextureAtlas

    /**
     * Texture Atlas for a person
     * @param male  true=male, false=female
     * @param index
     * @return
     */
    fun getPersonTextureAtlas(male: Boolean, index: Int): TextureAtlas


    // .................................................................................. Animations
    /** Returns the metamorphosis animation. */
    fun getMetamorphosisAnimation(): AnimatedImage

    /** Returns a summoning animation. */
    fun getSummoningAnimation(): Animation<TextureRegion>

    /** Returns a banning animation. */
    fun getBanningAnimation(): Animation<TextureRegion>

    fun getAttackAnimation(attack: String): Animation<TextureRegion>

    /**
     * Always use lower case names for status effect assets.
     * @param statusEffect
     * @return
     */
    fun getStatusEffectAnimation(statusEffect: Enum<*>): Animation<TextureRegion>


    // .............................................................................. Map Animations
    fun getPersonAnimationSet(gender: Boolean, index: Int): ArrayMap<SkyDirection, Animation<TextureRegion>>

    fun getPersonAnimationSet(name: String): ArrayMap<SkyDirection, Animation<TextureRegion>>

    fun getPersonAnimationSet(atlas: TextureAtlas): ArrayMap<SkyDirection, Animation<TextureRegion>>

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
    fun getTileAnimation(index: Int): Animation<TextureRegion>

    fun getObjectAnimation(id: String): Animation<TextureRegion>


    // .................................................................................. Management
    fun dispose()
}
