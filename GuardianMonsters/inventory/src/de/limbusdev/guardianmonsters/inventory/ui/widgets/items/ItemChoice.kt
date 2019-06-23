package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.GuardianListWidget
import de.limbusdev.guardianmonsters.scene2d.replaceOnClick
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.ItemListWidget

class ItemChoice
(
        private val inventory: Inventory,
        private val team: Team,
        private val battleSystem: BattleSystem,
        private val guardoSphere: GuardoSphere,
        val skin: Skin = Services.UI().inventorySkin
)
    : Group()
{
    // ............................................................................................. PROPERTIES
    private var guardianList    : GuardianListWidget? = null
    private var chosenItem      : Item? = null
    private val detailViewWidget: ItemApplicationWidget


    // ............................................................................................. CONSTRUCTORS
    init
    {
        setSize(Constant.WIDTHf, Constant.HEIGHTf)
        setPosition(0f, 0f, Align.bottomLeft)

        // Black transparent Overlay
        val overlay = Image(skin, "black-a80")
        overlay.setSize(Constant.WIDTHf, Constant.HEIGHTf)
        overlay.setPosition(0f, 0f, Align.bottomLeft)
        addActor(overlay)

        // Top Bar
        val topBar = Image(skin, "toolBar-bg")
        topBar.setSize(Constant.WIDTHf, 38f)
        topBar.setPosition(0f, Constant.HEIGHTf, Align.topLeft)
        addActor(topBar)

        // Top Bar Label
        val label = Label(Services.I18N().Inventory("in_battle_item"), skin, "burgund")
        label.setSize(Constant.WIDTHf/3, 32f)
        label.setPosition(Constant.WIDTHf/2, Constant.HEIGHTf - 18, Align.center)
        label.setAlignment(Align.center, Align.center)
        addActor(label)

        // Detail Widget
        detailViewWidget = ItemApplicationWidget(inventory, team)
        detailViewWidget.setPosition(20f, 2f, Align.bottomLeft)
        detailViewWidget.delete.isVisible = false

        setUp()

        val back = ImageButton(skin, "button-back")
        back.setPosition(Constant.WIDTHf - 20, 2f, Align.bottomRight)
        addActor(back)
        back.replaceOnClick { remove() }
    }


    // ............................................................................................. INITIALIZATION
    private fun setUp()
    {
        // What happens, when a Guardian is chosen from the list?
        val onButton: (Int) -> Boolean = { i ->

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
        val onItemButton = ItemListWidget.ClickListener { item ->

            chosenItem = item
            var hint = ""
            var applicable = true

            when(item)
            {
                is ChakraCrystalItem ->
                {
                    applicable = !guardoSphere.isFull()
                    if(!applicable) {hint = Services.I18N().Inventory("sphere_full") }
                }
                else -> {}
            }

            detailViewWidget.initialize(item, applicable, hint)
            addActor(detailViewWidget)

            val onUseButton: () -> Unit
            when(item)
            {
                is AMedicalItem -> onUseButton = {

                    if (guardianList != null) { guardianList!!.remove() }
                    guardianList = GuardianListWidget(team, onButton, chosenItem!!)
                    addActor(guardianList)
                    detailViewWidget.remove()
                }
                is ChakraCrystalItem -> onUseButton = {

                    if (guardianList != null) { guardianList!!.remove() }
                    detailViewWidget.remove()
                    remove()
                    inventory.takeFromInventory(chosenItem!!)
                    battleSystem.banWildGuardian(item)
                }
                else -> onUseButton = {}
            }

            detailViewWidget.use.replaceOnClick{ onUseButton.invoke() }
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
        val itemList = ItemListWidget(inventory, onItemButton, filters)
        itemList.setSize(140f, Constant.HEIGHTf)
        itemList.setPosition(Constant.WIDTHf / 2 - 32, 0f, Align.bottomLeft)
        addActor(itemList)
    }
}
