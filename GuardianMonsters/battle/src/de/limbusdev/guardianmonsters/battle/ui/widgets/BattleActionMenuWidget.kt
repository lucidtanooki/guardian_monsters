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
        private var onBackButton    : () -> Unit = {},
        private var onBagButton     : () -> Unit = {},
        private var onTeamButton    : () -> Unit = {},
        private var onExtraButton   : () -> Unit = {}
)
    : BattleWidget()
{
    // Buttons
    var backButton  : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BACK)
    var teamButton  : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.TEAM)
    var bagButton   : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BAG)
    var extraButton : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.DEFEND)

    init
    {
        // Add to parent
        addActor(backButton)
        addActor(teamButton)
        addActor(bagButton)
        addActor(extraButton)

        initCallbackHandler()
    }

    private fun initCallbackHandler()
    {
        backButton.onClick(onBackButton)
        bagButton.onClick(onBagButton)
        teamButton.onClick(onTeamButton)
        extraButton.onClick(onExtraButton)
    }

    fun setCallbacks
    (
            onBackButton  : () -> Unit = {},
            onBagButton   : () -> Unit = {},
            onTeamButton  : () -> Unit = {},
            onExtraButton : () -> Unit = {}
    ) {
        this.onBackButton     = onBackButton
        this.onBagButton      = onBagButton
        this.onTeamButton     = onTeamButton
        this.onExtraButton    = onExtraButton

        initCallbackHandler()
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
