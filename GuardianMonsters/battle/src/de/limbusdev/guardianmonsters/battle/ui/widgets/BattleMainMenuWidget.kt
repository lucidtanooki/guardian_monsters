package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin

import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener
import ktx.actors.onClick


/**
 * Created by Georg Eckert 2016
 */


/**
 *
 * @param skin battle UI skin
 */
class BattleMainMenuWidget
(
        skin: Skin,
        onSwordButton: () -> Unit,
        onRunButton: () -> Unit
)
    : BattleWidget()
{
    // .................................................................................. Properties
    // Buttons
    private val swordButton: ImageButton
    private val runButton: ImageButton


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

        swordButton.onClick(onSwordButton)
        runButton.onClick(onRunButton)
    }
}
