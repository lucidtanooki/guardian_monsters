package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin

import ktx.actors.onClick

/**
 *
 * @param skin battle action UI skin
 */
class BattleActionMenuWidget
(
        skin: Skin,
        onBackButton    : () -> Unit = {},
        onBagButton     : () -> Unit = {},
        onTeamButton    : () -> Unit = {},
        onExtraButton   : () -> Unit = {}
)
    : BattleWidget()
{
    // Buttons
    val backButton  : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BACK)
    val teamButton  : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.TEAM)
    val bagButton   : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BAG)
    val extraButton : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.DEFEND)

    init
    {
        // Add to parent
        addActor(backButton)
        addActor(teamButton)
        addActor(bagButton)
        addActor(extraButton)

        setCallbacks(
                onBackButton  = onBackButton,
                onBagButton   =  onBagButton,
                onTeamButton  = onTeamButton,
                onExtraButton = onExtraButton
        )
    }

    fun setCallbacks
    (
            onBackButton  : () -> Unit = {},
            onBagButton   : () -> Unit = {},
            onTeamButton  : () -> Unit = {},
            onExtraButton : () -> Unit = {}
    ) {
        backButton.onClick(onBackButton)
        bagButton.onClick(onBagButton)
        teamButton.onClick(onTeamButton)
        extraButton.onClick(onExtraButton)
    }

    fun disableAllButBackButton()
    {
        enable()
        disable(bagButton)
        disable(teamButton)
        disable(extraButton)
    }

    fun disableAllChildButtons()
    {
        enable()
        disable(bagButton)
        disable(teamButton)
        disable(extraButton)
        disable(backButton)
    }
}
