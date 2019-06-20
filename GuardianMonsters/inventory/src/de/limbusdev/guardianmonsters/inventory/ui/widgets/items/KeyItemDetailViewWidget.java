package de.limbusdev.guardianmonsters.inventory.ui.widgets.items;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * Created by georg on 20.02.17.
 */

public class KeyItemDetailViewWidget extends ItemApplicationWidget
{

    public KeyItemDetailViewWidget(Inventory inventory, Team team) {
        super(inventory, team);

        getUse().remove();
        getDelete().remove();

    }
}
