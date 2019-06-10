/*
 * *************************************************************************************************
 * Copyright (c) 2019. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <limbusdev.games@gmail.com>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * The AnimatedImage behaves like an [Image], but it changes the displayed textures every few frames
 * and thereby creates an animation.
 *
 * @author Georg Eckert 2019
 */

class AnimatedImage
(
        // ................................................ Primary Constructor
        private var animation: Animation<TextureRegion>
)
    // .................................................... Super Constructor
    : Image(animation.getKeyFrame(0f))
{
    // .................................................................................. Properties
    private var animationTime = 0f

    // ..................................................................................... Methods
    fun setAnimation(animation: Animation<TextureRegion>)
    {
        this.animation = animation
    }

    override fun act(delta: Float)
    {
        super.act(delta)
        animationTime += delta
        val region = animation.getKeyFrame(animationTime, false)
        (drawable as TextureRegionDrawable).region = region
    }
}
