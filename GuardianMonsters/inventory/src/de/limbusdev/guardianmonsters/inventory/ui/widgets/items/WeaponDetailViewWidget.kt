package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.items.Inventory
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.set
import ktx.actors.txt
import ktx.style.get

/**
 * WeaponDetailViewWidget displays information about [Equipment] items.
 *
 * @author Georg Eckert 2017
 */

class WeaponDetailViewWidget
(
        inventory: Inventory,
        team: Team
)
    : ItemApplicationWidget(inventory, team)
{
    // ............................................................................................. PROPERTIES
    private val valueLabels: ArrayMap<String, Label>


    // ............................................................................................. CONSTRUCTOR
    init
    {
        val skin = Services.getUI().inventorySkin

        var offX = 4f
        val offY = 74f
        val gap = 18f

        valueLabels = ArrayMap()
        var value: Label
        val labels = arrayOf("hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed")
        var key: Image
        var reset = 0

        for (i in labels.indices)
        {
            if (i > 0 && i % 3 == 0)
            {
                offX += 56
                reset = i
            }

            key = Image(skin.get<Drawable>("stats-symbol-${labels[i]}"))
            key.setSize(16f, 16f)
            key.setPosition(offX, offY - gap * (i + 1 - reset), Align.topLeft)
            addActor(key)

            value = Label("-", skin, "default")
            value.setPosition(offX + 17, offY - gap * (i + 1 - reset), Align.topLeft)
            addActor(value)
            valueLabels[labels[i]] = value
        }
    }


    // ............................................................................................. INITIALIZATION
    override fun initialize(itemToShow: Item)
    {
        super.initialize(itemToShow)

        check(itemToShow is Equipment)

        val values = mapOf(

                "hp"    to itemToShow.addsHP,
                "mp"    to itemToShow.addsMP,
                "pstr"  to itemToShow.addsPStr,
                "pdef"  to itemToShow.addsPDef,
                "mstr"  to itemToShow.addsMStr,
                "mdef"  to itemToShow.addsMDef,
                "speed" to itemToShow.addsSpeed
        )

        for(key in values.keys)
        {
            val value = values[key]
            if(values[key] != 0)
            {
                valueLabels[key].txt = "${prefix(value!!)}$value${suffix(key)}"
            }
        }
    }

    private fun prefix(value: Int)  = if(value >= 0) "+" else "-"
    private fun suffix(key: String) = when(key) { "hp", "mp" -> "%"; else -> "" }
}
