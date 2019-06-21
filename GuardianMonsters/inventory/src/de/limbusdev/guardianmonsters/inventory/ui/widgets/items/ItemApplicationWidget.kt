package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.GuardianListWidget
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.ReassuranceWidget
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener
import de.limbusdev.utils.extensions.replaceOnClick
import ktx.actors.txt

/**
 * @author Georg Eckert 2017
 */

open class ItemApplicationWidget
(
        private val inventory: Inventory,
        private val team: Team
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

            val monsterListWidget = GuardianListWidget(team, { it -> onButton(it) }, item)
            monsterListWidget.setPosition(-262f, 0f, Align.topLeft)
            addActor(monsterListWidget)
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

        itemName.setText(i18n.get(item.name))
        itemDescription.txt = i18n.get("${item.name}-description")
        itemHint.txt = hint
        itemImg.drawable = Services.Media().getItemDrawable(itemToShow.name)

        reassuranceWidget.question.setText(i18n.format("reassurance-throwaway", i18n.get(item.name)))
        reassuranceWidget.buttonYes.addListener(SimpleClickListener {

            inventory.takeFromInventory(item)
            if (inventory.items.containsKey(item)) { reassuranceWidget.remove() }
            else                                   { remove()                   }
        })
    }

    open fun initialize(itemToShow: Item)
    {
        initialize(itemToShow, true, "")
    }

    private fun constructLayout()
    {
        val skin = Services.UI().inventorySkin

        reassuranceWidget = ReassuranceWidget(skin)
        reassuranceWidget.setPosition(-264f, 0f, Align.bottomLeft)

        val bgLabel = Label("", skin, "paper")
        bgLabel.setSize(162f, 200f)
        bgLabel.setPosition(0f, 0f, Align.bottomLeft)
        addActor(bgLabel)

        itemArea = Label("", skin, "paper-dark-area")
        itemArea.setSize(40f, 40f)
        itemArea.setPosition(61f, 156f, Align.bottomLeft)
        addActor(itemArea)

        itemImg = Image(skin.getDrawable("sword-barb-steel"))
        itemImg.setSize(32f, 32f)
        itemImg.setPosition(65f, 160f, Align.bottomLeft)
        addActor(itemImg)

        itemName = Label("Item Name", skin, "paper-border")
        itemName.setSize(156f, 25f)
        itemName.setPosition(4f, 130f, Align.bottomLeft)
        addActor(itemName)

        itemDescription = Label("Item Description", skin, "paper-border")
        itemDescription.setSize(156f, 64f)
        itemDescription.setPosition(4f, 128f, Align.topLeft)
        itemDescription.setWrap(true)
        itemDescription.setAlignment(Align.topLeft)
        addActor(itemDescription)

        itemHint = Label("Item Hint", skin, "red")
        itemHint.setSize(144f, 48f)
        itemHint.setPosition(10f, 64f, Align.topLeft)
        itemHint.setWrap(true)
        itemHint.setAlignment(Align.topLeft)
        addActor(itemHint)

        delete = ImageButton(skin, "button-delete")
        delete.setPosition(24f, 160f, Align.bottomLeft)
        addActor(delete)

        use = ImageButton(skin, "button-use")
        use.setPosition(106f, 160f, Align.bottomLeft)
        addActor(use)
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
}
