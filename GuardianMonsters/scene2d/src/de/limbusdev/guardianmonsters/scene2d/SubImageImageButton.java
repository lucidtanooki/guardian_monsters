package de.limbusdev.guardianmonsters.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * SubImageImageButton
 *
 * @author Georg Eckert 2017
 */

public class SubImageImageButton extends ImageButton
{
    public SubImageImageButton(Skin skin, String style, Image image)
    {
        super(skin, style);
        image.setPosition(getWidth()/2,getHeight()/2, Align.center);
        addActor(image);
    }
}