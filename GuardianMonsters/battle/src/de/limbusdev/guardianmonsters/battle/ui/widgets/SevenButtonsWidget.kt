package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.Callback
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener

import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.BOTTOMLEFT
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.BOTTOMRIGHT
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.CENTER
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.LEFT
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.RIGHT
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.TOPLEFT
import de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDTextButton.TOPRIGHT
import de.limbusdev.utils.extensions.set
import ktx.actors.onClick

open class SevenButtonsWidget
(
        protected var skin: Skin,
        private var callbacks: (Int) -> Unit,
        buttonOrder: IntArray
)
    : BattleWidget()
{
    // ............................................................................ Companion Object
    companion object
    {
        val ABILITY_ORDER : IntArray = intArrayOf(5, 3, 1, 0, 4, 2, 6)
    }


    // .................................................................................. Properties
    // Buttons
    protected val buttons: ArrayMap<Int, TextButton> = ArrayMap()


    // ................................................................................ Constructors
    init
    {
        if (buttonOrder.size < 7)
        {
            throw IllegalArgumentException("buttonOrder must contain 7 values")
        }

        // Ability Buttons
        val positions = intArrayOf(LEFT, TOPLEFT, BOTTOMLEFT, CENTER, TOPRIGHT, BOTTOMRIGHT, RIGHT)

        for (i in positions)
        {
            val tb : TextButton = BattleHUDTextButton("", skin, i, Element.NONE)
            buttons.put(buttonOrder[i], tb)
            addActor(tb)
        }

        initCallbackHandler()
    }


    // ..................................................................................... Methods
    private fun initCallbackHandler()
    {
        // bind callback object's methods to the button's onClick

        for (i in 0..6)
        {
            buttons[i].onClick {

                println("SevenButtonsWidget: Clicked button $i")
                if (!buttons[i].isDisabled) { callbacks(i) }
            }
        }
    }

    fun setCallbacks(callbacks: (Int) -> Unit)
    {
        this.callbacks = callbacks
        initCallbackHandler()
    }

    protected fun enableButton(index: Int)
    {
        buttons[index].apply {

            color = Color.WHITE
            isDisabled = false
            touchable = Touchable.enabled
        }
    }

    protected fun disableButton(index: Int)
    {
        buttons[index].apply {

            color = Color.GRAY
            isDisabled = true
            touchable = Touchable.disabled
        }
    }

    fun setButtonText(index: Int, text: String)
    {
        buttons[index].setText(text)
    }

    fun setButtonText(index: Int, ability: Ability)
    {
        setButtonText(index, Services.getL18N().Abilities().get(ability.name))
    }

    fun setButtonStyle(index: Int, skin: Skin, style: String)
    {
        val bs = skin.get(style, TextButton.TextButtonStyle::class.java)
        buttons[index].style = bs
    }

    fun setButtonStyle(index: Int, element: Element)
    {
        val skin = Services.getUI().battleSkin
        val styleString = "tb-attack-" + element.toString().toLowerCase()
        setButtonStyle(index, skin, styleString)
    }

    protected fun getButton(index: Int): TextButton = buttons[index]

    protected fun replaceButton(button: TextButton, index: Int)
    {
        val removedButton = buttons.get(index)
        buttons.removeKey(index)

        button.setPosition(removedButton.x, removedButton.y, Align.bottomLeft)
        button.setScale(removedButton.scaleX, removedButton.scaleY)
        button.setSize(removedButton.width, removedButton.height)
        removedButton.remove()

        buttons[index] = button

        button.onClick {

            println("SevenButtonsWidget: Clicked button $index")
            if (!button.isDisabled) { callbacks(index) }
        }
        addActor(button)
    }


    // ............................................................................... Inner Classes

    class CentralHalfButtonsAddOn
    (
            skin: Skin,
            private val callbacks: Callback.ButtonID
    )
        : BattleWidget()
    {
        private val buttons: ArrayMap<Int, TextButton> = ArrayMap()

        init
        {
            val button7 = BattleHUDTextButton("", skin, 7, Element.NONE)
            buttons[7] = button7
            addActor(button7)
            val button8 = BattleHUDTextButton("", skin, 8, Element.NONE)
            button8.touchable = Touchable.disabled
            buttons[8] = button8
            addActor(button8)

            for (i in 7..8)
            {
                buttons[i].onClick {

                    if (!buttons[i].isDisabled) { callbacks.onClick(i) }
                }
            }
        }
    }
}
