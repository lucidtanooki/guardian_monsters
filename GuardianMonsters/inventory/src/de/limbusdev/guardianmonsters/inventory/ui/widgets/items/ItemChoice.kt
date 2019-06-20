package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.MonsterListWidget
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.ItemListWidget
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener

class ItemChoice
(
        private val inventory: Inventory,
        private val team: Team,
        private val battleSystem: BattleSystem
)
    : Group()
{
    // ............................................................................................. PROPERTIES
    private var guardianList: MonsterListWidget? = null
    private var chosenItem: Item? = null
    private val detailViewWidget: ItemApplicationWidget


    // ............................................................................................. CONSTRUCTORS
    init
    {
        val skin = Services.getUI().inventorySkin

        setSize(Constant.WIDTH.toFloat(), Constant.HEIGHT.toFloat())
        setPosition(0f, 0f, Align.bottomLeft)
        val overlay = Image(Services.getUI().inventorySkin.getDrawable("black-a80"))
        overlay.setSize(Constant.WIDTH.toFloat(), Constant.HEIGHT.toFloat())
        overlay.setPosition(0f, 0f, Align.bottomLeft)
        addActor(overlay)

        detailViewWidget = ItemApplicationWidget(skin, inventory, team)
        detailViewWidget.setPosition(20f, 2f, Align.bottomLeft)
        detailViewWidget.delete.isVisible = false

        setUp()

        val back = ImageButton(skin, "button-back")
        back.setPosition((Constant.WIDTH - 2).toFloat(), 2f, Align.bottomRight)
        addActor(back)
        back.addListener(SimpleClickListener { remove() })
    }


    // ............................................................................................. INITIALIZATION
    private fun setUp()
    {
        // What happens, when a Guardian is chosen from the list?
        val callbacks = MonsterListWidget.Callbacks { i ->

            when(chosenItem)
            {
                is AMedicalItem ->
                {
                    val med = chosenItem as AMedicalItem
                    inventory.takeFromInventory(med)
                    med.apply(team[i])

                    when(med.type)
                    {
                        AMedicalItem.Type.REVIVE -> battleSystem.revive(team[i])
                        else -> {}
                    }

                    remove()
                    battleSystem.doNothing()
                }
                else -> {}
            }
            false
        }

        // What happens, when an item is chosen from the list?
        val clicks = ItemListWidget.ClickListener { item ->

            chosenItem = item
            detailViewWidget.init(item)
            addActor(detailViewWidget)
            detailViewWidget.use.clearListeners()

            val clickListener = when(item)
            {
                is AMedicalItem -> SimpleClickListener {

                    if (guardianList != null) { guardianList!!.remove() }
                    guardianList = MonsterListWidget(team, callbacks, chosenItem)
                    addActor(guardianList)
                    detailViewWidget.remove()
                }
                is ChakraCrystalItem -> SimpleClickListener {

                    if (guardianList != null) { guardianList!!.remove() }
                    detailViewWidget.remove()
                    remove()
                    inventory.takeFromInventory(chosenItem!!)
                    battleSystem.banWildGuardian(item)
                }
                else -> SimpleClickListener { }
            }

            detailViewWidget.use.addListener(clickListener)
        }

        // Define which items will be shown
        val filters = Array<Item.Category>()
        filters.add(Item.Category.MEDICINE)

        // Show Chakra Crystals only if only one opponent is left and its a wild encounter
        if (battleSystem.queue.combatTeamRight.countFitMembers() == 1 && battleSystem.isWildEncounter)
        {
            filters.add(Item.Category.CHAKRACRYSTAL)
        }

        // Create filtered Item List
        val itemList = ItemListWidget(inventory, clicks, filters)
        itemList.setSize(140f, Constant.HEIGHTf)
        itemList.setPosition(Constant.WIDTHf / 2 - 32, 0f, Align.bottomLeft)
        addActor(itemList)
    }
}
