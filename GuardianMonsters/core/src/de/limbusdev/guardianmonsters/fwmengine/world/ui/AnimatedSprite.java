package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by georg on 31.01.17.
 */

public class AnimatedSprite extends Sprite {
    private Animation anim;
    public AnimatedSprite(Animation animation) {
        super(animation.getKeyFrame(0));
        this.anim = animation;
    }

    public void update(float elapsedTime) {
        this.setRegion(anim.getKeyFrame(elapsedTime));
    }
}
