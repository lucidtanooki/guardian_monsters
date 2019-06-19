package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.f
import de.limbusdev.utils.extensions.replaceOnClick


/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */

/**
 *
 * @param skin battle action UI skin
 */
class EndOfBattleWidget
(
        skin: Skin,
        onBackButton: () -> Unit
)
    : BattleWidget()
{
    // .................................................................................. Properties
    private val labelBGImg  : Image
    private val bgImg       : Image
    var messageLabel        : Label
    var backButton          : ImageButton


    // ................................................................................ Constructors
    init
    {
        this.setBounds(0f, 0f, 0f, 0f)

        labelBGImg = Image(skin.getDrawable("b-long-up"))
        labelBGImg.setPosition(Constant.RES_X / 2f, Constant.ROW * 7f, Align.bottom)
        bgImg = Image(skin.getDrawable("eob-pane"))
        bgImg.setPosition(Constant.RES_X / 2f, 0f, Align.bottom)

        addActor(labelBGImg)
        addActor(bgImg)

        val labs = Label.LabelStyle()
        labs.font = skin.getFont("default-font")
        messageLabel = Label("---", labs)
        messageLabel.height = 64f
        messageLabel.width = 500f
        messageLabel.setWrap(true)
        messageLabel.setPosition(Constant.RES_X / 2f, Constant.ROW * 8f, Align.bottom)
        addActor(messageLabel)

        // Change Screen
        backButton = ImageButton(skin, "b-back-eob")
        backButton.setPosition(Constant.RES_X.f(), 0f, Align.bottomRight)
        addActor(backButton)

        backButton.replaceOnClick(onBackButton)
    }

    /**
     * true = hero won
     * false = opponent won
     * @param won
     */
    fun initialize(won: Boolean)
    {
        val i18n = Services.getL18N().Battle()
        val message = if (won) "batt_game_over" else "batt_you_won"
        messageLabel.setText(i18n.get(message))
    }
}
