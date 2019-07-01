package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.f

/**
 * @author Georg Eckert 2017-02-07
 */

class AnimatedPersonSprite : Sprite
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private lateinit var animations: ArrayMap<SkyDirection, Animation<TextureRegion>> // characters animations (N,S,W,E)
    private lateinit var currentAnimation: Animation<TextureRegion> // current animation
    var visible: Boolean = false


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    constructor(male: Boolean, index: Int) : super()
    {
        construct(Services.Media().getPersonAnimationSet(male, index))
    }

    constructor(name: String) : super()
    {
        construct(Services.Media().getPersonAnimationSet(name))
    }

    private fun construct(animationMap: ArrayMap<SkyDirection, Animation<TextureRegion>>)
    {
        visible = true

        animations = animationMap
        currentAnimation = animations.firstValue()

        changeState(SkyDirection.SSTOP)

        val keyFrame = currentAnimation.getKeyFrame(0f)
        setSize(keyFrame.regionWidth.f(), keyFrame.regionHeight.f())
        update(0f)
    }


    fun changeState(dir: SkyDirection)
    {
        currentAnimation = animations.get(dir)
    }

    fun update(elapsedTime: Float)
    {
        val keyFrame = currentAnimation.getKeyFrame(elapsedTime)
        setRegion(keyFrame, 0, 0, keyFrame.regionWidth, keyFrame.regionHeight)
    }


}
