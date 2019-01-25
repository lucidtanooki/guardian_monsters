package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import ktx.actors.plus

/**
 * GuardoSphereTeamWidget
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereTeamWidget(

        private val skin: Skin,
        private val team: Team,
        private val buttonGroup: ButtonGroup<Button>

) : Group()
{
    private val monsterButtons: HorizontalGroup
    private val buttons: Array<Button>
    var callbacks: Callback.SingleInt? = null

    init
    {
        // Set Callbacks
        callbacks = Callback.SingleInt {

            teamPosition -> println("GuardoSphereTeamWidget: Dummy Callback $teamPosition")
        }

        // Define Actors
        buttons = Array()
        val background = Image(skin.getDrawable("guardosphere-frame"))
        monsterButtons = HorizontalGroup()

        // Scale Actors
        setSize(WIDTH, HEIGHT)
        background.setSize(WIDTH, HEIGHT)
        monsterButtons.setSize(240f, 32f)

        // Position Actors
        background.setPosition(0f, 0f, Align.bottomLeft)
        monsterButtons.setPosition(6f, 4f, Align.bottomLeft)

        // Add Actors
        this+background
        this+monsterButtons

        // Refresh Layout
        refresh()
    }

    fun refresh()
    {
        for(b in buttons)
        {
            buttonGroup.remove(b)
            b.remove()
        }
        buttons.clear()
        monsterButtons.clear()

        for(key in team.keys())
        {
            val guardian = team[key]
            val monsterButton = GuardoSphereButton(skin, guardian)
            monsterButtons+monsterButton
            buttons.add(monsterButton)
            buttonGroup.add(monsterButton)
            monsterButton.addListener(
                    object : ClickListener()
                    {
                        override fun clicked(event: InputEvent?, x: Float, y: Float)
                        {
                            callbacks!!.onClick(key!!)
                        }
                    }
            )
        }
    }

    companion object
    {
        private const val WIDTH = 252f
        private const val HEIGHT = 40f
    }
}