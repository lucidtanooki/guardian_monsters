package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.scene2d.lSetPosition
import de.limbusdev.guardianmonsters.scene2d.lSetSize
import ktx.actors.onClick
import ktx.actors.plusAssign

/**
 * GuardoSphereTeamWidget
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereTeamWidget(

        private val skin: Skin,
        private val team: Team,
        private val buttonGroup: ButtonGroup<Button>

)
    : Group()
{
    private val monsterButtons = HorizontalGroup()
    private val buttons = Array<Button>()
    var callback: (Int) -> Unit

    init
    {
        // Set Callbacks
        callback = { teamPosition -> println("$TAG: Dummy Callback $teamPosition") }

        // Define Actors
        val background = Image(skin.getDrawable("guardosphere-frame"))

        // Configure Actors
        setSize(WIDTH, HEIGHT)
        background
                .lSetSize(WIDTH, HEIGHT)
                .lSetPosition(0f, 0f, Align.bottomLeft)
        monsterButtons
                .lSetSize(240f, 32f)
                .lSetPosition(6f, 4f, Align.bottomLeft)

        // Add Actors
        this+=background
        this+=monsterButtons

        // Refresh Layout
        refresh()
    }

    fun refresh()
    {
        for(b in buttons) b.remove()

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
