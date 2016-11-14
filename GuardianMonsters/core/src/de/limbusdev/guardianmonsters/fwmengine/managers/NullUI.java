package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by georg on 14.11.16.
 */

public class NullUI implements UI {
    @Override
    public BitmapFont getFont(int color) {
        return new BitmapFont();
    }

    @Override
    public Skin getDefaultSkin() {
        return null;
    }

    @Override
    public Skin getBattleSkin() {
        return null;
    }

    @Override
    public Skin getInventorySkin() {
        return null;
    }


}
