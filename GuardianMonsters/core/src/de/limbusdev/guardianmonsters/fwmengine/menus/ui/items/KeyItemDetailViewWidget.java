package de.limbusdev.guardianmonsters.fwmengine.menus.ui.items;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * Created by georg on 20.02.17.
 */

public class KeyItemDetailViewWidget extends ItemDetailViewWidget {

    public KeyItemDetailViewWidget(Skin skin, Inventory inventory, ArrayMap<Integer, Guardian> team) {
        super(skin, inventory, team);

        getUse().remove();
        getDelete().remove();

    }
}
