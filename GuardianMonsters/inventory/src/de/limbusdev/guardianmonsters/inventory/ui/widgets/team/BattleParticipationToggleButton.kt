package de.limbusdev.guardianmonsters.inventory.de.limbusdev.guardianmonsters.inventory.ui.widgets.team

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import de.limbusdev.guardianmonsters.scene2d.PositionXYA
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.logWarning
import ktx.style.get

class BattleParticipationToggleButton
(
        skin        : Skin = Services.UI().inventorySkin,
        positionXYA : PositionXYA? = null,
        parent      : Group? = null
)
    : ImageButton(skin, "button-check")
{
    companion object { const val TAG = "BattleParticipationToggleButton" }

    private val defaultStyle = skin.get<ImageButtonStyle>("button-check")
    private val lockedStyle  = ImageButtonStyle().apply { checked = skin["button-check-down-locked"] }

    init
    {
        if(positionXYA != null) { setPosition(positionXYA.x, positionXYA.y, positionXYA.align) }
        parent?.addActor(this)
    }

    /** Adds a button for Guardians on positions 0..2, to enable or disable their battle participation. */
    fun autoEnableOrDisable(teamPosition: Int, activeTeamSize: Int, parent: Group? = null)
    {
        if(teamPosition !in 0..2) { logWarning(TAG) { "Participation button must not be visible for team position $teamPosition" } }

        // Only team positions 0..2 can be activated
        if (teamPosition in 0..2)
        {
            parent?.addActor(this)

            when
            {
                // Position 0 can never be disabled
                teamPosition == 0 ->
                {
                    isChecked = true
                    style = lockedStyle
                    touchable = Touchable.disabled
                }
                // Only the last active Guardian can be disabled
                teamPosition == activeTeamSize - 1 ->
                {
                    isChecked = true
                    style = defaultStyle
                    touchable = Touchable.enabled
                }
                // The Guardian after the last active one can be enabled, too
                teamPosition == activeTeamSize ->
                {
                    isChecked = false
                    style = defaultStyle
                    touchable = Touchable.enabled
                }
                // Active Guardians before the last active one cannot be disabled
                teamPosition < activeTeamSize ->
                {
                    isChecked = true
                    style = lockedStyle
                    touchable = Touchable.disabled
                }
                // Guardians not next to an enabled one cannot be activated
                else ->
                {
                    remove()
                }
            }
        }
    }
}