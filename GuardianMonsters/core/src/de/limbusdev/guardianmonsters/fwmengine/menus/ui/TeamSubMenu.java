package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.ATeamChoiceWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.MonsterStatusInventoryWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.StatusPentagonWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.TeamCircleWidget;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.Constant;
import de.limbusdev.guardianmonsters.utils.GameState;

/**
 * Created by Georg Eckert 2017
 */

public class TeamSubMenu extends AInventorySubMenu {

    private StatusPentagonWidget statPent;
    private MonsterStatusInventoryWidget monsterStats;
    private Image monsterImg;
    private Image blackOverlay;
    private TeamCircleWidget circleWidget;
    private ATeamChoiceWidget.ClickListener choiceHandler, swapHandler;
    private TeamComponent team;
    private ImageButton joinsBattleButton;
    private Group monsterChoice;
    private ImageButton.ImageButtonStyle lockedButtonStyle, normalButtonStyle;

    public TeamSubMenu(Skin skin, TeamComponent guardians) {
        super(skin);
        Media media = Services.getMedia();
        this.team = guardians;

        monsterChoice = new Group();
        monsterChoice.setSize(140, Constant.HEIGHT-36);
        monsterChoice.setPosition(0,0, Align.bottomLeft);
        Image monsterChoiceBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterChoiceBg.setPosition(2,2,Align.bottomLeft);
        monsterChoice.addActor(monsterChoiceBg);

        choiceHandler = new ATeamChoiceWidget.ClickListener() {
            @Override
            public void onTeamMemberButton(int position) {
                showGuardianInformation(position);
            }
        };

        swapHandler = new ATeamChoiceWidget.ClickListener() {
            @Override
            public void onTeamMemberButton(int position) {
                System.out.println("Clicked " + position);
                int oldPos = circleWidget.getOldPosition();
                if(position != oldPos) {
                    Monster currentMonster = team.monsters.get(oldPos);
                    Monster monsterToSwapWith = team.monsters.get(position);

                    team.monsters.put(oldPos, monsterToSwapWith);
                    team.monsters.put(position, currentMonster);

                    circleWidget.init(team.monsters);
                    showGuardianInformation(position);
                }
                circleWidget.setHandler(choiceHandler);
                blackOverlay.remove();
            }
        };

        circleWidget = new TeamCircleWidget(skin, team.monsters, choiceHandler);
        circleWidget.setPosition(1,40,Align.bottomLeft);
        monsterChoice.addActor(circleWidget);

        blackOverlay = new Image(skin.getDrawable("black-a80"));
        blackOverlay.setSize(Constant.WIDTH, Constant.HEIGHT);
        ImageButton swapButton = new ImageButton(skin, "button-switch");
        swapButton.setPosition(8,8,Align.bottomLeft);
        swapButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                circleWidget.remove();
                circleWidget.setHandler(swapHandler);
                addActor(blackOverlay);
                addActor(circleWidget);
            }
        });
        monsterChoice.addActor(swapButton);

        joinsBattleButton = new ImageButton(skin, "button-check");
        joinsBattleButton.setPosition(140-8,8,Align.bottomRight);
        joinsBattleButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(joinsBattleButton.isChecked())
                    team.activeInCombat+=1;
                else
                    team.activeInCombat-=1;

                System.out.println("Now active in combat: " + team.activeInCombat);
            }
        });
        normalButtonStyle = joinsBattleButton.getStyle();
        lockedButtonStyle = new ImageButton.ImageButtonStyle();
        lockedButtonStyle.checked = skin.getDrawable("button-check-down-locked");

        monsterStats = new MonsterStatusInventoryWidget(skin);
        monsterStats.setPosition(140+2,0,Align.bottomLeft);
        monsterStats.init(team.monsters.get(0));


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

        statPent = new StatusPentagonWidget(skin);
        statPent.setPosition(20+2,4,Align.bottomLeft);
        monsterView.addActor(statPent);

        addActor(monsterChoice);
        addActor(monsterStats);
        addActor(monsterView);

        showGuardianInformation(0);

        setDebug(Constant.DEBUGGING_ON, true);
    }

    private void showGuardianInformation(int teamPosition) {
        monsterStats.init(team.monsters.get(teamPosition));
        monsterImg.setDrawable(new TextureRegionDrawable(Services.getMedia().getMonsterSprite(team.monsters.get(teamPosition).ID)));
        statPent.init(team.monsters.get(teamPosition));
        joinsBattleButton.remove();
        joinsBattleButton.setChecked(false);
        joinsBattleButton.setStyle(normalButtonStyle);

        GameState state = SaveGameManager.loadSaveGame();

        // If the shown position belongs to the range given by activeInCombat & within 0-2
        if(teamPosition <= team.activeInCombat && teamPosition <3 && teamPosition < state.maxTeamSizeInBattle) {
            joinsBattleButton.setTouchable(Touchable.enabled);
            monsterChoice.addActor(joinsBattleButton);

            // if shown monster is in the range of active monsters
            if(teamPosition < team.activeInCombat) {
                joinsBattleButton.setChecked(true);
            }
            // the shown monster is at position 0 or not the last active monster
            if(teamPosition == 0 || teamPosition < team.activeInCombat -1) {
                joinsBattleButton.setTouchable(Touchable.disabled);
                joinsBattleButton.setStyle(lockedButtonStyle);
            }
        }
    }

    @Override
    public void refresh() {
        showGuardianInformation(circleWidget.getCurrentPosition());
    }
}
