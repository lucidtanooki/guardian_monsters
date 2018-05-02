/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.ATeamChoiceWidget;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.guardianmonsters.ui.widgets.GuardianStatusWidget;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;
import de.limbusdev.guardianmonsters.ui.widgets.TeamCircleWidget;
import de.limbusdev.guardianmonsters.ui.widgets.TiledImage;

/**
 * SwitchActiveGuardianWidget
 *
 * @author Georg Eckert 2018
 */

public class SwitchActiveGuardianWidget extends BattleWidget
{
    private GuardianStatusWidget guardianStatusWidget;
    private ATeamChoiceWidget    teamChoiceWidget;
    private Callback backButtonCallback;
    private Callback switchButtonCallback;
    private Image guardianImg;
    private Button switchButton;
    private Button backButton;

    public SwitchActiveGuardianWidget(Skin battleSkin, Skin inventorySkin)
    {
        setSize(Constant.WIDTH, Constant.HEIGHT);
        TiledImage bgImg = new TiledImage(inventorySkin.getDrawable("bg-pattern-3"), 27, 16);
        bgImg.setPosition(0,0, Align.bottomLeft);
        addActor(bgImg);

        guardianStatusWidget = new GuardianStatusWidget(Services.getUI().getInventorySkin());;
        guardianStatusWidget.setPosition(2, Constant.HEIGHT, Align.topLeft);
        addActor(guardianStatusWidget);

        backButton = new ImageButton(inventorySkin, "button-back");
        backButton.setPosition(Constant.WIDTH-2, 2, Align.bottomRight);
        addActor(backButton);

        switchButton = new ImageButton(inventorySkin, "button-switch");
        switchButton.setPosition(2,2,Align.bottomLeft);
        addActor(switchButton);

        teamChoiceWidget = new TeamCircleWidget(Services.getUI().getInventorySkin(), null);
        teamChoiceWidget.setPosition(Constant.WIDTH-2,Constant.HEIGHT/2,Align.right);
        addActor(teamChoiceWidget);

        Group guardianView = new Group();
        guardianView.setSize(140, Constant.HEIGHT-36);
        guardianView.setPosition(Constant.WIDTH/2, Constant.HEIGHT, Align.top);
        Image monsterViewBg = new Image(inventorySkin.getDrawable("menu-col-bg"));
        monsterViewBg.setPosition(2,2,Align.bottomLeft);
        guardianView.addActor(monsterViewBg);
        guardianImg = new Image();
        guardianImg.setSize(128,128);
        guardianImg.setPosition(6,202,Align.topLeft);
        guardianView.addActor(guardianImg);
        addActor(guardianView);
    }

    public void setCallbacks(Callback onBack, Callback onSwitch)
    {
        this.backButtonCallback = onBack;
        this.switchButtonCallback = onSwitch;

        backButton.clearListeners();
        switchButton.clearListeners();
        backButton.addListener(new SimpleClickListener(backButtonCallback));
        switchButton.addListener(new SimpleClickListener(switchButtonCallback));
    }

    public void init(AGuardian guardian, Team team)
    {
        guardianStatusWidget.init(guardian);
        teamChoiceWidget.init(team, team.getPosition(guardian));
        activateGuardian(guardian, team);

        Callback.ButtonID circleMenuCallbacks = nr ->
        {
            activateGuardian(team.get(nr), team);
        };

        teamChoiceWidget.setHandler(circleMenuCallbacks);
    }

    private void activateGuardian(AGuardian guardian, Team team)
    {
        int chosenPos = teamChoiceWidget.getCurrentPosition();
        if(chosenPos >= team.getActiveTeamSize()) {
            switchButton.setTouchable(Touchable.enabled);
            switchButton.setColor(Color.WHITE);
        } else {
            switchButton.setTouchable(Touchable.disabled);
            switchButton.setColor(Color.GRAY);
        }

        guardianStatusWidget.init(guardian);
        int speciesID = guardian.getSpeciesID();
        int currentForm = guardian.getAbilityGraph().getCurrentForm();
        TextureAtlas.AtlasRegion sprite = Services.getMedia().getMonsterSprite(speciesID, currentForm);
        guardianImg.setDrawable(new TextureRegionDrawable(sprite));
    }

    public int getChosenSubstitute()
    {
        return teamChoiceWidget.getCurrentPosition();
    }
}
