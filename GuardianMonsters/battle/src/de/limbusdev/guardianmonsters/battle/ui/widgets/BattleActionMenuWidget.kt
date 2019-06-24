package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import de.limbusdev.guardianmonsters.scene2d.replaceOnClick
import de.limbusdev.guardianmonsters.services.Services


class BattleActionMenuWidget
(
        skin: Skin = Services.UI().battleSkin,
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
        bagButton.replaceOnClick(onBagButton);
        teamButton.replaceOnClick(onTeamButton);
        extraButton.replaceOnClick(onExtraButton);
    }

    /** Sets all given callbacks. Disables all buttons where callback is null. Enables the others. */
    fun setCallbacksAndAutoEnable
    (
            onBackButton  : (() -> Unit)? = null,
            onBagButton   : (() -> Unit)? = null,
            onTeamButton  : (() -> Unit)? = null,
            onExtraButton : (() -> Unit)? = null
    ) {
        enable()

        if(onBackButton == null) { backButton.replaceOnClick {};                disable(backButton) }
        else                     { backButton.replaceOnClick(onBackButton);     enable(backButton)  }
        if(onBagButton == null)  { bagButton.replaceOnClick {};                 disable(bagButton)  }
        else                     { bagButton.replaceOnClick(onBagButton);       enable(bagButton)   }
        if(onTeamButton == null) { teamButton.replaceOnClick {};                disable(teamButton) }
        else                     { teamButton.replaceOnClick(onTeamButton);     enable(teamButton)  }
        if(onExtraButton == null){ extraButton.replaceOnClick {};               disable(extraButton)}
        else                     { extraButton.replaceOnClick(onExtraButton);   enable(extraButton) }
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
