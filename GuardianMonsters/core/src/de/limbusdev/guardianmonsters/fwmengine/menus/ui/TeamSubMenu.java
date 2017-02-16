package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 16.02.17.
 */

public class TeamSubMenu extends AInventorySubMenu {

    private StatusPentagonWidget statPent;
    private MonsterStatusInventoryWidget monsterStats;
    private Image monsterImg;

    public TeamSubMenu(Skin skin, final ArrayMap<Integer, Monster> team) {
        super(skin);

        // ................................................................................ MONSTERS
        Group monsterChoice = new Group();
        monsterChoice.setSize(140, GS.HEIGHT-36);
        monsterChoice.setPosition(0,0, Align.bottomLeft);
        Image monsterChoiceBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterChoiceBg.setPosition(2,2,Align.bottomLeft);
        monsterChoice.addActor(monsterChoiceBg);

        TeamCircleWidget.ClickHandler handler = new TeamCircleWidget.ClickHandler() {
            @Override
            public void onTeamMemberButton(int position) {
                monsterStats.init(team.get(position));
                monsterImg.setDrawable(new TextureRegionDrawable(Services.getMedia().getMonsterSprite(team.get(position).ID)));
                statPent.init(team.get(position));
            }
        };

        TeamCircleWidget circle = new TeamCircleWidget(skin, team, handler);
        circle.setPosition(1,32,Align.bottomLeft);
        monsterChoice.addActor(circle);

        // .................................................................................. VALUES
        monsterStats = new MonsterStatusInventoryWidget(skin);
        monsterStats.setPosition(140+2,0,Align.bottomLeft);
        monsterStats.init(team.get(0));



        // .................................................................................. SPRITE
        Group monsterView = new Group();
        monsterView.setSize(140,GS.HEIGHT-36);
        monsterView.setPosition((140+2)*2,0,Align.bottomLeft);
        Image monsterViewBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterViewBg.setPosition(2,2,Align.bottomLeft);
        monsterView.addActor(monsterViewBg);
        monsterImg = new Image();
        monsterImg.setSize(128,128);
        monsterImg.setPosition(6,200-4,Align.topLeft);
        monsterImg.setDrawable(new TextureRegionDrawable(Services.getMedia().getMonsterSprite(team.get(0).ID)));
        monsterView.addActor(monsterImg);

        statPent = new StatusPentagonWidget();
        statPent.setSize(64,64);
        statPent.setPosition(38+2,8,Align.bottomLeft);
        monsterView.addActor(statPent);
        Image statPentagon = new Image();
        statPentagon.setSize(64,64);
        statPentagon.setPosition(38+2,8,Align.bottomLeft);
        statPentagon.setDrawable(skin.getDrawable("statPentagram"));
        monsterView.addActor(statPentagon);


        addActor(monsterChoice);
        addActor(monsterStats);
        addActor(monsterView);

        setDebug(GS.DEBUGGING_ON, true);
    }
}
