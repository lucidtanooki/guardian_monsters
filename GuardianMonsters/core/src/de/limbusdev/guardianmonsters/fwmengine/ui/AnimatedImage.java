package de.limbusdev.guardianmonsters.fwmengine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

/**
 * Source: http://pimentoso.blogspot.de/2013/01/animated-actor-with-scene2d-libgdx.html
 * Animated scene2d actor. Similar to {@link Image} but uses {@link Animation} instead of a {@link Drawable}.
 * @author Pimentoso
 */
public class AnimatedImage extends Widget {

    private Scaling scaling;
    private int align = Align.center;
    private float imageX, imageY, imageWidth, imageHeight;

    private Animation<TextureRegion> animation;
    private TextureRegion region;
    public int state;
    public float stateTime;

    public AnimatedImage() {
        this((Animation<TextureRegion>) null);
    }

    public AnimatedImage(Animation<TextureRegion> animation) {
        this(animation, Scaling.stretch, Align.center);
    }

    public AnimatedImage(Animation<TextureRegion> animation, Scaling scaling, int align) {
        setAnimation(animation);
        this.scaling = scaling;
        this.align = align;
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        float x = getX();
        float y = getY();
        float scaleX = getScaleX();
        float scaleY = getScaleY();

        if (animation != null) {
            region = animation.getKeyFrame(stateTime);
            float rotation = getRotation();
            if (scaleX == 1 && scaleY == 1 && rotation == 0)
                batch.draw(region, x + imageX, y + imageY, imageWidth, imageHeight);
            else {
                batch.draw(region, x + imageX, y + imageY, getOriginX() - imageX, getOriginY() - imageY, imageWidth, imageHeight,
                    scaleX, scaleY, rotation);
            }
        }
    }

    @Override
    public void layout() {
        float regionWidth, regionHeight;
        if (animation != null) {
            regionWidth = animation.getKeyFrame(0).getRegionWidth();
            regionHeight = animation.getKeyFrame(0).getRegionHeight();
        } else
            return;

        float width = getWidth();
        float height = getHeight();

        Vector2 size = scaling.apply(regionWidth, regionHeight, width, height);
        imageWidth = size.x;
        imageHeight = size.y;

        if ((align & Align.left) != 0)
            imageX = 0;
        else if ((align & Align.right) != 0)
            imageX = width-imageWidth;
        else
            imageX = (width/2)-(imageWidth/2);

        if ((align & Align.top) != 0)
            imageY = height-imageHeight;
        else if ((align & Align.bottom) != 0)
            imageY = 0;
        else
            imageY = (height/2)-(imageHeight/2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if(animation.isAnimationFinished(stateTime) && animation.getPlayMode() == Animation.PlayMode.NORMAL) {
            remove();
            stateTime = 0;
        }
    }

    public void setState(int state) {
        this.state = state;
        stateTime = 0.0f;
    }

    public void setPlayMode (Animation.PlayMode playMode) {
        animation.setPlayMode(playMode);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        if (animation != null) {
            if (this.animation == animation) return;
            invalidateHierarchy();
        } else {
            if (getPrefWidth() != 0 || getPrefHeight() != 0) invalidateHierarchy();
        }
        this.animation = animation;
    }

    public void setScaling (Scaling scaling) {
        if (scaling == null) throw new IllegalArgumentException("scaling cannot be null.");
        this.scaling = scaling;
    }

    public void setAlign (int align) {
        this.align = align;
    }

    public float getMinWidth () {
        return 0;
    }

    public float getMinHeight () {
        return 0;
    }

    public float getPrefWidth () {
        if (animation != null) return animation.getKeyFrame(0).getRegionWidth();
        return 0;
    }

    public float getPrefHeight () {
        if (animation != null) return animation.getKeyFrame(0).getRegionHeight();
        return 0;
    }

    public float getImageX () {
        return imageX;
    }

    public float getImageY () {
        return imageY;
    }

    public float getImageWidth () {
        return imageWidth;
    }

    public float getImageHeight () {
        return imageHeight;
    }
}