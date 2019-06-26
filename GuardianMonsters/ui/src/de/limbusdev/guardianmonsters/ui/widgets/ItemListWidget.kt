package de.limbusdev.guardianmonsters.ui.widgets

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.ItemSignal
import de.limbusdev.guardianmonsters.scene2d.Scene2DLayout
import de.limbusdev.guardianmonsters.scene2d.makeScrollPane
import de.limbusdev.guardianmonsters.scene2d.replaceOnButtonClick
import de.limbusdev.guardianmonsters.services.Services

/**
 * ItemListWidget displays a list of several items.
 *
 * @author Georg Eckert 2017
 */

class ItemListWidget
(
        private var inventory: Inventory,
        private val onItemButton: (Item) -> Unit,
        private var currentFilters: Array<Item.Category>,
        private val skin: Skin = Services.UI().inventorySkin
)
    : Group(), Listener<ItemSignal>
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val itemTable = Table()
    private var lastChosenItem = 0


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        itemTable.align(Align.topLeft)

        makeScrollPane(itemTable, skin, Scene2DLayout(192f, 200f, 2f, 2f), false, true, this)
    }

    fun initialize(inventory: Inventory, filters: Array<Item.Category>)
    {
        itemTable.clearChildren()
        this.inventory = inventory
        inventory.add(this)

        val buttons = Array<Button>()
        val buttonGroup = ButtonGroup<TextButton>()
        buttonGroup.setMinCheckCount(1)
        buttonGroup.setMaxCheckCount(1)

        // Only proceed if there are any items
        if (inventory.items.size > 0)
        {
            val filteredItems = ArrayMap<Int, Item>()
            var counter = 0
            for (item in inventory.items.keys())
            {
                if (filters.contains(Item.Category.ALL, false) || filters.contains(item.category, false))
                {
                    val itemButton = ItemInventoryButton(item, skin, "item-button-sandstone", inventory)

                    itemTable.add(itemButton).width(192f).height(40f)
                    buttons.add(itemButton)
                    buttonGroup.add(itemButton)
                    itemTable.row().spaceBottom(1f)

                    itemButton.replaceOnButtonClick {

                        onItemButton(item)
                        lastChosenItem = buttonGroup.checkedIndex
                    }

                    if (counter == 0) {
                        itemButton.isChecked = true
                        onItemButton(item)
                        inventory.removeAllListeners()
                        inventory.add(this)
                    }
                    filteredItems.put(counter, item)
                    counter++
                }
            }

            // set to item next to that one, which got deleted
            if (buttonGroup.buttons.size > 0) {

                if (lastChosenItem > buttonGroup.buttons.size - 1) {
                    lastChosenItem = buttonGroup.buttons.size - 1
                }

                buttonGroup.setChecked(buttonGroup.buttons.get(lastChosenItem).text.toString())
                onItemButton(filteredItems.get(lastChosenItem))
            }

        }
    }


    // --------------------------------------------------------------------------------------------- METHODS
    fun applyFilter(filter: Item.Category) {
        val filters = Array<Item.Category>()
        filters.add(filter)
        applyFilter(filters)
    }

    fun applyFilter(filters: Array<Item.Category>) {

        lastChosenItem = 0
        currentFilters = filters
        initialize(inventory, filters)
    }

    override fun receive(signal: Signal<ItemSignal>, itemSignal: ItemSignal) {
        initialize(inventory, currentFilters)
    }
}
