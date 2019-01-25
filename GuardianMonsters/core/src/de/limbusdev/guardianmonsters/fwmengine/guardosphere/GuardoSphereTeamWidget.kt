package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import ktx.actors.onClick
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
    var callback: (Int) -> Unit

    init
    {
        // Set Callbacks
        callback = { teamPosition -> println("$TAG: Dummy Callback $teamPosition") }

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
            b.remove()
        }
        buttonGroup.clear()
        buttons.clear()
        monsterButtons.clear()

        for(key in team.keys())
        {
            val guardian = team[key]
            val monsterButton = GuardoSphereButton(skin, guardian)
            monsterButtons+monsterButton
            buttons+monsterButton
            buttonGroup.add(monsterButton)
            monsterButton.onClick { callback.invoke(key) }
        }
    }

    companion object
    {
        private const val TAG ="GuardoSphereTeamWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 40f
    }
}