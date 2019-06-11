/*
 * *************************************************************************************************
 * Copyright (c) 2019. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <limbusdev.games@gmail.com>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.services.Services

import de.limbusdev.guardianmonsters.ui.Constant
import ktx.actors.then

open class InfoLabelWidget() : BattleWidget()
{
    protected var infoBGImg : Image
    private var infoLabel   : Label

    private var wholeText   : String = ""
    private var currentText : String = ""

    init
    {
        val skin = Services.getUI().battleSkin

        infoBGImg = Image(skin.getDrawable("label"))
        infoLabel = Label("", skin, "default")

        infoBGImg.setSize(372f * Constant.zoom, 62f * Constant.zoom)
        infoBGImg.setPosition(Constant.RES_X / 2f, Constant.zoom * 2f, Align.bottom)

        infoLabel.setSize(200f, 58f)
        infoLabel.setWrap(true)
        infoLabel.setPosition((Constant.RES_X / 2).toFloat(), 3f, Align.bottom)

        addActor(infoBGImg)
        addActor(infoLabel)
    }

    fun animateTextAppearance()
    {
        currentText = ""

        val sequence = Actions.run {

            currentText += wholeText.substring(0, 1)
            wholeText = wholeText.substring(1, wholeText.length)
            infoLabel.setText(currentText)

        } then Actions.delay(0.01f)

        addAction(Actions.repeat(wholeText.length, sequence))
    }

    fun setWholeText(wholeText: String)
    {
        this.wholeText = wholeText
        clearActions()
    }
}
