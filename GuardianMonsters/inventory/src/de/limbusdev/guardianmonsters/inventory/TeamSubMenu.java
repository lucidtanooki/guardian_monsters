package de.limbusdev.guardianmonsters.inventory;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.StatusPentagonWidget;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.guardianmonsters.ui.widgets.GuardianStatusWidget;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;
import de.limbusdev.guardianmonsters.ui.widgets.TeamCircleWidget;

/**
 * Created by Georg Eckert 2017
 */

public class TeamSubMenu extends AInventorySubMenu implements Observer
{

    private StatusPentagonWidget statPent;
    private GuardianStatusWidget monsterStats;
    private Image monsterImg;
    private Image blackOverlay;
    private TeamCircleWidget circleWidget;
    private Callback.ButtonID choiceHandler, swapHandler;
    private Team team;
    private ImageButton joinsBattleButton, swapButton;
    private Group monsterChoice;
    private ImageButton.ImageButtonStyle lockedButtonStyle, normalButtonStyle;

    public TeamSubMenu(Skin skin, Team team) {

        super(skin);

        choiceHandler = position -> {

            showGuardianInformation(position);
            propagateSelectedGuardian(position);
        };

        swapHandler = position -> {

            int oldPos = circleWidget.getOldPosition();

            if(position != oldPos) {

                AGuardian currentGuardian = team.get(oldPos);
                AGuardian guardianToSwapWith = team.get(position);

                team.swap(oldPos, position);

                circleWidget.init(team);
                showGuardianInformation(position);
                propagateSelectedGuardian(position);
            }
            circleWidget.setHandler(choiceHandler);
            blackOverlay.remove();
        };

        layout(skin);

        this.team = team;
        circleWidget.init(team);

        swapButton.addListener(new SimpleClickListener(() -> {

            circleWidget.remove();
            circleWidget.setHandler(swapHandler);
            addActor(blackOverlay);
            addActor(circleWidget);
        }));

        joinsBattleButton.addListener(new SimpleClickListener(() -> {

            if (joinsBattleButton.isChecked()) {
                team.setActiveTeamSize(team.getActiveTeamSize() + 1);
            } else {
                team.setActiveTeamSize(team.getActiveTeamSize() - 1);
            }
            System.out.println("Now active in combat: " + team.getActiveTeamSize());
        }));

        monsterStats.initialize(team.get(0));

        showGuardianInformation(0);

        setDebug(Constant.DEBUGGING_ON, true);
    }

    @Override
    protected void layout(Skin skin) {

        circleWidget = new TeamCircleWidget(skin, choiceHandler);

        monsterChoice = new Group();
        monsterChoice.setSize(140, Constant.HEIGHT-36);
        monsterChoice.setPosition(0,0, Align.bottomLeft);
        Image monsterChoiceBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterChoiceBg.setPosition(2,2,Align.bottomLeft);
        monsterChoice.addActor(monsterChoiceBg);

        circleWidget.setPosition(1,40,Align.bottomLeft);
        monsterChoice.addActor(circleWidget);

        blackOverlay = new Image(skin.getDrawable("black-a80"));
        blackOverlay.setSize(Constant.WIDTH, Constant.HEIGHT);
        swapButton = new ImageButton(skin, "button-switch");
        swapButton.setPosition(8,8,Align.bottomLeft);
        monsterChoice.addActor(swapButton);

        joinsBattleButton = new ImageButton(skin, "button-check");
        joinsBattleButton.setPosition(140-8,8,Align.bottomRight);

        normalButtonStyle = joinsBattleButton.getStyle();
        lockedButtonStyle = new ImageButton.ImageButtonStyle();
        lockedButtonStyle.checked = skin.getDrawable("button-check-down-locked");

        Group monsterView = new Group();
        monsterView.setSize(140, Constant.HEIGHT-36);
        monsterView.setPosition((140+2)*2,0,Align.bottomLeft);
        Image monsterViewBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterViewBg.setPosition(2,2,Align.bottomLeft);
        monsterView.addActor(monsterViewBg);
        monsterImg = new Image();
        monsterImg.setSize(128,128);
        monsterImg.setPosition(6,202,Align.topLeft);
        monsterView.addActor(monsterImg);

        monsterStats = new GuardianStatusWidget();
        monsterStats.setPosition(140+2,0,Align.bottomLeft);

        statPent = new StatusPentagonWidget(skin);
        statPent.setPosition(20+2,4,Align.bottomLeft);
        monsterView.addActor(statPent);

        addActor(monsterChoice);
        addActor(monsterStats);
        addActor(monsterView);
    }

    private void showGuardianInformation(int teamPosition) {

        monsterStats.initialize(team.get(teamPosition));

        AGuardian chosenGuardian = team.get(teamPosition);
        int guardianID = chosenGuardian.getSpeciesID();
        int guardianForm = chosenGuardian.getAbilityGraph().getCurrentForm();

        monsterImg.setDrawable(new TextureRegionDrawable(
                Services.getMedia().getMonsterSprite(guardianID, guardianForm)));
        statPent.init(team.get(teamPosition));
        joinsBattleButton.remove();
        joinsBattleButton.setChecked(false);
        joinsBattleButton.setStyle(normalButtonStyle);

        // If the shown position belongs to the range given by activeInCombat & within 0-2
        if(teamPosition <= team.getActiveTeamSize() && teamPosition <3 && teamPosition < 3) {

            // TODO take max team size into account
            joinsBattleButton.setTouchable(Touchable.enabled);
            monsterChoice.addActor(joinsBattleButton);

            // if shown monster is in the range of active monsters
            if(teamPosition < team.getActiveTeamSize()) {

                joinsBattleButton.setChecked(true);
            }
            // the shown monster is at position 0 or not the last active monster
            if(teamPosition == 0 || teamPosition < team.getActiveTeamSize() -1) {

                joinsBattleButton.setTouchable(Touchable.disabled);
                joinsBattleButton.setStyle(lockedButtonStyle);
            }
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public void syncSelectedGuardian(int teamPosition) {

        // Update widgets
        circleWidget.init(team, teamPosition);
        showGuardianInformation(teamPosition);

        for(AGuardian guardian : team.values()) {

            guardian.deleteObserver(this);
        }
        team.get(teamPosition).addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if(observable instanceof AGuardian)
        showGuardianInformation(team.getPosition((AGuardian) observable));
    }
}
