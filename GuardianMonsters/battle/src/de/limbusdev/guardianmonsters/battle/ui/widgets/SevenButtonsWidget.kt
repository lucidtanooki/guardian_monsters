package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.battle.BattleHUD

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.services.Services

import de.limbusdev.utils.extensions.set
import ktx.actors.onClick
import ktx.actors.txt
import ktx.style.get

/**
 * SevenButtonsWidget is the base for all menu widgets for the [BattleHUD] that use the central
 * menu area and a 7 buttons comb layout.
 */
open class SevenButtonsWidget
(
        private var callbacks: (Int) -> Unit,
        buttonOrder: IntArray
)
    : BattleWidget()
{
    // ............................................................................ Companion Object
    companion object
    {
        const val TAG = "SevenButtonsWidget"
        val ABILITY_ORDER : IntArray = intArrayOf(5, 3, 1, 0, 4, 2, 6)
    }


    // .................................................................................. Properties
    // Buttons
    protected val buttons : ArrayMap<Int, TextButton> = ArrayMap()
    private   val skin    : Skin get() = Services.getUI().battleSkin


    // ................................................................................ Constructors
    init
    {
        check(buttonOrder.size == 7) { "$TAG: buttonOrder must contain 7 values" }

        // Ability Buttons
        val positions = intArrayOf(
                BattleHUDTextButton.LEFT,
                BattleHUDTextButton.TOP_LEFT,
                BattleHUDTextButton.BOTTOM_LEFT,
                BattleHUDTextButton.CENTER,
                BattleHUDTextButton.TOP_RIGHT,
                BattleHUDTextButton.BOTTOM_RIGHT,
                BattleHUDTextButton.RIGHT)

        for (i in positions)
        {
            val tb : TextButton = BattleHUDTextButton("", i, Element.NONE)
            buttons[buttonOrder[i]] = tb
            this.addActor(tb)
        }

        bindCallbackHandler()
    }


    // ..................................................................................... Methods
    private fun bindCallbackHandler()
    {
        // bind callback object's methods to the button's onClick

        for (i in 0..6)
        {
            buttons[i].onClick {

                println("SevenButtonsWidget: Clicked button $i")
                if (!buttons[i].isDisabled) { callbacks.invoke(i) }
            }
        }
    }

    /** Binds the widget's buttons to a new callback function. */
    fun setCallbacks(callbacks: (Int) -> Unit)
    {
        this.callbacks = callbacks
        bindCallbackHandler()
    }

    /** Enables a button visually (tint white) and functionally (touchable and enabled) */
    protected fun enableButton(index: Int)
    {
        buttons[index].apply {

            color = Color.WHITE
            isDisabled = false
            touchable = Touchable.enabled
        }
    }

    /** Disables a button visually (tint gray) and functionally (not touchable, disabled) */
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
        buttons[index].txt= text
    }

    fun setButtonText(index: Int, ability: Ability)
    {
        setButtonText(index, Services.getL18N().Abilities().get(ability.name))
    }

    fun setButtonStyle(index: Int, style: String)
    {
        val bs = skin.get<TextButton.TextButtonStyle>(style)
        buttons[index].style = bs
    }

    fun setButtonStyle(index: Int, element: Element)
    {
        val styleString = "tb-attack-" + element.toString().toLowerCase()
        setButtonStyle(index, styleString)
    }

    protected fun getButton(index: Int): TextButton = buttons[index]

    /** Replaces the button with the given index with the provided button. */
    protected fun replaceButton(button: TextButton, index: Int)
    {
        val removedButton = buttons[index]
        buttons.removeKey(index)

        // Layout button
        button.setPosition(removedButton.x, removedButton.y, Align.bottomLeft)
        button.setScale(removedButton.scaleX, removedButton.scaleY)
        button.setSize(removedButton.width, removedButton.height)

        // Remove old button
        removedButton.remove()

        // Replace button
        buttons[index] = button

        // Bind callback handler to new button
        button.onClick {

            println("SevenButtonsWidget: Clicked button $index")
            if (!button.isDisabled) { callbacks.invoke(index) }
        }

        // Add button to menu
        addActor(button)
    }


    // ............................................................................... Inner Classes

    /**
     * Adds the half buttons above and below the central button.
     * (Example: the "?" button in the ability menu)
     */
    class CentralHalfButtonsAddOn(private val callbacks: (Int) -> Unit) : BattleWidget()
    {
        private val buttons: ArrayMap<Int, TextButton> = ArrayMap()

        init
        {
            // Half button above central button ("?")
            val button7 = BattleHUDTextButton("", 7, Element.NONE)
            buttons[7] = button7
            addActor(button7)

            // Half button below central button
            val button8 = BattleHUDTextButton("", 8, Element.NONE)
            button8.touchable = Touchable.disabled
            buttons[8] = button8
            addActor(button8)

            // Bind callbacks to them
            for (i in 7..8)
            {
                buttons[i].onClick { if (!buttons[i].isDisabled) { callbacks.invoke(i) } }
            }
        }
    }
}
