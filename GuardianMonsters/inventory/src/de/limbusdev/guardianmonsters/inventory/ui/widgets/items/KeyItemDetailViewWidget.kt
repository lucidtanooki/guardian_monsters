package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.monsters.Team

/**
 * KeyItemDetailViewWidget displays information about key items. It has no use or delete buttons,
 * since key items are not usable and must not be thrown away.
 *
 * @author Georg Eckert 2018
 */

class KeyItemDetailViewWidget
(
        inventory: Inventory,
        team: Team
)
    : ItemApplicationWidget(inventory, team)
{
    init
    {
        // Key items cannot be "used" or "deleted", for that reason the buttons will be removed
        use.remove()
        delete.remove()
    }
}
