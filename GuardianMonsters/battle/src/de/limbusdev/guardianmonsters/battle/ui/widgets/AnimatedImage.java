/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * AnimationActor
 *
 * @author Georg Eckert 2018
 */

public class AnimatedImage extends Image
{
    private float animationTime=0;
    private Animation<TextureRegion> animation;

    public AnimatedImage(Animation<TextureRegion> animation)
    {
        super(animation.getKeyFrame(0));
        this.animation = animation;
    }

    public void setAnimation(Animation<TextureRegion> animation)
    {
        this.animation = animation;
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        animationTime += delta;
        TextureRegion region = animation.getKeyFrame(animationTime, false);
        ((TextureRegionDrawable)getDrawable()).setRegion(region);
    }
}
