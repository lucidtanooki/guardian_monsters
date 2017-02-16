package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 16.02.17.
 */

public abstract class AInventorySubMenu extends Group {
    private Skin skin;

    public AInventorySubMenu(Skin skin) {
        super();
        this.skin = skin;
        setDebug(GS.DEBUGGING_ON, true);
        setSize(428,240-36);
        setPosition(0,0, Align.bottomLeft);
    }

    public Skin getSkin() {
        return skin;
    }
}
