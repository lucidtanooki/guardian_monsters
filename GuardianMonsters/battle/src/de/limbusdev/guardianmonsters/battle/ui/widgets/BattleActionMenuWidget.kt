package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import de.limbusdev.guardianmonsters.scene2d.replaceOnButtonClick
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
        backButton.replaceOnButtonClick(onBackButton)
        bagButton.replaceOnButtonClick(onBagButton);
        teamButton.replaceOnButtonClick(onTeamButton);
        extraButton.replaceOnButtonClick(onExtraButton);
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

        if(onBackButton == null) { backButton.replaceOnButtonClick {};                disable(backButton) }
        else                     { backButton.replaceOnButtonClick(onBackButton);     enable(backButton)  }
        if(onBagButton == null)  { bagButton.replaceOnButtonClick {};                 disable(bagButton)  }
        else                     { bagButton.replaceOnButtonClick(onBagButton);       enable(bagButton)   }
        if(onTeamButton == null) { teamButton.replaceOnButtonClick {};                disable(teamButton) }
        else                     { teamButton.replaceOnButtonClick(onTeamButton);     enable(teamButton)  }
        if(onExtraButton == null){ extraButton.replaceOnButtonClick {};               disable(extraButton)}
        else                     { extraButton.replaceOnButtonClick(onExtraButton);   enable(extraButton) }
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
