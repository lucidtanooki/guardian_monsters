/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.monsters.Team
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.utils.geometry.IntVec2
import ktx.style.get

class TeamCircleWidget(skin: Skin, callbacks: (Int) -> Unit) : ATeamChoiceWidget(skin, callbacks)
{
    init
    {
        positions.add(IntVec2(54, 144 - 85))
        positions.add(IntVec2(54, 144 - 45))
        positions.add(IntVec2(89, 144 - 65))
        positions.add(IntVec2(89, 144 - 105))
        positions.add(IntVec2(54, 144 - 125))
        positions.add(IntVec2(19, 144 - 105))
        positions.add(IntVec2(19, 144 - 65))

        setSize(140f, 140f)
        val bgImg = makeImage(skin["teamCircle"], this)
        bgImg.setPosition(0f, 0f, Align.bottomLeft)
        addActor(bgImg)
    }

    constructor(skin: Skin, team: Team, callbacks: (Int) -> Unit) : this(skin, callbacks)
    {
        initialize(team)
    }
}
