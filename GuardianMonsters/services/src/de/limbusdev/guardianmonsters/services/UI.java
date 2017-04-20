package de.limbusdev.guardianmonsters.services;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by georg on 14.11.16.
 */

public interface UI {

    BitmapFont getFont(int color);
    Skin getDefaultSkin();
    Skin getBattleSkin();
    Skin getInventorySkin();
    void dispose();

}
