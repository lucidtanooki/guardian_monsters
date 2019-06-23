package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.GuardianListWidget
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.ReassuranceWidget
import ktx.actors.txt

/**
 * @author Georg Eckert 2017
 */

open class ItemApplicationWidget
(
        private val inventory: Inventory,
        private val team: Team,
        private val skin: Skin = Services.UI().inventorySkin
)
    : Group()
{
    // ............................................................................................. PROPERTIES
    // UI
    private lateinit var itemName           : Label
    private lateinit var itemDescription    : Label
    private lateinit var itemHint           : Label
    private lateinit var itemArea           : Label
    private lateinit var itemImg            : Image
    private lateinit var reassuranceWidget  : ReassuranceWidget

    lateinit var delete  : ImageButton private set
    lateinit var use     : ImageButton private set

    // Data
    private var item: Item = MedicalItem()


    // ............................................................................................. CONSTRUCTOR
    init
    {
        constructLayout()

        delete.replaceOnClick { addActor(reassuranceWidget) }

        use.replaceOnClick {

            val widget = GuardianListWidget(team, { it -> onButton(it) }, item)
            widget.setup(

                    position= Position2D(-262f, 0f, Align.topLeft),
                    parent  = this
            )
        }
    }


    /**
     * Initializes this widget with an item's information.
     *
     * @param itemToShow The item I want to display information about
     * @param applicable May this item be used?
     * @param hint       What should I know, when using it?
     */
    open fun initialize(itemToShow: Item, applicable: Boolean, hint: String)
    {
        this.item = itemToShow
        val i18n = Services.I18N().Inventory()

        use.isVisible = applicable

        itemName.txt = i18n.get(item.name)
        itemDescription.txt = i18n.get("${item.name}-description")
        itemHint.txt = hint
        itemImg.drawable = Services.Media().getItemDrawable(itemToShow.name)

        reassuranceWidget.question.txt = i18n.format("reassurance-throwaway", i18n.get(item.name))
        reassuranceWidget.buttonYes.replaceOnClick { onYesButton(item) }
    }

    open fun initialize(itemToShow: Item)
    {
        initialize(itemToShow, true, "")
    }

    private fun constructLayout()
    {
        reassuranceWidget = ReassuranceWidget()
        reassuranceWidget.position = Position2D(-264f, 0f, Align.bottomLeft)

        val bgLabel = makeLabel(

                skin    = skin,
                style   = "paper",
                layout  = Layout2D(162f, 200f, 0f, 0f, Align.bottomLeft),
                parent  = this
        )

        itemArea = makeLabel(

                skin    = skin,
                style   = "paper-dark-area",
                layout  = Layout2D(40f, 40f, 61f, 156f, Align.bottomLeft),
                parent  = this
        )

        itemImg = makeImage(

                drawable= skin.getDrawable("sword-barb-steel"),
                layout  = Layout2D(32f, 32f, 65f, 160f, Align.bottomLeft),
                parent  = this
        )

        itemName = makeLabel(

                skin    = skin,
                style   = "paper-border",
                text    = "Item Name",
                layout  = Layout2D(156f, 25f, 4f, 130f, Align.bottomLeft),
                parent  = this
        )

        itemDescription = makeLabel(

                skin    = skin,
                style   = "paper-border",
                text    = "Item Description",
                layout  = LabelLayout2D(156f, 64f, 4f, 128f, Align.topLeft, Align.topLeft, true),
                parent  = this
        )

        itemHint = makeLabel(

                skin    = skin,
                style   = "red",
                text    = "Item Hint",
                layout  = LabelLayout2D(144f, 48f, 10f, 64f, Align.topLeft, Align.topLeft, true),
                parent  = this
        )

        delete = makeImageButton(

                skin    = skin,
                style   = "button-delete",
                position= Position2D(24f, 160f, Align.bottomLeft),
                parent  = this
        )

        use = makeImageButton(

                skin    = skin,
                style   = "button-use",
                position= Position2D(106f, 160f, Align.bottomLeft),
                parent  = this
        )
    }

    fun onButton(i: Int): Boolean
    {
        inventory.takeFromInventory(item)

        when(item)
        {
            is Equipment ->
            {
                val replaced = team[i].stats.giveEquipment((item as Equipment))
                if (replaced != null) { inventory.putIntoInventory(replaced) }
            }
            is AMedicalItem ->
            {
                (item as AMedicalItem).apply(team[i])
            }
            else -> {}
        }

        val empty = inventory.getAmountOf(item) <= 0
        if (empty) { remove() }
        return !empty
    }

    private fun onYesButton(item: Item)
    {
        inventory.takeFromInventory(item)
        if (inventory.items.containsKey(item)) { reassuranceWidget.remove() }
        else                                   { remove()                   }
    }
}
