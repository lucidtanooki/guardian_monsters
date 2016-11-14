package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by georg on 14.11.16.
 */

public interface UI {

    public BitmapFont getFont(int color);

    public Skin getDefaultSkin();

    public Skin getBattleSkin();

    public Skin getInventorySkin();

}
