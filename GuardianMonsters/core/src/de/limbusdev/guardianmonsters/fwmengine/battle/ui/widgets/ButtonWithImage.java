package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by georg on 11.12.16.
 */

public class ButtonWithImage extends TextButton {

    private Image childImage;

    public ButtonWithImage(String text, Skin skin) {
        super(text, skin);
    }

    public ButtonWithImage(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public ButtonWithImage(String text, TextButtonStyle style) {
        super(text, style);
    }

    private void setUp() {
        childImage = new Image();
        childImage.setAlign(Align.left);
        childImage.setSize(16,16);
        childImage.setPosition(16,16,Align.center);
        addActor(childImage);
    }

    public void setChildImage(Drawable drawable) {
        setUp();
        this.childImage.setDrawable(drawable);
    }

    @Override
    public void setSize(float width, float height) {
        if(childImage == null) childImage = new Image();
        childImage.setSize(width/getWidth()*childImage.getWidth(),width/getWidth()*childImage.getHeight());
        childImage.setPosition(childImage.getX()*width/getWidth(),childImage.getY()*height/getHeight());
        super.setSize(width, height);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        childImage.setScale(scaleXY);
    }
}
