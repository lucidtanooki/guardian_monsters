/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.scene2d.ImgPosition
import de.limbusdev.guardianmonsters.scene2d.Position2D
import de.limbusdev.guardianmonsters.scene2d.position
import de.limbusdev.guardianmonsters.scene2d.replaceOnClick
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logInfo
import ktx.style.get

abstract class ATeamChoiceWidget(private val skin: Skin, private var handler: (Int) -> Unit) : Group()
{
    companion object { const val TAG = "ATeamChoiceWidget" }

    var positions: Array<IntVec2> protected set
    protected var memberButtons = ButtonGroup<ImageButton>()
    protected var buttons: Group

    var currentPosition = 0
        protected set
    var oldPosition = 0
        protected set


    init
    {
        positions = Array()
        buttons = Group()
    }

    fun initialize(team: Team, initialPosition: Int)
    {
        this.currentPosition = initialPosition
        initialize(team)
    }

    fun initialize(team: Team)
    {
        memberButtons.clear()
        buttons.clearChildren()
        addActor(buttons)

        for (key in team.occupiedSlotsKeys())
        {
            val guardian = team[key]
            val preview = Image(Services.Media().getMonsterMiniSprite(guardian.speciesID, guardian.currentForm))
            preview.position = ImgPosition(38f / 2 - 8, 38f / 2 - 8, Align.bottomLeft)

            val imUp = skin.get<Drawable>("transparent").apply { minHeight = 38f; minWidth = 38f }

            val ibs = ImageButton.ImageButtonStyle().apply {

                imageUp      = imUp
                imageChecked = skin["teamCircle-chosen"]
                imageDown    = skin["teamCircle-chosen"]
            }

            val button = ImageButton(ibs)
            button.addActor(preview)
            val pos = positions.get(key)
            button.setPosition(pos.x - 3f, pos.y - 4f, Align.bottomLeft)

            button.replaceOnClick {

                logInfo(TAG) { "Current Position set to $key" }
                oldPosition = currentPosition
                currentPosition = key
                handler(key)
            }

            memberButtons.add(button)
            buttons.addActor(button)

            button.isChecked = (key == currentPosition)
        }
        memberButtons.setMaxCheckCount(1)
    }

    fun setHandler(handler: (Int) -> Unit)
    {
        this.handler = handler
    }
}
