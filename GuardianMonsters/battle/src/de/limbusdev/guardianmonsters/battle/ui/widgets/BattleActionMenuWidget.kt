package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.replaceOnClick


class BattleActionMenuWidget
(
        onBackButton    : () -> Unit = {},
        onBagButton     : () -> Unit = {},
        onTeamButton    : () -> Unit = {},
        onExtraButton   : () -> Unit = {}
)
    : BattleWidget()
{
    // .................................................................................. Properties
    val backButton  : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BACK)
    val teamButton  : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.TEAM)
    val bagButton   : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BAG)
    val extraButton : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.DEFEND)

    val skin : Skin get() = Services.getUI().battleSkin


    // ................................................................................ Constructors
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


    // ..................................................................................... Methods
    fun setCallbacks
    (
            onBackButton  : () -> Unit = {},
            onBagButton   : () -> Unit = {},
            onTeamButton  : () -> Unit = {},
            onExtraButton : () -> Unit = {}
    ) {
        backButton.replaceOnClick(onBackButton)
        bagButton.replaceOnClick(onBagButton)
        teamButton.replaceOnClick(onTeamButton)
        extraButton.replaceOnClick(onExtraButton)
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
