package de.limbusdev.guardianmonsters.inventory.ui.widgets.team

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.arrayMapOf
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.style.get

/**
 * @author Georg Eckert 2017
 */

class GuardianOverviewButton
(
        private val guardian: AGuardian,
        private val item: Item,
        skin: Skin,
        styleName: String
)
    : Button(), Listener<Guardian>
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val guardianNameLabel : Label
    private val guardianSprite : Image

    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        val buttonStyle = skin.get<TextButton.TextButtonStyle>(styleName)
        style = buttonStyle
        this.skin = skin

        val guardianName = Services.I18N().getLocalizedGuardianName(guardian)
        guardianNameLabel = Label(guardianName, Label.LabelStyle(buttonStyle.font, buttonStyle.fontColor))
        val region = Services.Media().getMonsterMiniSprite(guardian.speciesID, guardian.currentForm)
        guardianSprite = Image(region)

        augmentButton(guardian, item)
    }

    /** Creates the first line in the button, with Guardian name and mini sprite. */
    private fun assembleButtonHead()
    {
        pad(4f)
        val line = Table()
        line.add(guardianNameLabel).align(Align.left).expand().fill()
        line.add(guardianSprite)
        add(line).expand().fill()
        row()
    }


    // --------------------------------------------------------------------------------------------- METHODS
    override fun setColor(r: Float, g: Float, b: Float, a: Float)
    {
        color = Color(r, g, b, a)
    }

    override fun setColor(color: Color)
    {
        super.setColor(color)
        children.forEach { it.color = color }
    }

    private fun augmentButton(guardian: AGuardian, item: Item)
    {
        when (item.category)
        {
            Item.Category.EQUIPMENT -> augmentButtonEquipment(guardian, item as Equipment)
            else                    -> augmentButtonMedicine(guardian, item)
        }
    }

    private fun augmentButtonEquipment(guardian: AGuardian, equipment: Equipment)
    {
        clear()
        assembleButtonHead()

        // Put this into a new table
        val line = Table()

        val potential = guardian.stats.getEquipmentPotential(equipment)

        val properties = arrayMapOf(
                "hp"    to potential.hp,
                "mp"    to potential.mp,
                "speed" to potential.speed,
                "exp"   to potential.exp,
                "pstr"  to potential.pstr,
                "pdef"  to potential.pdef,
                "mstr"  to potential.mstr,
                "mdef"  to potential.mdef
        )

        var fontStyle: String
        var sign     : String
        var value    : String

        for((counter, entry) in properties.withIndex())
        {
            val statSymbol = makeImage(skin["stats-symbol-${entry.key}"])
            line.add(statSymbol).size(16f).padRight(2f)

            fontStyle = when
            {
                entry.value > 0 -> "green"
                entry.value < 0 -> "red"
                else            -> "default"
            }

            sign = if (entry.value > 0) "+" else ""

            line.add(Label("$sign${entry.value}", skin, fontStyle)).width(32f)

            if (counter == 3) { line.row() }
        }

        layout()

        if (!equipment.canBeEquipped(guardian))
        {
            touchable = Touchable.disabled
            setColor(.6f, .6f, .6f, 1f)
        }

        add(line).expand().fill()
    }

    private fun augmentButtonMedicine(guardian: AGuardian, item: Item)
    {
        clear()

        assembleButtonHead()

        // Put this into a new table
        val line = Table()

        line.add(makeImage(skin["stats-symbol-hp"])).padRight(2f)
        line.add(Label(guardian.stats.hpFractionToString, skin, "default")).width(56f).padRight(8f)
        line.add(makeImage(skin["stats-symbol-mp"])).padRight(2f)
        line.add(Label(guardian.stats.mpFractionToString, skin, "default")).width(56f)

        layout()

        if (item is AMedicalItem)
        {
            if (!item.applicable(guardian))
            {
                touchable = Touchable.disabled
                setColor(.6f, .6f, .6f, 1f)
            }
        }

        add(line).expand().fill()
    }

    override fun receive(signal: Signal<Guardian>, guardian: Guardian)
    {
        augmentButton(guardian, item)
    }
}
