

/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.utils.geometry.IntVec2;

public class TeamCircleWidget extends ATeamChoiceWidget
{
    public TeamCircleWidget(Skin skin, Callback.ButtonID callbacks)
    {
        super(skin, callbacks);

        positions.add(new IntVec2(54,144-85));
        positions.add(new IntVec2(54,144-45));
        positions.add(new IntVec2(89,144-65));
        positions.add(new IntVec2(89,144-105));
        positions.add(new IntVec2(54,144-125));
        positions.add(new IntVec2(19,144-105));
        positions.add(new IntVec2(19,144-65));

        setSize(140,140);
        Image bgImg = new Image(skin.getDrawable("teamCircle"));
        bgImg.setPosition(0,0, Align.bottomLeft);
        addActor(bgImg);
    }

    public TeamCircleWidget(Skin skin, Team team, Callback.ButtonID callbacks)
    {
        this(skin, callbacks);
        init(team);
    }
}
