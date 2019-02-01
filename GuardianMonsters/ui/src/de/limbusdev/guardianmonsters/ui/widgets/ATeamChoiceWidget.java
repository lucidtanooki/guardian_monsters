/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.utils.geometry.IntVec2;

public abstract class ATeamChoiceWidget extends Group {

    protected Array<IntVec2> positions;
    protected Callback.ButtonID handler;
    protected ButtonGroup<ImageButton> memberButtons;
    protected int currentPosition=0, oldPosition=0;
    protected Skin skin;
    protected Group buttons;

    public ATeamChoiceWidget(Skin skin, Callback.ButtonID clHandler)
    {
        super();
        this.skin = skin;
        this.handler = clHandler;
        positions = new Array<>();
        buttons = new Group();
    }

    public void init(Team team, int initialPosition)
    {
        this.currentPosition = initialPosition;
        init(team);
    }

    public void init(Team team)
    {
        memberButtons = new ButtonGroup<>();
        buttons.clearChildren();
        addActor(buttons);

        for(final int key : team.keys())
        {
            AGuardian m = team.get(key);
            Image preview = new Image(Services.getMedia().getMonsterMiniSprite(m.getSpeciesDescription().getID(), m.getAbilityGraph().getCurrentForm()));
            preview.setPosition(38/2-8, 38/2-8, Align.bottomLeft);
            ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
            Drawable imUp = skin.getDrawable("transparent");
            imUp.setMinHeight(38);
            imUp.setMinWidth(38);
            ibs.imageUp = imUp;
            ibs.imageChecked = skin.getDrawable("teamCircle-chosen");
            ibs.imageDown = skin.getDrawable("teamCircle-chosen");
            ImageButton button = new ImageButton(ibs);
            button.addActor(preview);
            IntVec2 pos = positions.get(key);
            button.setPosition(pos.getX() -3, pos.getY() -4, Align.bottomLeft);

            button.addListener(new SimpleClickListener(() ->
            {
                    oldPosition = currentPosition;
                    currentPosition = key;
                    handler.onClick(key);
                    System.out.println("Current Position set to " + key);
            }));

            memberButtons.add(button);
            buttons.addActor(button);
            if(key == currentPosition) {
                button.setChecked(true);
            }
        }
        memberButtons.setMaxCheckCount(1);
    }

    public Array<IntVec2> getPositions() {
        return positions;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public void setHandler(Callback.ButtonID handler) {
        this.handler = handler;
    }


}
