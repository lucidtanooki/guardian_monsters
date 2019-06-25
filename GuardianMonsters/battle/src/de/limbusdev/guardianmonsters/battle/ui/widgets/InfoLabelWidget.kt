/*
 * *************************************************************************************************
 * Copyright (c) 2019. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <limbusdev.games@gmail.com>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.battle.BattleHUD
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services

import de.limbusdev.guardianmonsters.ui.Constant
import ktx.actors.then
import ktx.actors.txt
import ktx.style.get

/**
 * InfoLabelWidget displays an information label that fits into the [BattleHUD] design.
 */
open class InfoLabelWidget() : BattleWidget()
{
    // .................................................................................. Properties
    companion object { const val typingDelay = 0.01f }

    protected var infoBGImg   : Image
    private   var infoLabel   : Label

    private   var wholeText     : String = ""
    private   var remainingText : String = ""
    private   var currentText   : String = ""


    // ................................................................................ Constructors
    init
    {
        val skin = Services.UI().battleSkin

        infoBGImg = makeImage(

                drawable = skin["label"],
                layout = ImgLayout(372f * Constant.zoom, 62f, Constant.RES_Xf / 2, 2f, Align.bottom)
        )

        infoLabel = makeLabel(skin)
        infoLabel.setupLabel(LabelLayout2D(200f, 58f, Constant.RES_Xf / 2 - 100f, 61f, Align.topLeft, Align.left, true))

        addActor(infoBGImg)
        addActor(infoLabel)
    }


    // ..................................................................................... Methods
    /** Instead of showing the complete text at once, this function starts a type writer animation. */
    fun animateAsTypeWriter()
    {
        // Clear current text
        currentText = ""

        val typeWriterSequence = runThis {

            currentText += remainingText.substring(0, 1)                     // adds next character
            remainingText = remainingText.substring(1, remainingText.length) // removes first character from remaining text
            infoLabel.txt = currentText

        } then delay(typingDelay)

        addAction(repeat(wholeText.length, typeWriterSequence))
    }

    fun typeWrite(text: String)
    {
        txt = text
        animateAsTypeWriter()
    }

    /** Provides InfoLabelWidget with the complete text, the label should display. */
    var txt : String
        get() = wholeText
        set(text)
        {
            wholeText = text
            remainingText = wholeText
            clearActions()
        }
}
