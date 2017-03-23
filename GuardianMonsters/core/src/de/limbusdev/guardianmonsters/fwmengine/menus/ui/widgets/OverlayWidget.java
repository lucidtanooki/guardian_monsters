package de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.Constant;

/**
 * OverlayWidget
 *
 * @author Georg Eckert 2017
 */

public abstract class OverlayWidget extends Group {
    private Skin skin;

    public OverlayWidget(Skin skin) {
        super();
        this.skin = skin;

        setSize(Constant.WIDTH, Constant.HEIGHT);
        setPosition(0,0, Align.bottomLeft);
        Image bg = new Image(skin.getDrawable("black-a80"));
        bg.setFillParent(true);
        addActor(bg);
    }

    public Skin getSkin() {
        return skin;
    }
}
