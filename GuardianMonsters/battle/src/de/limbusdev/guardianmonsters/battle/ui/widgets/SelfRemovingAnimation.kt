package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * @author Georg Eckert 2016
 */
class SelfRemovingAnimation
(
        private val animation: Animation<TextureRegion>
)
    : Image(animation.getKeyFrame(0f))
{
    private var animationTime = 0f

    override fun act(delta: Float)
    {
        super.act(delta)
        animationTime += delta
        val region = animation.getKeyFrame(animationTime, false)
        (drawable as TextureRegionDrawable).region = region

        if (animation.isAnimationFinished(animationTime))
        {
            remove()
        }
    }
}
