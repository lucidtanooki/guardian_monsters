package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Georg Eckert 2016
 */
public class SelfRemovingAnimation extends Image {
    private float animationTime=0;
    private Animation<TextureRegion> animation;

    public SelfRemovingAnimation(Animation<TextureRegion> animation) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animationTime += delta;
        TextureRegion region = animation.getKeyFrame(animationTime, false);
        ((TextureRegionDrawable)getDrawable()).setRegion(region);

        if(animation.isAnimationFinished(animationTime)) {
            remove();
        }
    }
}
