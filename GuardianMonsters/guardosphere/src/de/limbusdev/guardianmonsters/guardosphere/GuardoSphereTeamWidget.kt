package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.scene2d.*

import ktx.actors.plusAssign

/**
 * GuardoSphereTeamWidget
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereTeamWidget
(
        private val skin: Skin,
        private val team: Team,
        private val buttonGroup: ButtonGroup<Button>
)
    : Group()
{
    private val monsterButtons = HorizontalGroup()
    private val buttons = Array<Button>()
    private var callback: (Int) -> Unit

    init
    {
        // Set Callbacks
        callback = { teamPosition -> println("$TAG: Dummy Callback $teamPosition") }

        // Define Actors
        val background = Image(skin.getDrawable("guardosphere-frame"))

        // Configure Actors
        setSize(WIDTH, HEIGHT)
        background.setup(WIDTH, HEIGHT, 0f, 0f, Align.bottomLeft, this)
        monsterButtons.setup(240f, 32f, 6f, 4f, Align.bottomLeft, this)

        // Refresh Layout
        refresh()
    }

    fun refresh()
    {
        buttons.forEach { it.remove() }

        buttonGroup.clear()
        buttons.clear()
        monsterButtons.clear()

        for(key in (0 until team.size))
        {
            val guardian = team[key]
            val monsterButton = GuardoSphereButton(skin, guardian)
            monsterButtons+=monsterButton
            buttons.add(monsterButton)
            buttonGroup.add(monsterButton)
            monsterButton.replaceOnButtonClick { callback.invoke(key) }
        }
    }

    companion object
    {
        private const val TAG ="GuardoSphereTeamWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 40f
    }
}
