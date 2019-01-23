package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener

/**
 * GuardoSphereChoiceWidget
 *
 * @author Georg Eckert 2017
 */

class GuardoSphereChoiceWidget(
        private val skin: Skin,
        private val sphere: GuardoSphere,
        private val buttonGroup: ButtonGroup<Button>) : Group()
{
    private val table: Table
    private val buttons: Array<Button>
    private var callbacks: Callback.SingleInt? = null

    companion object
    {
        private const val WIDTH = 252f
        private const val HEIGHT = 180f
    }

    init
    {
        val runnable = Runnable { println("") }

        callbacks = Callback.SingleInt{println("GuardoSphereChoiceWidget: Dummy Callback")}

        buttons = Array()

        setSize(WIDTH, HEIGHT)
        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)
        addActor(background)

        table = Table()
        table.setSize(240f, 170f)
        table.setPosition(6f, 4f, Align.bottomLeft)
        addActor(table)

        refresh(0)
    }

    fun refresh(page: Int) {

        for (b in buttons) {
            buttonGroup.remove(b)
            b.remove()
        }

        buttons.clear()
        table.clear()

        for (i in page * 35 until (page + 1) * 35) {
            if (i % 7 == 0) {
                table.row()
            }

            val key = i
            val guardian = sphere.get(i)
            val monsterButton = GuardoSphereButton(skin, guardian)

            table.add<ImageButton>(monsterButton).width(32f).height(32f)
            buttons.add(monsterButton)
            buttonGroup.add(monsterButton)
            monsterButton.addListener(SimpleClickListener { callbacks!!.onClick(key) })
        }
    }

    fun setCallbacks(callbacks: Callback.SingleInt)
    {
        this.callbacks = callbacks
    }
}
