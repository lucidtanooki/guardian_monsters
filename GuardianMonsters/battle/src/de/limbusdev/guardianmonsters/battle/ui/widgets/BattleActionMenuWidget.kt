package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin

import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener
import ktx.actors.onClick

/**
 *
 * @param skin battle action UI skin
 */
class BattleActionMenuWidget
(
        skin: Skin,
        private var backCB      : () -> Unit = {},
        private var bagCB       : () -> Unit = {},
        private var monsterCB   : () -> Unit = {},
        private var extraCB     : () -> Unit = {}
)
    : BattleWidget()
{
    // Buttons
    var backButton      : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BACK)
    var monsterButton   : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.TEAM)
    var bagButton       : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.BAG)
    var extraButton     : ImageButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.DEFEND)

    init
    {
        // Add to parent
        addActor(backButton)
        addActor(monsterButton)
        addActor(bagButton)
        addActor(extraButton)

        initCallbackHandler()
    }

    private fun initCallbackHandler()
    {
        backButton.onClick(backCB)
        bagButton.onClick(bagCB)
        monsterButton.onClick(monsterCB)
        extraButton.onClick(extraCB)
    }

    fun setCallbacks
    (
            backCB: ()    -> Unit = {},
            bagCB: ()     -> Unit = {},
            monsterCB: () -> Unit = {},
            extraCB: ()   -> Unit = {}
    ) {
        this.backCB     = backCB
        this.bagCB      = bagCB
        this.monsterCB  = monsterCB
        this.extraCB    = extraCB

        initCallbackHandler()
    }

    fun disableAllButBackButton()
    {
        enable()
        disable(bagButton)
        disable(monsterButton)
        disable(extraButton)
    }

    fun disableAllChildButtons()
    {
        enable()
        disable(bagButton)
        disable(monsterButton)
        disable(extraButton)
        disable(backButton)
    }
}
