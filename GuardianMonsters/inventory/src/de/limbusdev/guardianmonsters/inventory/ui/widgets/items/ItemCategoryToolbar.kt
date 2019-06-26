package de.limbusdev.guardianmonsters.inventory.ui.widgets.items

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.scene2d.PositionXYA
import de.limbusdev.guardianmonsters.scene2d.makeImageButton
import ktx.style.get

/**
 * @author Georg Eckert 2017
 */

class ItemCategoryToolbar
(
        skin: Skin,
        private val callbacks: ClickListener
)
    : Group()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private val buttonGroup: ButtonGroup<ImageButton>


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        setSize(82f, 204f)

        val offX = 2f
        val offY = 204f - 2f
        val gap = 36f

        // ............................................................. Medical Items Filter Button
        val medicalItemsFilterButton = makeImageButton(

                style       = skin["b-toolbar-side-medicine"],
                position    = PositionXYA(offX, offY, Align.topLeft),
                parent      = this,
                callback    = { callbacks.onMedicineButton() }
        )
        medicalItemsFilterButton.isChecked = true

        // ............................................................... Other Items Filter Button
        val allItemsFilterButton = makeImageButton(

                style       = skin["b-toolbar-side-other"],
                position    = PositionXYA(offX, offY - gap*1, Align.topLeft),
                parent      = this,
                callback    = { callbacks.onOtherItemsButton() }
        )

        // ........................................................... Equipment Items Filter Button
        val equipmentItemsFilterButton = makeImageButton(

                style       = skin["b-toolbar-side-equip"],
                position    = PositionXYA(offX, offY - gap*2, Align.topLeft),
                parent      = this,
                callback    = { callbacks.onEquipItemsButton() }
        )

        // ................................................................. Key Items Filter Button
        val keyItemsFilterButton = makeImageButton(

                style       = skin["b-toolbar-side-key"],
                position    = PositionXYA(offX, offY - gap*3, Align.topLeft),
                parent      = this,
                callback    = { callbacks.onKeyItemsButton() }
        )

        // Group Buttons together
        buttonGroup = ButtonGroup(
                medicalItemsFilterButton,
                allItemsFilterButton,
                equipmentItemsFilterButton,
                keyItemsFilterButton
        )

        // At all times exactly one button must be checked
        buttonGroup.setMaxCheckCount(1)
        buttonGroup.setMinCheckCount(1)
    }

    interface ClickListener
    {
        fun onMedicineButton()
        fun onOtherItemsButton()
        fun onEquipItemsButton()
        fun onKeyItemsButton()
    }

}
