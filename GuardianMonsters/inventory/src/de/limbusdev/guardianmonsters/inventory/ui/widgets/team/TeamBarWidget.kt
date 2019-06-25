package de.limbusdev.guardianmonsters.inventory.ui.widgets.team

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.ui.widgets.ATeamChoiceWidget
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.utils.geometry.IntVec2

/**
 * @author Georg Eckert 2017
 */

class TeamBarWidget(skin: Skin, team: Team, callbacks: (Int) -> Unit) : ATeamChoiceWidget(skin, callbacks)
{
    init
    {
        setSize(260f, 44f)

        positions.clear()
        positions.add(IntVec2(4, 8))
        positions.add(IntVec2(4 + (2 + 36), 8))
        positions.add(IntVec2(4 + (2 + 36) * 2, 8))
        positions.add(IntVec2(4 + (2 + 36) * 3, 8))
        positions.add(IntVec2(4 + (2 + 36) * 4, 8))
        positions.add(IntVec2(4 + (2 + 36) * 5, 8))
        positions.add(IntVec2(4 + (2 + 36) * 6, 8))


        val bg = Label("", skin, "list-item")
        bg.setSize(260f, 44f)
        bg.setPosition(0f, 0f, Align.bottomLeft)
        addActor(bg)

        initialize(team)
    }
}
