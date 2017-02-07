package de.limbusdev.guardianmonsters.fwmengine.world.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;

/**
 * Created by georg on 07.02.17.
 */

public class AnimatedPersonSprite extends Sprite {

    public ArrayMap<SkyDirection,Animation> animations; // characters animations (N,S,W,E)
    public Animation                        recentAnim; // alive animation
    public boolean visible;
    /* ........................................................................... CONSTRUCTOR .. */
    public AnimatedPersonSprite(boolean male, int index) {
        super();

        // load animation textures
        animations = Services.getMedia().getPersonAnimationSet(male,index);

        construct(animations);
    }

    public AnimatedPersonSprite(String name) {
        super();

        // load animation textures
        animations = Services.getMedia().getPersonAnimationSet(name);

        construct(animations);
    }

    private void construct(ArrayMap<SkyDirection,Animation> animations) {

        this.visible = true;

        this.animations = animations;

        changeState(SkyDirection.SSTOP);

        TextureRegion keyFrame = recentAnim.getKeyFrame(0);
        super.setSize(keyFrame.getRegionWidth(), keyFrame.getRegionHeight());
        update(0);
    }


    public void changeState(SkyDirection dir) {
        recentAnim = animations.get(dir);
    }

    public void update(float elapsedTime) {
        TextureRegion keyFrame = recentAnim.getKeyFrame(elapsedTime);
        super.setRegion(keyFrame, 0, 0, keyFrame.getRegionWidth(), keyFrame.getRegionHeight());
    }

    public Animation getAnimation() {
        return recentAnim;
    }



}
