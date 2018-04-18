package main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.items;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * Created by georg on 20.02.17.
 */

public class KeyItemDetailViewWidget extends main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemDetailViewWidget
{

    public KeyItemDetailViewWidget(Skin skin, Inventory inventory, ArrayMap<Integer, AGuardian> team) {
        super(skin, inventory, team);

        getUse().remove();
        getDelete().remove();

    }
}
