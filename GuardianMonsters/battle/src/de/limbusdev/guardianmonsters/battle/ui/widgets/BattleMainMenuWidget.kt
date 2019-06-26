package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import de.limbusdev.guardianmonsters.scene2d.replaceOnButtonClick
import de.limbusdev.guardianmonsters.services.Services

import de.limbusdev.guardianmonsters.ui.Constant


/**
 * BattleMainMenuWidget
 *
 * @author Georg Eckert 2016
 */
class BattleMainMenuWidget
(
        onSwordButton : () -> Unit,
        onRunButton   : () -> Unit
)
    : BattleWidget()
{
    // .................................................................................. Properties
    // Buttons
    private val swordButton : ImageButton
    private val runButton   : ImageButton

    private val skin: Skin get() = Services.UI().battleSkin


    // ................................................................................ Constructors
    init
    {
        this.setBounds(0f, 0f, Constant.RES_X.toFloat(), 64f)

        val emptyButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.EMPTY)
        addActor(emptyButton)

        // Fight Button
        swordButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.SWORD)

        // Escape Button
        runButton = BattleHUDMenuButton(skin, BattleHUDMenuButton.ESCAPE)

        this.addActor(emptyButton)
        this.addActor(swordButton)
        this.addActor(runButton)

        swordButton.replaceOnButtonClick(onSwordButton)
        runButton.replaceOnButtonClick(onRunButton)
    }
}
