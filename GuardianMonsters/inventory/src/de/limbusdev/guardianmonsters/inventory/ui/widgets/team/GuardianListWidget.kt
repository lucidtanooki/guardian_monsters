package de.limbusdev.guardianmonsters.inventory.ui.widgets.team

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.replaceOnClick

/**
 * Gives the player a quick overview over all guardians currently in team. All status values are
 * shown and guardians not applicable for the choice to be made, should be disabled
 *
 *
 * @author Georg Eckert 2017
 */

class GuardianListWidget
(
        team                : Team,
        private val onButton: (Int) -> Boolean,
        item                : Item
)
    : Group()
{
    // ............................................................................................. PROPERTIES
    private val blackLayer: Image


    // ............................................................................................. CONSTRUCTORS
    init
    {
        val skin = Services.UI().inventorySkin

        blackLayer = Image(skin.getDrawable("black-a80"))
        blackLayer.setSize(Constant.WIDTHf, Constant.HEIGHTf)
        blackLayer.setPosition(0f, 0f, Align.bottomLeft)
        addActor(blackLayer)

        val back = ImageButton(skin, "button-back")
        back.setPosition(Constant.WIDTHf - 4, 0f, Align.bottomRight)
        back.replaceOnClick { remove() }
        addActor(back)

        val guardianTable = Table()
        guardianTable.align(Align.topLeft)

        val scrollPane = ScrollPane(guardianTable, skin)

        scrollPane.setSize(192f, 236f)
        scrollPane.setPosition(0f, 0f)
        scrollPane.setScrollBarPositions(false, true)
        addActor(scrollPane)

        for (i in 0 until team.size)
        {
            val guardian = team[i]
            val guardianButton = GuardianOverviewButton(guardian, skin, "button-sandstone", item)

            guardianTable.add(guardianButton).width(192f).height(64f)
            guardianTable.row().spaceBottom(1f)

            guardianButton.replaceOnClick {

                val moreItems = onButton(i)
                if(!moreItems) { remove() }
            }
        }
    }
}
