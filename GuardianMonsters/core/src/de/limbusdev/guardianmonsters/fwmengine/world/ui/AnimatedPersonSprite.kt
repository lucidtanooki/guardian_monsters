package de.limbusdev.guardianmonsters.fwmengine.world.ui

import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.f

/**
 * Animates walking people. For every pixel a person moves, a new sprite is drawn.
 *
 * @author Georg Eckert 2017-02-07
 */

class AnimatedPersonSprite : Sprite
{
    // TODO synchronize animation with movement by pixel

    // --------------------------------------------------------------------------------------------- PROPERTIES
    private lateinit var animations: ArrayMap<SkyDirection, Animation<TextureRegion>>   // character's animations (N,S,W,E)
    private lateinit var currentAnimation: Animation<TextureRegion>                     // current animation
    var currentFrameID: Int = 0
        private set
    val lastFrameID: Int get() = currentAnimation.keyFrames.size-1
    val currentFrame: TextureRegion get() = currentAnimation.keyFrames[currentFrameID]
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
        if(currentFrameID > currentAnimation.keyFrames.size - 1) { currentFrameID = 0 }
        updateFrame()
    }

    fun update(elapsedTime: Float)
    {
        //val keyFrame = currentAnimation.getKeyFrame(elapsedTime)
        //setRegion(keyFrame, 0, 0, keyFrame.regionWidth, keyFrame.regionHeight)
    }

    fun updateAndDrawIfVisible(elapsedTime: Float, batch: Batch)
    {
        if (visible)
        {
            update(elapsedTime)
            draw(batch)
        }
    }

    fun toNextFrame()
    {
        currentFrameID++
        if(currentFrameID > lastFrameID) { currentFrameID = 0 }
        updateFrame()
    }

    fun toPreviousFrame()
    {
        currentFrameID--
        if(currentFrameID < 0) { currentFrameID = lastFrameID }
        updateFrame()
    }

    fun resetAnimation()
    {
        currentFrameID = 0
        updateFrame()
    }

    private fun updateFrame()
    {
        val keyFrame = currentAnimation.keyFrames[currentFrameID]
        setRegion(keyFrame, 0, 0, keyFrame.regionWidth, keyFrame.regionHeight)
    }
}
