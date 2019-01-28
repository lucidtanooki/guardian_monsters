package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import ktx.actors.minus
import ktx.actors.onClick
import ktx.actors.plus
import ktx.scene2d.table

/**
 * GuardoSphereChoiceWidget
 *
 * @author Georg Eckert 2017
 */

class GuardoSphereChoiceWidget
(
        private val skin: Skin,
        private val sphere: GuardoSphere,
        private val buttonGroup: ButtonGroup<Button>
)
    : Group()
{
    private val table: Table
    private val buttons = Array<Button>()

    var callback: (Int) -> Unit

    init
    {
        callback = {println("$TAG: dummy callback")}

        // Setup Layout
        setSize(WIDTH, HEIGHT)

        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH,HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)


        table = table {

            setSize(WIDTH,HEIGHT)
        }

        // Setup Hierarchy
        this+background
        this+table

        refresh(0)
    }

    fun refresh(page: Int)
    {
        for (b in buttons) table-b

        buttonGroup.clear()
        buttons.clear()
        table.clear()

        for (key in page * 35 until (page + 1) * 35)
        {
            if (key % 7 == 0) table.row()

            val guardian = sphere[key]
            val monsterButton = GuardoSphereButton(skin, guardian)

            table.add<ImageButton>(monsterButton).width(32f).height(32f)
            buttons+monsterButton
            buttonGroup.add(monsterButton)
            monsterButton.onClick { callback.invoke(key) }
        }
    }

    companion object
    {
        private const val TAG = "GuardoSphereChoiceWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 180f
    }
}
