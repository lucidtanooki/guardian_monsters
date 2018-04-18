package main.java.de.limbusdev.guardianmonsters.inventory;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.ui.Constant;


/**
 * @author Georg Eckert 2017
 */

public abstract class AInventorySubMenu extends Group {
    private Skin skin;
    public static final int TOOLBAR_HEIGHT = 36;

    public AInventorySubMenu(Skin skin) {
        super();
        this.skin = skin;
        setDebug(Constant.DEBUGGING_ON, true);
        setSize(Constant.WIDTH, Constant.HEIGHT-TOOLBAR_HEIGHT);
        setPosition(0,0, Align.bottomLeft);
    }

    public Skin getSkin() {
        return skin;
    }

    public abstract void refresh();
}
