package de.limbusdev.guardianmonsters.media

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage


/**
 * NullMedia, gets returned from Service Locator, when no Media Service has been injected yet.
 *
 * @author Georg Eckert
 */

class NullMediaManager : IMediaManager
{
    // .......................................................................... Textures & Sprites
    override fun getTexture(path: String) = Texture(100, 100, Pixmap.Format.Alpha)

    override fun getMonsterSprite(index: Int, form: Int) : TextureRegion = TextureRegion()

    override fun getMonsterMiniSprite(index: Int, form: Int) : TextureRegion = TextureRegion()

    override fun getItemDrawable(nameID: String) : Drawable = TextureRegionDrawable()

    override fun getMonsterFace(id: Int, form: Int): Image = Image()

    override fun getBackgroundTexture(index: Int): TextureRegion = TextureRegion()

    override fun getMetamorphosisBackground(): Image = Image()


    // ....................................................................................... Atlas
    override fun getTextureAtlas(path: String): TextureAtlas = TextureAtlas()

    override fun getTextureAtlasType(type: TextureAtlasType): TextureAtlas = TextureAtlas()

    override fun getPersonTextureAtlas(male: Boolean, index: Int): TextureAtlas = TextureAtlas()


    // .................................................................................. Animations
    override fun getMetamorphosisAnimation(): AnimatedImage = AnimatedImage()

    override fun getSummoningAnimation(): Animation<TextureRegion> = Animation(1f)

    override fun getBanningAnimation(): Animation<TextureRegion> = Animation(1f)

    override fun getAttackAnimation(attack: String): Animation<TextureRegion> = Animation(1f)

    override fun getStatusEffectAnimation(statusEffect: Enum<*>): Animation<TextureRegion> = Animation(1f)


    // .............................................................................. Map Animations
    override fun getPersonAnimationSet(gender: Boolean, index: Int): ArrayMap<SkyDirection, Animation<TextureRegion>>
    {
        return ArrayMap()
    }

    override fun getPersonAnimationSet(name: String): ArrayMap<SkyDirection, Animation<TextureRegion>>
    {
        return ArrayMap()
    }

    override fun getPersonAnimationSet(atlas: TextureAtlas): ArrayMap<SkyDirection, Animation<TextureRegion>>
    {
        return ArrayMap()
    }

    override fun getTileAnimation(index: Int): Animation<TextureRegion> = Animation(1f)

    override fun getObjectAnimation(id: String): Animation<TextureRegion> = Animation(1f)


    // .................................................................................. Management
    override fun dispose() {}
}
