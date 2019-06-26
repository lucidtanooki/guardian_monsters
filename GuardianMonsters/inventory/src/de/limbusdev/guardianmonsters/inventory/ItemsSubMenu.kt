package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemApplicationWidget
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemCategoryToolbar
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.KeyItemDetailViewWidget
import de.limbusdev.guardianmonsters.inventory.ui.widgets.items.WeaponDetailViewWidget
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.ItemListWidget

/**
 * ItemsSubMenu allows the user to view, choose, apply and organize
 * [Item]s, [Equipment] and [KeyItem]s.
 *
 * @author Georg Eckert 2017-02-17
 */

class ItemsSubMenu
(
        skin: Skin,
        private val inventory: Inventory,
        private val team: Team
)
    : AInventorySubMenu(), ItemCategoryToolbar.ClickListener, ItemListWidget.ClickListener
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private var detailView      : ItemApplicationWidget = ItemApplicationWidget(inventory, team)
    private val itemListWidget  : ItemListWidget
    private val toolbar = ItemCategoryToolbar(skin, this)


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        // .......................................... scrollable list
        // initialize with medical items
        val filters = Array<Item.Category>()
        filters.add(Item.Category.MEDICINE)

        itemListWidget = ItemListWidget(inventory, this, filters)
        itemListWidget.setPosition(68f, 0f, Align.bottomLeft)

        // Add widgets
        addActor(toolbar)
        addActor(itemListWidget)
    }


    // --------------------------------------------------------------------------------------------- METHODS
    private fun showItemDetailView(item: Item)
    {
        // Remove currently displayed ItemDetailView
        detailView.remove()

        detailView = when (item.category)
        {
            Item.Category.EQUIPMENT -> WeaponDetailViewWidget(inventory, team)
            Item.Category.KEY       -> KeyItemDetailViewWidget(inventory, team)
            else                    -> ItemApplicationWidget(inventory, team)
        }

        detailView.initialize(item)

        detailView.setPosition(264f, 2f, Align.bottomLeft)
        addActor(detailView)
    }

    // .............................................................................. CLICK LISTENER

    override fun onMedicineButton()
    {
        itemListWidget.applyFilter(Item.Category.MEDICINE)
    }

    override fun onOtherItemsButton()
    {
        itemListWidget.applyFilter(Item.Category.ALL)
    }

    override fun onEquipItemsButton()
    {
        itemListWidget.applyFilter(Item.Category.EQUIPMENT)
    }

    override fun onKeyItemsButton()
    {
        itemListWidget.applyFilter(Item.Category.KEY)
    }

    override fun onChoosingItem(item: Item)
    {
        showItemDetailView(item)
    }

    override fun refresh()
    {
        // Do nothing
    }

    override fun layout(skin: Skin) {}
}
