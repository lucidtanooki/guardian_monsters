package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.Equipment;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Created by georg on 20.02.17.
 */

public class KeyItemDetailViewWidget extends ItemDetailViewWidget {

    public KeyItemDetailViewWidget(Skin skin, Inventory inventory, ArrayMap<Integer, Monster> team) {
        super(skin, inventory, team);

        getUse().remove();
        getDelete().remove();

    }
}
